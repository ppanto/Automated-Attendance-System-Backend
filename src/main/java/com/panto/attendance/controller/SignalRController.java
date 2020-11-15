package com.panto.attendance.controller;

import com.panto.attendance.dto.attendance.AttendanceActionSimpleResponse;
import com.panto.attendance.helpers.AttendanceInsertRequest;
import com.panto.attendance.helpers.SignalRConnectionInfo;
import com.panto.attendance.helpers.SignalRMessage;
import com.panto.attendance.helpers.TerminalResponse;
import com.panto.attendance.service.AttendanceActionInsertService;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.spec.SecretKeySpec;
import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@RestController
public class SignalRController {

    private String signalRServiceKey = "rcLhEZpc8DuGPKygb9E6HKVImPFNMTRimvs6gTtlpU0=";
    private String signalRServiceBaseEndpoint = "https://attendance.service.signalr.net";
    private String hubName = "actions";

    private final AttendanceActionInsertService attendanceActionInsertService;
    @Autowired
    public SignalRController(AttendanceActionInsertService attendanceActionInsertService){
        this.attendanceActionInsertService = attendanceActionInsertService;
    }

    @GetMapping("/api/signalr/negotiate")
    public SignalRConnectionInfo negotiate() {
        String hubUrl = signalRServiceBaseEndpoint + "/client/?hub=" + hubName;
        String accessKey = generateJwt(hubUrl, null /*"12345"*/);
        return new SignalRConnectionInfo(hubUrl, accessKey);
    }

    @PostMapping("/api/attendance/insert")
    public ResponseEntity<?> insertAttendance(@RequestBody @Valid AttendanceInsertRequest request) {
        AttendanceActionSimpleResponse attendanceResponse = attendanceActionInsertService.insert(request);
        if(attendanceResponse.getId() != null){
            Thread thread = new Thread(() -> {
                sendMessageToClientsViaSignal(attendanceResponse);
            });
            thread.start();
        }

        TerminalResponse response = getTerminalResponseFromAttendance(attendanceResponse);
        return ResponseEntity.ok().body(response);
    }

    private TerminalResponse getTerminalResponseFromAttendance(AttendanceActionSimpleResponse attendance){
        TerminalResponse finalResponse = new TerminalResponse(false, null, null, null);
        finalResponse.fullName = attendance.getPersonnelName();
        if(attendance.getId() != null){
            finalResponse.isSuccessful = true;
        }
        finalResponse.messageCode = attendance.getMessageCode();
        if(finalResponse.messageCode == 1){
            finalResponse.message = "You have already started work.";
        }
        else if(finalResponse.messageCode == 2){
            finalResponse.message = "You haven't started work yet.";
        }
        else if(finalResponse.messageCode == 3){
            finalResponse.message = "You haven't finished previous action.";
        }
        else if(finalResponse.messageCode == 4){
            finalResponse.message = "Welcome.";
        }
        else if(finalResponse.messageCode == 5){
            finalResponse.message = "Have fun.";
        }
        else if(finalResponse.messageCode == 6){
            finalResponse.message = "Stay safe.";
        }
        else if(finalResponse.messageCode == 7){
            finalResponse.message = "Goodbye and see you soon.";
        }
        else if(finalResponse.messageCode == 8){
            finalResponse.message = "Welcome back.";
        }
        else{
            finalResponse.message = "Something went wrong.";
        }
        return finalResponse;
    }
    private void sendMessageToClientsViaSignal(AttendanceActionSimpleResponse response){
        String hubUrl = signalRServiceBaseEndpoint + "/api/v1/hubs/" + hubName;
        String accessKey = generateJwt(hubUrl, null);

        Unirest.post(hubUrl)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessKey)
                .body(new SignalRMessage("newMessage", new Object[] { response }))
                .asEmpty();
    }

    private String generateJwt(String audience, String userId) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        long expMillis = nowMillis + (30 * 30 * 10000);
        Date exp = new Date(expMillis);

        byte[] apiKeySecretBytes = signalRServiceKey.getBytes(StandardCharsets.UTF_8);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        JwtBuilder builder = Jwts.builder()
                .setAudience(audience)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(signingKey);

        if (userId != null) {
            builder.claim("nameid", userId);
        }

        return builder.compact();
    }
}
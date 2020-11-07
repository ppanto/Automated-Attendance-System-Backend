package com.panto.attendance.controller;

import com.panto.attendance.dto.LeaveInsertRequest;
import com.panto.attendance.dto.LeaveUpdateRequest;
import com.panto.attendance.service.LeaveService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Date;


@RestController
@RequestMapping("/api/leave")
@AllArgsConstructor
public class LeaveController {
    private final LeaveService leaveService;

    @GetMapping
    public ResponseEntity<?> getByUserForDate(@RequestParam(required = false) Long personnelId, @RequestParam(required = false) Date date){
        return ResponseEntity.ok().body(leaveService.getByUserForDate(personnelId, date));
    }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid LeaveInsertRequest leaveInsertRequest) throws Exception {
        try{
            return ResponseEntity.ok().body(leaveService.create(leaveInsertRequest));
        }
        catch(Exception ex){
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("{id}")
    public ResponseEntity<?> update(@RequestBody @Valid LeaveUpdateRequest leaveUpdateRequest) throws Exception {
        try{
            return ResponseEntity.ok().body(leaveService.update(leaveUpdateRequest));
        }
        catch(Exception ex){
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        if(leaveService.delete(id)) return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }
}

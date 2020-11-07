package com.panto.attendance.controller;

import com.panto.attendance.dto.ApplicationUserUpdateRequest;
import com.panto.attendance.service.ApplicationUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/account")
@AllArgsConstructor
public class AccountController {
    private final ApplicationUserService applicationUserService;

    @PutMapping("{id}")
    ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody ApplicationUserUpdateRequest entity){
        boolean result = applicationUserService.update(id, entity);
        return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @GetMapping("validate-login")
    ResponseEntity<?> validateLogin(){
        return ResponseEntity.ok().build();
    }

    @PutMapping("update-password")
    ResponseEntity<?> updatePassword(@RequestBody String newPassword, Principal principal){
        try {
            applicationUserService.updatePassword(principal.getName(), newPassword);
            return ResponseEntity.ok().build();
        }catch(Exception ex){
            return ResponseEntity.notFound().build();
        }
    }
}

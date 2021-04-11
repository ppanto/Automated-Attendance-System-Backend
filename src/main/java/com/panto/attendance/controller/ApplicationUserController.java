package com.panto.attendance.controller;

import com.panto.attendance.dto.ApplicationUserInsertRequest;
import com.panto.attendance.dto.ApplicationUserResponse;
import com.panto.attendance.service.ApplicationUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class ApplicationUserController {
    private final ApplicationUserService applicationUserService;

    @GetMapping("filtered/by-username")
    ResponseEntity<?> getByUsername(@RequestParam String username){
        try{
            return ResponseEntity.ok().body(applicationUserService.getApplicationUserResponse(username));
        }
        catch(Exception ex){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    ResponseEntity<?> get() {
        return ResponseEntity.ok().body(applicationUserService.get());
    }
    @PostMapping("toggleActive/{id}")
    ResponseEntity<?> toggleActive(@PathVariable Long id){
        return applicationUserService.toggleActive(id) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    @PostMapping
    ResponseEntity<?> post(@Valid @RequestBody ApplicationUserInsertRequest entity) throws Exception  {
        //return ResponseEntity.created(new URI("/api/user/" + result.getId())).body(result);
        return ResponseEntity.ok().body(applicationUserService.create(entity));
    }
    @DeleteMapping("{id}")
    ResponseEntity<?> delete(@PathVariable Long id){
        return applicationUserService.delete(id) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}

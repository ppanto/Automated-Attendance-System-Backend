package com.panto.attendance.controller;

import com.panto.attendance.dto.PersonnelResponse;
import com.panto.attendance.dto.PersonnelUpsertRequest;
import com.panto.attendance.service.PersonnelService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/personnel")
@AllArgsConstructor
public class PersonnelController {
    private final PersonnelService personnelService;

    @GetMapping
    ResponseEntity<List<PersonnelResponse>> get() {
        return ResponseEntity.ok().body(personnelService.get());
    }
    @GetMapping("filtered")
    ResponseEntity<?> getByUsername(@RequestParam String username){
        try{
            return ResponseEntity.ok().body(personnelService.get(username));
        }
        catch(Exception ex){
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("simple")
    ResponseEntity<?> getSimple() {
        return ResponseEntity.ok().body(personnelService.getSimple());
    }
    @GetMapping("simple/filtered/no-account")
    ResponseEntity<?> getSimpleFilteredNoAccount() {
        return ResponseEntity.ok().body(personnelService.getSimpleFilterNoAccount());
    }
    @PostMapping("toggleActive/{id}")
    ResponseEntity<?> toggleActive(@PathVariable Long id){
        return personnelService.toggleActive(id) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    @DeleteMapping("{id}")
    ResponseEntity<?> delete(@PathVariable Long id){
        return personnelService.delete(id) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    @DeleteMapping("/wipe")
    ResponseEntity<?> delete_all(){
        return personnelService.delete_all() ? ResponseEntity.ok().build() : ResponseEntity.status(500).build();
    }
    @PostMapping
    ResponseEntity<?> post(@Valid @RequestBody PersonnelUpsertRequest entity) throws URISyntaxException {
        PersonnelResponse result = personnelService.create(entity);
        return ResponseEntity.created(new URI("/api/personnel/" + result.getId())).body(result);
    }
    @PutMapping("{id}")
    ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody PersonnelUpsertRequest entity){
        PersonnelResponse result = personnelService.update(id, entity);
        if(result == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().body(result);
    }
    @GetMapping("image/get/{id}")
    ResponseEntity<?> get(@PathVariable Long id) {
        byte[] image = personnelService.getImage(id);
        return image != null ? ResponseEntity.ok().body(image) : ResponseEntity.ok().build();
    }
    @PostMapping("image/{id}")
    ResponseEntity<?> upload(@PathVariable Long id, @RequestParam("file")MultipartFile file) throws Exception {
        personnelService.uploadImage(id, file);
        return ResponseEntity.ok().build();
    }
}

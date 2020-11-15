package com.panto.attendance.controller;

import com.panto.attendance.dto.PersonnelShiftUpsertRequest;
import com.panto.attendance.service.PersonnelShiftService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/personnel-shift")
@AllArgsConstructor
public class PersonnelShiftController {
    private final PersonnelShiftService personnelShiftService;

    @GetMapping
    public ResponseEntity<?> get(){
        return ResponseEntity.ok().body(personnelShiftService.get());
    }
    @GetMapping("by-personnel")
    public ResponseEntity<?> getByPersonnelId(@RequestParam(required = true) Long personnelId){
        if(personnelId == null) return ResponseEntity.ok().body(personnelShiftService.get());
        else return ResponseEntity.ok().body(personnelShiftService.getByPersonnelId(personnelId));
    }
    @GetMapping("ongoing")
    public ResponseEntity<?> getAllOngoing(@RequestParam(required = true) Long personnelId){
        if(personnelId == null) return ResponseEntity.ok().body(personnelShiftService.getAllOngoing());
        else return ResponseEntity.ok().body(personnelShiftService.getAllOngoingByPersonnelId(personnelId));
    }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid PersonnelShiftUpsertRequest personnelShift){
        return ResponseEntity.ok().body(personnelShiftService.create_update(personnelShift));
    }
    @PutMapping
    public ResponseEntity<?> update(@RequestBody @Valid PersonnelShiftUpsertRequest personnelShift){
        return ResponseEntity.ok().body(personnelShiftService.create_update(personnelShift));
    }
    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        if(personnelShiftService.delete(id)) return ResponseEntity.ok().build();
        return ResponseEntity.notFound().build();
    }
}

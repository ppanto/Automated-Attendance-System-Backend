package com.panto.attendance.controller;

import com.panto.attendance.dto.ShiftTypeInsertRequest;
import com.panto.attendance.mapper.ShiftTypeMapper;
import com.panto.attendance.model.ShiftType;
import com.panto.attendance.service.ShiftTypeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shift-type")
@AllArgsConstructor
public class ShiftTypeController {
    private final ShiftTypeService shiftTypeService;

    @GetMapping
    public ResponseEntity<?> get(){
        return ResponseEntity.ok().body(shiftTypeService.get());
    }
    @GetMapping("{id}")
    ResponseEntity<?> get(@PathVariable Long id){
        try{
            return ResponseEntity.ok().body(shiftTypeService.get(id));
        }
        catch(Exception ex){
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("{id}")
    ResponseEntity<?> delete(@PathVariable Long id){
        if(shiftTypeService.deactivate(id)) return ResponseEntity.ok().build();
        return ResponseEntity.notFound().build();
    }
    @PostMapping
    ResponseEntity<?> create(@RequestBody ShiftTypeInsertRequest shiftType){
        return ResponseEntity.ok().body(shiftTypeService.create(shiftType));
    }
}

package com.panto.attendance.controller;

import com.panto.attendance.mapper.MyDateMapper;
import com.panto.attendance.model.MyDate;
import com.panto.attendance.repository.MyDateRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/calendar")
@AllArgsConstructor
public class MyDateController {
    private final MyDateRepository myDateRepository;
    private final MyDateMapper myDateMapper;

    @GetMapping("/holiday")
    public ResponseEntity<?> getHolidays(){
        return ResponseEntity.ok().body(
                myDateRepository.findByIsHoliday(true)
                .stream()
                .map(myDateMapper::mapToSimpleResponse)
                .collect(Collectors.toList())
        );
    }
    @PostMapping("/holiday")
    public ResponseEntity<?> toggleHoliday(@RequestBody Date fullDate){
        Optional<MyDate> database = myDateRepository.findById(fullDate);
        if(database.isEmpty()) return ResponseEntity.notFound().build();
        database.get().setHoliday(!database.get().isHoliday());
        MyDate result = myDateRepository.save(database.get());
        return ResponseEntity.ok().body(myDateMapper.mapToSimpleResponse(result));
    }
}

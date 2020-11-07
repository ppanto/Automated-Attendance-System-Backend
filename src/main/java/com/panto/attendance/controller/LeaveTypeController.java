package com.panto.attendance.controller;

import com.panto.attendance.model.LeaveType;
import com.panto.attendance.repository.LeaveTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leave-type")
@AllArgsConstructor
public class LeaveTypeController {
    private final LeaveTypeRepository repository;

    @GetMapping
    ResponseEntity<Collection<LeaveType>> get() {
        return ResponseEntity.ok().body(
                repository.findAll()
                        .stream()
                        .filter(LeaveType::isActive)
                        .collect(Collectors.toList())
        );
    }
    @GetMapping("{id}")
    ResponseEntity<?> get(@PathVariable Long id) {
        Optional<LeaveType> database = repository.findById(id);
        return database.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @PostMapping
    ResponseEntity<LeaveType> post(@Valid @RequestBody LeaveType entity) throws URISyntaxException {
        LeaveType result = repository.save(entity);
        return ResponseEntity.created(new URI("/api/leave-type/" + entity.getId())).body(result);
    }
    @PutMapping("{id}")
    ResponseEntity<LeaveType> update(@PathVariable Long id, @Valid @RequestBody LeaveType entity){
        Optional<LeaveType> database = repository.findById(id);
        if(database.isEmpty()) return ResponseEntity.notFound().build();
        database.get().setName(entity.getName());
        LeaveType result = repository.save(database.get());
        return ResponseEntity.ok().body(result);
    }
    @DeleteMapping("{id}")
    ResponseEntity<?> delete(@PathVariable Long id){
        Optional<LeaveType> database = repository.findById(id);
        if(database.isEmpty()) return ResponseEntity.notFound().build();
        database.get().setActive(false);
        repository.save(database.get());
        return ResponseEntity.ok().build();
    }
}

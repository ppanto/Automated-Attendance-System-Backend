package com.panto.attendance.controller;

import com.panto.attendance.model.Department;
import com.panto.attendance.repository.DepartmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/api/department")
@AllArgsConstructor
public class DepartmentController {
    private final DepartmentRepository repository;

    @GetMapping
    ResponseEntity<Collection<Department>> get() {
        return ResponseEntity.ok().body(repository.findAll());
    }
    @GetMapping("{id}")
    ResponseEntity<?> get(@PathVariable Long id) {
        Optional<Department> department = repository.findById(id);
        return department.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @PostMapping
    ResponseEntity<Department> post(@Valid @RequestBody Department entity) throws URISyntaxException {
        entity.setModifiedTime(Instant.now());
        entity.setAddedTime(Instant.now());
        Department result = repository.save(entity);
        return ResponseEntity.created(new URI("/api/department/" + result.getId())).body(result);
    }
    @PutMapping("{id}")
    ResponseEntity<Department> update(@PathVariable Long id, @Valid @RequestBody Department entity){
        Optional<Department> database = repository.findById(id);
        if(database.isEmpty()) return ResponseEntity.notFound().build();
        database.get().setName(entity.getName());
        database.get().setModifiedTime(Instant.now());
        Department result = repository.save(database.get());
        return ResponseEntity.ok().body(result);
    }
    @DeleteMapping("{id}")
    ResponseEntity<?> delete(@PathVariable Long id){
        if((repository.findById(id)).isEmpty()) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}

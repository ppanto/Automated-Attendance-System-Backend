package com.panto.attendance.controller;

import com.panto.attendance.model.Title;
import com.panto.attendance.repository.TitleRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/title")
public class TitleController {
    private TitleRepository titleRepository;

    @Autowired
    public TitleController(TitleRepository titleRepository) {
        this.titleRepository = titleRepository;
    }
    @GetMapping
    ResponseEntity<Collection<Title>> get() {
        return ResponseEntity.ok().body(titleRepository.findAll());
    }
    @GetMapping("{id}")
    ResponseEntity<?> get(@PathVariable Long id) {
        Optional<Title> title = titleRepository.findById(id);
        return title.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @PostMapping
    ResponseEntity<Title> post(@Valid @RequestBody Title title) throws URISyntaxException {
        title.setModifiedTime(Instant.now());
        title.setAddedTime(Instant.now());
        Title result = titleRepository.save(title);
        return ResponseEntity.created(new URI("/api/title" + result.getId())).body(result);
    }
    @PutMapping("{id}")
    ResponseEntity<Title> update(@PathVariable Long id, @Valid @RequestBody Title title){
        Optional<Title> databaseTitle = titleRepository.findById(id);
        if(databaseTitle.isEmpty()) return ResponseEntity.notFound().build();
        databaseTitle.get().setName(title.getName());
        databaseTitle.get().setModifiedTime(Instant.now());
        Title result = titleRepository.save(databaseTitle.get());
        return ResponseEntity.ok().body(result);
    }
    @DeleteMapping("{id}")
    ResponseEntity<?> delete(@PathVariable Long id){
        if((titleRepository.findById(id)).isEmpty()) return ResponseEntity.notFound().build();
        titleRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}

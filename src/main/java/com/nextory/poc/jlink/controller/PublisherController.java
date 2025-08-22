package com.nextory.poc.jlink.controller;

import com.nextory.poc.jlink.dto.PublisherDto;
import com.nextory.poc.jlink.service.PublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
public class PublisherController {

    private final PublisherService publisherService;

    @PostMapping
    public ResponseEntity<PublisherDto> create(@Valid @RequestBody PublisherDto dto, UriComponentsBuilder uriBuilder) {
        PublisherDto created = publisherService.create(dto);
        URI location = uriBuilder.path("/api/publishers/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublisherDto> getById(@PathVariable Long id) {
        return publisherService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<PublisherDto>> getAll(
            @PageableDefault(page = 0, size = 20) Pageable pageable,
            @RequestParam(required = false) String name) {
        Page<PublisherDto> page = publisherService.getAll(pageable, name);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PublisherDto> update(@PathVariable Long id, @Valid @RequestBody PublisherDto dto) {
        return publisherService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        publisherService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
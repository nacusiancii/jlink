package com.nextory.poc.jlink.service;

import com.nextory.poc.jlink.dto.AuthorDto;
import com.nextory.poc.jlink.model.Author;
import com.nextory.poc.jlink.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorDto create(AuthorDto dto) {
        Author author = Author.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .bio(dto.getBio())
                .build();
        Author saved = authorRepository.save(author);
        return toDto(saved);
    }

    public Optional<AuthorDto> getById(Long id) {
        return authorRepository.findById(id).map(this::toDto);
    }

    public Page<AuthorDto> getAll(Pageable pageable, String nameFilter) {
        if (nameFilter != null && !nameFilter.isBlank()) {
            return authorRepository.findAllByNameContainingIgnoreCase(nameFilter, pageable)
                    .map(this::toDto);
        }
        return authorRepository.findAll(pageable).map(this::toDto);
    }

    public Optional<AuthorDto> update(Long id, AuthorDto dto) {
        return authorRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            existing.setEmail(dto.getEmail());
            existing.setBio(dto.getBio());
            Author saved = authorRepository.save(existing);
            return toDto(saved);
        });
    }

    public void delete(Long id) {
        authorRepository.deleteById(id);
    }

    private AuthorDto toDto(Author author) {
        return AuthorDto.builder()
                .id(author.getId())
                .name(author.getName())
                .email(author.getEmail())
                .bio(author.getBio())
                .build();
    }
}
package com.nextory.poc.jlink.service;

import com.nextory.poc.jlink.dto.PublisherDto;
import com.nextory.poc.jlink.model.Publisher;
import com.nextory.poc.jlink.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PublisherService {

    private final PublisherRepository publisherRepository;

    public PublisherDto create(PublisherDto dto) {
        Publisher publisher = Publisher.builder()
                .name(dto.name())
                .website(dto.website())
                .address(dto.address())
                .build();
        Publisher saved = publisherRepository.save(publisher);
        return toDto(saved);
    }

    public Optional<PublisherDto> getById(Long id) {
        return publisherRepository.findById(id).map(this::toDto);
    }

    public Page<PublisherDto> getAll(Pageable pageable, String nameFilter) {
        if (nameFilter != null && !nameFilter.isBlank()) {
            return publisherRepository.findAllByNameContainingIgnoreCase(nameFilter, pageable)
                    .map(this::toDto);
        }
        return publisherRepository.findAll(pageable).map(this::toDto);
    }

    public Optional<PublisherDto> update(Long id, PublisherDto dto) {
        return publisherRepository.findById(id).map(existing -> {
            existing.setName(dto.name());
            existing.setWebsite(dto.website());
            existing.setAddress(dto.address());
            Publisher saved = publisherRepository.save(existing);
            return toDto(saved);
        });
    }

    public void delete(Long id) {
        publisherRepository.deleteById(id);
    }

    private PublisherDto toDto(Publisher publisher) {
        return new PublisherDto(publisher.getId(), publisher.getName(), publisher.getWebsite(), publisher.getAddress());
    }
}
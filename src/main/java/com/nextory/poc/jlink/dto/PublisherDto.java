package com.nextory.poc.jlink.dto;

/**
 * DTO for Publisher entity.
 */
public record PublisherDto(Long id,
                           String name,
                           String website,
                           String address) {
}
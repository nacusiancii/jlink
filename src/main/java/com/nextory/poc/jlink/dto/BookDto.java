package com.nextory.poc.jlink.dto;

/**
 * DTO for Book entity.
 */
public record BookDto(Long id,
                      String title,
                      String isbn,
                      java.time.LocalDate publicationDate,
                      java.math.BigDecimal price,
                      Long authorId,
                      Long publisherId) {
}
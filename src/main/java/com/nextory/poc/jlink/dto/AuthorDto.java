package com.nextory.poc.jlink.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Author entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorDto {
    private Long id;
    private String name;
    private String email;
    private String bio;
}
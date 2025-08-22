package com.nextory.poc.jlink.repository;

import com.nextory.poc.jlink.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    // Find authors by name fragment (case insensitive) with pagination
    Page<Author> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
}
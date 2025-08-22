package com.nextory.poc.jlink.repository;

import com.nextory.poc.jlink.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    // Additional query methods can be defined here if needed
}
package com.nextory.poc.jlink.repository;

import com.nextory.poc.jlink.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    // Additional query methods can be defined here if needed
}
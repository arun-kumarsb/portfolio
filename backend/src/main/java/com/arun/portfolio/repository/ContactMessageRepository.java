package com.arun.portfolio.repository;

import com.arun.portfolio.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for ContactMessage entities.
 */
@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
}

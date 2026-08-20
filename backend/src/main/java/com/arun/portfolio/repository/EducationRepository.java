package com.arun.portfolio.repository;

import com.arun.portfolio.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for Education entities.
 */
@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
}

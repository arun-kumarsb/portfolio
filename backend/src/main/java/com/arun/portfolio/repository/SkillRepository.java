package com.arun.portfolio.repository;

import com.arun.portfolio.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for Skill entities.
 */
@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByCategoryIgnoreCase(String category);
}

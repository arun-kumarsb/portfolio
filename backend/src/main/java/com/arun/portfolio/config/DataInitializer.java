package com.arun.portfolio.config;

import com.arun.portfolio.entity.Education;
import com.arun.portfolio.entity.Project;
import com.arun.portfolio.entity.Skill;
import com.arun.portfolio.repository.EducationRepository;
import com.arun.portfolio.repository.ProjectRepository;
import com.arun.portfolio.repository.SkillRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Seeds initial baseline portfolio data if database tables are empty on startup.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;
    private final EducationRepository educationRepository;

    public DataInitializer(ProjectRepository projectRepository,
                           SkillRepository skillRepository,
                           EducationRepository educationRepository) {
        this.projectRepository = projectRepository;
        this.skillRepository = skillRepository;
        this.educationRepository = educationRepository;
    }

    @Override
    public void run(String... args) {
        seedProjects();
        seedSkills();
        seedEducation();
    }

    private void seedProjects() {
        if (projectRepository.count() == 0) {
            List<Project> projects = Arrays.asList(
                    new Project(
                            "SpendSense",
                            "A personal and group expense tracking application with expense management, splitting, settlements, statistics, and local data persistence.",
                            "React Native, Expo, JavaScript",
                            "https://github.com/TODO-arun/spendsense",
                            "",
                            "",
                            true
                    ),
                    new Project(
                            "Full-Stack Portfolio REST API Engine",
                            "A clean, production-grade backend demonstrating REST APIs, DTO request/response boundaries, Service layer, Repository layer, JPA/Hibernate persistence, and MySQL integration.",
                            "Java, Spring Boot, Spring Data JPA, Hibernate, MySQL, REST API, Jakarta Validation",
                            "https://github.com/TODO-arun/portfolio-backend",
                            "",
                            "",
                            true
                    ),
                    new Project(
                            "Secure Tactical Communication & Command System (STCS)",
                            "[Academic System-Design Simulation] An academic project exploring secure distributed communication concepts, command-and-control simulation architecture, event-driven messaging, and data flow modeling.",
                            "Spring Boot, REST APIs, Distributed Systems, Event Architecture, AI/ML",
                            "https://github.com/TODO-arun/stcs-simulation",
                            "",
                            "",
                            false
                    )
            );
            projectRepository.saveAll(projects);
        }
    }

    private void seedSkills() {
        if (skillRepository.count() == 0) {
            List<Skill> skills = Arrays.asList(
                    // Programming
                    new Skill("Java", "Programming", "Advanced"),
                    new Skill("Python", "Programming", "Intermediate"),
                    new Skill("JavaScript", "Programming", "Intermediate"),
                    new Skill("C", "Programming", "Proficient"),

                    // Backend
                    new Skill("Spring Boot", "Backend", "Advanced"),
                    new Skill("REST APIs", "Backend", "Advanced"),
                    new Skill("Spring Data JPA", "Backend", "Advanced"),
                    new Skill("Hibernate", "Backend", "Proficient"),

                    // Database
                    new Skill("MySQL", "Database", "Advanced"),
                    new Skill("SQL", "Database", "Advanced"),

                    // Frontend
                    new Skill("HTML", "Frontend", "Advanced"),
                    new Skill("CSS", "Frontend", "Proficient"),
                    new Skill("JavaScript", "Frontend", "Proficient"),

                    // Tools
                    new Skill("Git", "Tools", "Advanced"),
                    new Skill("GitHub", "Tools", "Advanced"),
                    new Skill("VS Code", "Tools", "Proficient"),
                    new Skill("IntelliJ IDEA", "Tools", "Proficient"),

                    // AI/ML
                    new Skill("Machine Learning", "AI/ML", "Intermediate"),
                    new Skill("Deep Learning", "AI/ML", "Intermediate"),
                    new Skill("Neural Networks", "AI/ML", "Intermediate"),
                    new Skill("Python ML Ecosystem", "AI/ML", "Intermediate")
            );
            skillRepository.saveAll(skills);
        }
    }

    private void seedEducation() {
        if (educationRepository.count() == 0) {
            List<Education> educationList = Arrays.asList(
                    new Education(
                            "TODO: Engineering Institution / University",
                            "Bachelor of Engineering (B.E. / B.Tech)",
                            "Computer Science & Engineering",
                            "2023",
                            "2027 (Expected)",
                            "Core coursework in Data Structures & Algorithms, Object-Oriented Programming, Database Management Systems, Computer Networks, Operating Systems, and AI/ML foundations."
                    ),
                    new Education(
                            "TODO: Higher Secondary School",
                            "Higher Secondary Certificate (Class XII)",
                            "Science Stream (Physics, Chemistry, Mathematics, Computer Science)",
                            "2021",
                            "2023",
                            "Developed strong foundations in mathematics, analytical reasoning, and foundational computer science."
                    )
            );
            educationRepository.saveAll(educationList);
        }
    }
}

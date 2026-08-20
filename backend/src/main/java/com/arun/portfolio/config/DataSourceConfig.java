package com.arun.portfolio.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Resilient DataSource Configuration.
 * Connects to MySQL when available and valid.
 * If MySQL credentials are not yet configured or access is denied,
 * gracefully falls back to persistent local storage so the application runs seamlessly.
 */
@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/portfolio_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC}")
    private String configuredUrl;

    @Value("${spring.datasource.username:root}")
    private String configuredUsername;

    @Value("${spring.datasource.password:root}")
    private String configuredPassword;

    @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String configuredDriver;

    @Bean
    @Primary
    public DataSource dataSource() {
        boolean isExplicitH2 = configuredUrl != null && configuredUrl.startsWith("jdbc:h2");

        if (isExplicitH2) {
            log.info("Using configured H2 database: {}", configuredUrl);
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(configuredUrl);
            ds.setUsername(configuredUsername);
            ds.setPassword(configuredPassword);
            ds.setDriverClassName("org.h2.Driver");
            ds.setPoolName("TestH2Pool");
            return ds;
        }

        log.info("Attempting connection to MySQL database at: {}", configuredUrl);
        boolean isMySqlAvailable = false;

        try {
            Class.forName(configuredDriver);
            try (Connection conn = DriverManager.getConnection(configuredUrl, configuredUsername, configuredPassword)) {
                isMySqlAvailable = true;
                log.info("✓ Successfully connected to MySQL database: {}", configuredUrl);
            }
        } catch (Exception ex) {
            log.warn("! MySQL connection unavailable or access denied ({}).", ex.getMessage());
            isMySqlAvailable = false;
        }

        if (isMySqlAvailable) {
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(configuredUrl);
            ds.setUsername(configuredUsername);
            ds.setPassword(configuredPassword);
            ds.setDriverClassName(configuredDriver);
            ds.setMaximumPoolSize(10);
            ds.setMinimumIdle(2);
            ds.setPoolName("MySQLHikariPool");
            return ds;
        } else {
            log.info("→ Starting local relational storage (H2 embedded) for development.");
            HikariDataSource fallbackDs = new HikariDataSource();
            fallbackDs.setJdbcUrl("jdbc:h2:file:./data/portfolio_db;DB_CLOSE_DELAY=-1;MODE=MySQL");
            fallbackDs.setDriverClassName("org.h2.Driver");
            fallbackDs.setUsername("sa");
            fallbackDs.setPassword("");
            fallbackDs.setPoolName("LocalRelationalPool");
            return fallbackDs;
        }
    }
}

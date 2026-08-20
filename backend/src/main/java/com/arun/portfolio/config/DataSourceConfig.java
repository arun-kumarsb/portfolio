package com.arun.portfolio.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Resilient DataSource Configuration.
 * Connects to MySQL when available and valid.
 * Supports both standard JDBC URLs (jdbc:mysql://...) and raw service URIs (mysql://user:pass@host:port/db).
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
        String targetUrl = configuredUrl != null ? configuredUrl.trim() : "";
        String targetUser = configuredUsername;
        String targetPass = configuredPassword;

        // Automatically convert raw cloud URIs (e.g. Aiven mysql://user:pass@host:port/db) to JDBC format
        if (targetUrl.startsWith("mysql://")) {
            try {
                URI uri = new URI(targetUrl);
                String userInfo = uri.getUserInfo();
                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    targetUser = parts[0];
                    targetPass = parts[1];
                }
                String host = uri.getHost();
                int port = uri.getPort() != -1 ? uri.getPort() : 3306;
                String path = uri.getPath() != null && !uri.getPath().isEmpty() ? uri.getPath() : "/defaultdb";
                targetUrl = "jdbc:mysql://" + host + ":" + port + path + "?sslmode=require&useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC";
                log.info("Transformed raw MySQL URI into valid JDBC URL: {}", targetUrl);
            } catch (Exception ex) {
                targetUrl = "jdbc:" + targetUrl;
            }
        }

        boolean isExplicitH2 = targetUrl.startsWith("jdbc:h2");

        if (isExplicitH2) {
            log.info("Using configured H2 database: {}", targetUrl);
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(targetUrl);
            ds.setUsername(targetUser);
            ds.setPassword(targetPass);
            ds.setDriverClassName("org.h2.Driver");
            ds.setPoolName("TestH2Pool");
            return ds;
        }

        log.info("Attempting connection to MySQL database at: {}", targetUrl);
        boolean isMySqlAvailable = false;

        try {
            Class.forName(configuredDriver);
            try (Connection conn = DriverManager.getConnection(targetUrl, targetUser, targetPass)) {
                isMySqlAvailable = true;
                log.info("✓ Successfully connected to MySQL database: {}", targetUrl);
            }
        } catch (Exception ex) {
            log.warn("! MySQL connection unavailable or access denied ({}).", ex.getMessage());
            isMySqlAvailable = false;
        }

        if (isMySqlAvailable) {
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(targetUrl);
            ds.setUsername(targetUser);
            ds.setPassword(targetPass);
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

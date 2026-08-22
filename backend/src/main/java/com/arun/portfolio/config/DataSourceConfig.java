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
 * Connects to PostgreSQL (Render / Cloud / Local) when available and valid.
 * Supports both standard JDBC URLs (jdbc:postgresql://...) and raw service URIs (postgres://user:pass@host:port/db).
 * If external credentials are not yet configured or connection fails,
 * gracefully falls back to persistent local storage so the application runs seamlessly.
 */
@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url:jdbc:postgresql://dpg-da4ls5on74is73e0rsvg-a:5432/portfolio_db_mvyr}")
    private String configuredUrl;

    @Value("${spring.datasource.username:arun}")
    private String configuredUsername;

    @Value("${spring.datasource.password:arun}")
    private String configuredPassword;

    @Value("${spring.datasource.driver-class-name:org.postgresql.Driver}")
    private String configuredDriver;

    @Bean
    @Primary
    public DataSource dataSource() {
        String targetUrl = configuredUrl != null ? configuredUrl.trim() : "";
        String targetUser = configuredUsername;
        String targetPass = configuredPassword;

        // If URL has user:pass embedded in jdbc: format, normalize for parsing
        if (targetUrl.startsWith("jdbc:postgresql://") && targetUrl.contains("@")) {
            targetUrl = targetUrl.substring(5);
        } else if (targetUrl.startsWith("jdbc:mysql://") && targetUrl.contains("@")) {
            targetUrl = targetUrl.substring(5);
        }

        // Automatically convert raw cloud URIs (e.g. Render/Supabase postgres://user:pass@host:port/db) to JDBC format
        if (targetUrl.startsWith("postgres://") || targetUrl.startsWith("postgresql://")) {
            try {
                String cleanUri = targetUrl.startsWith("postgres://") ? targetUrl.replaceFirst("postgres://", "postgresql://") : targetUrl;
                URI uri = new URI(cleanUri);
                String userInfo = uri.getUserInfo();
                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    targetUser = parts[0];
                    targetPass = parts[1];
                }
                String host = uri.getHost();
                int port = uri.getPort() != -1 ? uri.getPort() : 5432;
                String path = (uri.getPath() != null && !uri.getPath().isEmpty() && !uri.getPath().equals("/")) ? uri.getPath() : "/portfolio_db_mvyr";
                if (!path.startsWith("/")) path = "/" + path;
                targetUrl = "jdbc:postgresql://" + host + ":" + port + path;
                if (!targetUrl.contains("sslmode") && host != null && !host.contains("localhost") && !host.contains("127.0.0.1") && !host.equals("dpg-da4ls5on74is73e0rsvg-a")) {
                    targetUrl += "?sslmode=require";
                }
                log.info("Transformed raw PostgreSQL URI into valid JDBC URL: {}", targetUrl);
            } catch (Exception ex) {
                targetUrl = "jdbc:" + targetUrl;
            }
        } else if (targetUrl.startsWith("mysql://")) {
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
                String path = uri.getPath() != null && !uri.getPath().isEmpty() ? uri.getPath() : "/portfolio_db";
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

        // Determine driver class name dynamically if not explicitly matching
        String effectiveDriver = configuredDriver;
        if (targetUrl.startsWith("jdbc:postgresql:")) {
            effectiveDriver = "org.postgresql.Driver";
        } else if (targetUrl.startsWith("jdbc:mysql:")) {
            effectiveDriver = "com.mysql.cj.jdbc.Driver";
        }

        log.info("Attempting connection to database at: {}", targetUrl);
        boolean isDbAvailable = false;

        try {
            Class.forName(effectiveDriver);
            try (Connection conn = DriverManager.getConnection(targetUrl, targetUser, targetPass)) {
                isDbAvailable = true;
                log.info("✓ Successfully connected to database: {}", targetUrl);
            }
        } catch (Exception ex) {
            log.warn("! Primary database unavailable or access denied ({}).", ex.getMessage());
            isDbAvailable = false;
        }

        if (isDbAvailable) {
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(targetUrl);
            ds.setUsername(targetUser);
            ds.setPassword(targetPass);
            ds.setDriverClassName(effectiveDriver);
            ds.setMaximumPoolSize(10);
            ds.setMinimumIdle(2);
            ds.setPoolName("PrimaryHikariPool");
            return ds;
        } else {
            log.info("→ Starting local relational storage (H2 embedded in PostgreSQL mode) for seamless development.");
            HikariDataSource fallbackDs = new HikariDataSource();
            fallbackDs.setJdbcUrl("jdbc:h2:file:./data/portfolio_db;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE");
            fallbackDs.setDriverClassName("org.h2.Driver");
            fallbackDs.setUsername("sa");
            fallbackDs.setPassword("");
            fallbackDs.setPoolName("LocalRelationalPool");
            return fallbackDs;
        }
    }
}

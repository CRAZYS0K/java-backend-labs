package com.sokolov.labs.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;

public class DatabaseConfig {
    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);
    private final HikariDataSource dataSource;

    public DatabaseConfig() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("application.yml")) {
            if (in == null) {
                throw new RuntimeException("application.yml not found in resources");
            }
            Yaml yaml = new Yaml();
            Map<String, Map<String, String>> config = yaml.load(in);
            Map<String, String> dbConfig = config.get("db");

            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(dbConfig.get("url"));
            hikariConfig.setUsername(dbConfig.get("user"));
            hikariConfig.setPassword(dbConfig.get("password"));

            this.dataSource = new HikariDataSource(hikariConfig);
        } catch (Exception e) {
            log.error("Failed to load database configuration", e);
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    public void runMigrations() {
        try (Connection conn = dataSource.getConnection(); java.sql.Statement stmt = conn.createStatement()) {
            log.info("Creating schema if not exists");
            stmt.execute("CREATE SCHEMA IF NOT EXISTS company_schema;");
        } catch (java.sql.SQLException e) {
            log.error("Failed to create schema", e);
            throw new RuntimeException("Failed to create schema", e);
        }

        // 2. Теперь запускаем Liquibase
        try (Connection connection = dataSource.getConnection()) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

            database.setDefaultSchemaName("company_schema");
            database.setLiquibaseSchemaName("company_schema");

            log.info("Starting Liquibase migrations");
            try (Liquibase liquibase = new Liquibase("db/changelog/db.changelog-master.xml", new ClassLoaderResourceAccessor(), database)) {
                liquibase.update("");
                log.info("Liquibase migrations executed successfully!");
            }
        } catch (Exception e) {
            log.error("Failed to execute migrations", e);
            throw new RuntimeException("Migration failed", e);
        }
    }
}
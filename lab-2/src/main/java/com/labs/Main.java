package com.labs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.labs.model.CompanyEntity;
import com.labs.repository.CompanyRepository;
import com.labs.repository.JdbcCompanyRepository;
import com.labs.util.DatabaseConfig;

/*
Company: id (int), name (String), employeesCount (int)
*/

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Starting Lab 2");

        DatabaseConfig config = new DatabaseConfig();
        config.runMigrations();

        CompanyRepository repository = new JdbcCompanyRepository(config.getDataSource());

        CompanyEntity yandex = new CompanyEntity("Yandex", 12000);
        int savedId = repository.save(yandex);
        log.info("Saved company with ID: {}", savedId);

        CompanyEntity foundById = repository.findById(savedId).orElse(null);
        log.info("Found by ID: {}", foundById);

        CompanyEntity foundByName = repository.findByName("Yandex").orElse(null);
        log.info("Found by Name: {}", foundByName);

        if (foundById != null) {
            foundById.setEmployeesCount(15000);
            boolean isUpdated = repository.update(foundById);
            log.info("Update successful: {}", isUpdated);
        }

        log.info("All companies: {}", repository.findAll());

        repository.deleteById(savedId);
        log.info("Deleted company with ID: {}", savedId);

        repository.deleteById(savedId);
    }
}
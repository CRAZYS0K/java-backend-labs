package com.labs.service;

import com.labs.exception.EntityNotFoundException;
import com.labs.model.CompanyEntity;
import com.labs.repository.CompanyRepository;

import java.util.List;

public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository repository;

    public CompanyServiceImpl(CompanyRepository repository) {
        this.repository = repository;
    }

    @Override
    public int save(String name, int employeesCount) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Company name must not be empty");
        }
        if (employeesCount < 0) {
            throw new IllegalArgumentException("Employees count must be positive");
        }

        CompanyEntity entity = new CompanyEntity(name, employeesCount);
        return repository.save(entity);
    }

    @Override
    public CompanyEntity findById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Company not found with id: " + id));
    }

    @Override
    public CompanyEntity findByName(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Company not found with name: " + name));
    }

    @Override
    public List<CompanyEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public void update(CompanyEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }

        repository.findById(entity.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cannot update: company not found with id: " + entity.getId()));

        boolean updated = repository.update(entity);
        if (!updated) {
            throw new EntityNotFoundException(
                    "Update failed: company with id " + entity.getId() + " no longer exists");
        }
    }

    @Override
    public void deleteById(int id) {
        repository.deleteById(id);
    }
}

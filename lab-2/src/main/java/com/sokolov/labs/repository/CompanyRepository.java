package com.sokolov.labs.repository;

import com.sokolov.labs.model.CompanyEntity;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository {
    int save(CompanyEntity entity);

    Optional<CompanyEntity> findById(int id);

    Optional<CompanyEntity> findByName(String name);

    List<CompanyEntity> findAll();

    boolean update(CompanyEntity entity);

    void deleteById(int id);
}
package com.sokolov.labs.repository;

import com.sokolov.labs.model.CompanyEntity;

import java.util.List;

public interface CompanyRepository {
    int save(CompanyEntity entity);

    CompanyEntity findById(int id);

    CompanyEntity findByName(String name);

    List<CompanyEntity> findAll();

    boolean update(CompanyEntity entity);

    void deleteById(int id);
}

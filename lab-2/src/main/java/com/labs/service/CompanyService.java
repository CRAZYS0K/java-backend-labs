package com.labs.service;

import com.labs.model.CompanyEntity;

import java.util.List;

public interface CompanyService {

    int save(String name, int employeesCount);

    CompanyEntity findById(int id);

    CompanyEntity findByName(String name);

    List<CompanyEntity> findAll();

    void update(CompanyEntity entity);

    void deleteById(int id);
}

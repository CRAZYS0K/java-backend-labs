package com.sokolov.labs.model;

public class CompanyEntity {
    private Integer id;
    private String name;
    private Integer employeesCount;

    public CompanyEntity() {
    }

    public CompanyEntity(String name, Integer employeesCount) {
        this.name = name;
        this.employeesCount = employeesCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getEmployeesCount() {
        return employeesCount;
    }

    public void setEmployeesCount(Integer employeesCount) {
        this.employeesCount = employeesCount;
    }

    @Override
    public String toString() {
        return "CompanyEntity{id=" + id + ", name='" + name + "', employeesCount=" + employeesCount + "}";
    }
}

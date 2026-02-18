package com.sokolov.labs.model;

import java.util.Objects;

public class User {
    private Integer id;
    private String name;
    private String status;
    private String species;
    private String type;
    private String gender;
    private String origin;
    private String location;
    private String created;

    public User(Integer id, String name, String status, String species, String type,
                String gender, String origin, String location, String created) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.species = species;
        this.type = type;
        this.gender = gender;
        this.origin = origin;
        this.location = location;
        this.created = created;
    }

    // Геттеры
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getSpecies() {
        return species;
    }

    public String getType() {
        return type;
    }

    public String getGender() {
        return gender;
    }

    public String getOrigin() {
        return origin;
    }

    public String getLocation() {
        return location;
    }

    public String getCreated() {
        return created;
    }

    // Сеттеры
    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(name, user.name) &&
                Objects.equals(status, user.status) &&
                Objects.equals(species, user.species) &&
                Objects.equals(type, user.type) &&
                Objects.equals(gender, user.gender) &&
                Objects.equals(origin, user.origin) &&
                Objects.equals(location, user.location) &&
                Objects.equals(created, user.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status, species, type, gender, origin, location, created);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", species='" + species + '\'' +
                ", type='" + type + '\'' +
                ", gender='" + gender + '\'' +
                ", origin='" + origin + '\'' +
                ", location='" + location + '\'' +
                ", created='" + created + '\'' +
                '}';
    }
}
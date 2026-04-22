package com.labs.repository;

import com.zaxxer.hikari.HikariDataSource;
import com.labs.exception.RepositoryException;
import com.labs.model.CompanyEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCompanyRepository implements CompanyRepository {

    private final HikariDataSource dataSource;

    private static final String SAVE_SQL = "INSERT INTO company (name, employees_count) VALUES (?, ?)";
    private static final String FIND_BY_ID_SQL = "SELECT id, name, employees_count FROM company WHERE id = ?";
    private static final String FIND_BY_NAME_SQL = "SELECT id, name, employees_count FROM company WHERE name = ?";
    private static final String FIND_ALL_SQL = "SELECT id, name, employees_count FROM company";
    private static final String UPDATE_SQL = "UPDATE company SET name = ?, employees_count = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM company WHERE id = ?";

    public JdbcCompanyRepository(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int save(CompanyEntity entity) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getName());
            ps.setInt(2, entity.getEmployeesCount());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    entity.setId(id);
                    return id;
                }
                throw new RepositoryException("Saving failed, no ID obtained.", null);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error saving company", e);
        }
    }

    @Override
    public Optional<CompanyEntity> findById(int id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToEntity(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error finding company by id: " + id, e);
        }
    }

    @Override
    public Optional<CompanyEntity> findByName(String name) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_NAME_SQL)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToEntity(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error finding company by name", e);
        }
    }

    @Override
    public List<CompanyEntity> findAll() {
        List<CompanyEntity> companies = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                companies.add(mapRowToEntity(rs));
            }
            return companies;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding all companies", e);
        }
    }

    @Override
    public boolean update(CompanyEntity entity) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, entity.getName());
            ps.setInt(2, entity.getEmployeesCount());
            ps.setInt(3, entity.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error updating company", e);
        }
    }

    @Override
    public void deleteById(int id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting company", e);
        }
    }

    private CompanyEntity mapRowToEntity(ResultSet rs) throws SQLException {
        CompanyEntity entity = new CompanyEntity();
        entity.setId(rs.getInt("id"));
        entity.setName(rs.getString("name"));
        entity.setEmployeesCount(rs.getInt("employees_count"));
        return entity;
    }
}
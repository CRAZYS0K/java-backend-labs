package com.sokolov.labs.repository;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sokolov.labs.model.CompanyEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcCompanyRepository implements CompanyRepository {

    private static final Logger log = LoggerFactory.getLogger(JdbcCompanyRepository.class);
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
            }
        } catch (SQLException e) {
            log.error("Error saving company", e);
        }
        return -1;
    }

    @Override
    public CompanyEntity findById(int id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID_SQL)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToEntity(rs);
                }
            }
        } catch (SQLException e) {
            log.error("Error finding company by id", e);
        }
        return null;
    }

    @Override
    public CompanyEntity findByName(String name) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_NAME_SQL)) {

            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToEntity(rs);
                }
            }
        } catch (SQLException e) {
            log.error("Error finding company by name", e);
        }
        return null;
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
        } catch (SQLException e) {
            log.error("Error finding all companies", e);
        }
        return companies;
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
            log.error("Error updating company", e);
        }
        return false;
    }

    @Override
    public void deleteById(int id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting company", e);
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
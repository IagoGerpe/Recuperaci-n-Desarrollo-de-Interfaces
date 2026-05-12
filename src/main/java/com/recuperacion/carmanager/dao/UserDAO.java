package com.recuperacion.carmanager.dao;

import com.recuperacion.carmanager.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public boolean existsByUsername(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException exception) {
            System.out.println("Error comprobando el nombre de usuario: " + exception.getMessage());
            return false;
        }
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException exception) {
            System.out.println("Error al comprobar el email: " + exception.getMessage());
            return false;
        }
    }

    public boolean save(User user) {
        String sql = """
                INSERT INTO users (username, email, password, role)
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getRole());

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            System.out.println("Error al guardar el usuario: " + exception.getMessage());
            return false;
        }
    }

    public User findByUsername(String username) {
        String sql = """
                SELECT id, username, email, password, role
                FROM users
                WHERE username = ?
                """;

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }

        } catch (SQLException exception) {
            System.out.println("Error al buscar el usuario: " + exception.getMessage());
        }

        return null;
    }

    public List<User> findAll() {
        String sql = """
                SELECT id, username, email, password, role
                FROM users
                ORDER BY username
                """;

        List<User> users = new ArrayList<>();

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }

        } catch (SQLException exception) {
            System.out.println("Error al obtener los usuarios: " + exception.getMessage());
        }

        return users;
    }

    public List<User> findByEmail(String emailFilter) {
        String sql = """
                SELECT id, username, email, password, role
                FROM users
                WHERE email LIKE ?
                ORDER BY username
                """;

        List<User> users = new ArrayList<>();

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, "%" + emailFilter + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(mapResultSetToUser(resultSet));
                }
            }

        } catch (SQLException exception) {
            System.out.println("Error al filtrar usuarios por email: " + exception.getMessage());
        }

        return users;
    }

    public boolean updateRole(int userId, String role) {
        String sql = """
                UPDATE users
                SET role = ?
                WHERE id = ?
                """;

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, role);
            statement.setInt(2, userId);

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            System.out.println("Error al actualizar el rol del usuario: " + exception.getMessage());
            return false;
        }
    }

    public boolean deleteById(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            System.out.println("Error al eliminar el usuario: " + exception.getMessage());
            return false;
        }
    }

    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getInt("id"),
                resultSet.getString("username"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                resultSet.getString("role")
        );
    }
}
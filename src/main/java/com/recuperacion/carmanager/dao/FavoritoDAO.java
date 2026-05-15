package com.recuperacion.carmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.recuperacion.carmanager.model.Leaderboard;
import com.recuperacion.carmanager.model.User;

import java.util.ArrayList;
import java.util.List;

public class FavoritoDAO {

    public int findFavoriteCarIdByUserId(int userId) {
        String sql = """
                SELECT car_id
                FROM favorites
                WHERE user_id = ?
                """;

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("car_id");
                }
            }

        } catch (SQLException exception) {
            System.out.println("Error al obtener el coche favorito del usuario: " + exception.getMessage());
        }

        return -1;
    }

    public boolean setFavoriteCar(int userId, int carId) {
        String sql = """
                INSERT INTO favorites (user_id, car_id)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE car_id = ?
                """;

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);
            statement.setInt(2, carId);
            statement.setInt(3, carId);

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            System.out.println("Error al marcar coche favorito: " + exception.getMessage());
            return false;
        }
    }

    public int findMostFavoriteCarId() {
        String sql = """
                SELECT car_id, COUNT(*) AS total
                FROM favorites
                GROUP BY car_id
                ORDER BY total DESC, car_id ASC
                LIMIT 1
                """;

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            if (resultSet.next()) {
                return resultSet.getInt("car_id");
            }

        } catch (SQLException exception) {
            System.out.println("Error al obtener el coche favorito: " + exception.getMessage());
        }

        return -1;
    }

    public int countFavoritesByCarId(int carId) {
        String sql = """
                SELECT COUNT(*) AS total
                FROM favorites
                WHERE car_id = ?
                """;

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, carId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("total");
                }
            }

        } catch (SQLException exception) {
            System.out.println("Error al contar favoritos del coche: " + exception.getMessage());
        }

        return 0;
    }

    public List<Leaderboard> findLeaderboardEntries() {
        String sql = """
                SELECT c.id,
                       c.brand,
                       c.model,
                       c.image_path,
                       COUNT(f.id) AS votes
                FROM cars c
                LEFT JOIN favorites f ON f.car_id = c.id
                GROUP BY c.id, c.brand, c.model, c.image_path
                ORDER BY votes DESC, c.brand ASC, c.model ASC
                """;

        List<Leaderboard> leaderboardEntries = new ArrayList<>();

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            int position = 1;

            while (resultSet.next()) {
                String carName = resultSet.getString("brand") + " " + resultSet.getString("model");

                Leaderboard entry = new Leaderboard(
                        position,
                        resultSet.getInt("id"),
                        carName,
                        resultSet.getString("image_path"),
                        resultSet.getInt("votes")
                );

                leaderboardEntries.add(entry);
                position++;
            }

        } catch (SQLException exception) {
            System.out.println("Error al obtener la clasificación: " + exception.getMessage());
        }

        return leaderboardEntries;
    }

    public int findRankingPositionByCarId(int carId) {
        List<Leaderboard> entries = findLeaderboardEntries();

        for (Leaderboard entry : entries) {
            if (entry.getCarId() == carId) {
                return entry.getPosition();
            }
        }

        return -1;
    }

    public List<User> findUsersByFavoriteCarId(int carId) {
        String sql = """
            SELECT u.id, u.username, u.email, u.password, u.role
            FROM users u
            INNER JOIN favorites f ON f.user_id = u.id
            WHERE f.car_id = ?
            ORDER BY u.username
            """;

        List<User> users = new ArrayList<>();

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, carId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    User user = new User(
                            resultSet.getInt("id"),
                            resultSet.getString("username"),
                            resultSet.getString("email"),
                            resultSet.getString("password"),
                            resultSet.getString("role")
                    );

                    users.add(user);
                }
            }

        } catch (SQLException exception) {
            System.out.println("Error al obtener usuarios favoritos del coche :" + exception.getMessage());
        }

        return users;
    }
}

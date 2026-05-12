package com.recuperacion.carmanager.dao;

import com.recuperacion.carmanager.model.Car;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarDAO {

    private Car mapResultSetToCar(ResultSet resultSet) throws SQLException {
        Date registrationSqlDate = resultSet.getDate("registration_date");

        return new Car(
                resultSet.getInt("id"),
                resultSet.getString("brand"),
                resultSet.getString("model"),
                resultSet.getInt("horse_power"),
                resultSet.getString("car_type"),
                registrationSqlDate.toLocalDate(),
                resultSet.getString("image_path")
        );
    }

    public List<Car> findAll() {
        String sql = """
                SELECT id, brand, model, horse_power, car_type, registration_date, image_path
                FROM cars
                ORDER BY brand, model
                """;

        List<Car> cars = new ArrayList<>();

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                cars.add(mapResultSetToCar(resultSet));
            }

        } catch (SQLException exception) {
            System.out.println("Error al obtener los coches: " + exception.getMessage());
        }

        return cars;
    }

    public List<Car> findByType(String carType) {
        String sql = """
                SELECT id, brand, model, horse_power, car_type, registration_date, image_path
                FROM cars
                WHERE car_type = ?
                ORDER BY brand, model
                """;

        List<Car> cars = new ArrayList<>();

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, carType);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    cars.add(mapResultSetToCar(resultSet));
                }
            }

        } catch (SQLException exception) {
            System.out.println("Error al filtrar los coches: " + exception.getMessage());
        }

        return cars;
    }

    public List<String> findAllCarTypes() {
        String sql = """
                SELECT DISTINCT car_type
                FROM cars
                ORDER BY car_type
                """;

        List<String> carTypes = new ArrayList<>();

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                carTypes.add(resultSet.getString("car_type"));
            }

        } catch (SQLException exception) {
            System.out.println("Error al obtener los tipos de coche: " + exception.getMessage());
        }

        return carTypes;
    }
    public boolean save(Car car) {
        String sql = """
                INSERT INTO cars (brand, model, horse_power, car_type, registration_date, image_path)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        )
        {
            statement.setString(1, car.getBrand());
            statement.setString(2, car.getModel());
            statement.setInt(3, car.getHorsePower());
            statement.setString(4, car.getCarType());
            statement.setDate(5, Date.valueOf(car.getRegistrationDate()));
            statement.setString(6, car.getImagePath());

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            System.out.println("Error al guarda+r el coche: " + exception.getMessage());
            return false;
        }

    }

    public boolean update(Car car) {
        String sql = """
                UPDATE cars
                SET brand = ?,
                    model = ?,
                    horse_power = ?,
                    car_type = ?,
                    registration_date = ?,
                    image_path = ?
                WHERE id = ?
                """;

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, car.getBrand());
            statement.setString(2, car.getModel());
            statement.setInt(3, car.getHorsePower());
            statement.setString(4, car.getCarType());
            statement.setDate(5, Date.valueOf(car.getRegistrationDate()));
            statement.setString(6, car.getImagePath());
            statement.setInt(7, car.getId());

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            System.out.println("Error al actualizar el coche: " + exception.getMessage());
            return false;
        }
    }

    public boolean deleteById(int carId) {
        String sql = "DELETE FROM cars WHERE id = ?";

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, carId);

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            System.out.println("Error al eliminar el coche: " + exception.getMessage());
            return false;
        }
    }


}

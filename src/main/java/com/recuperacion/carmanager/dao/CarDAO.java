package com.recuperacion.carmanager.dao;

import com.recuperacion.carmanager.model.Car;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarDAO {  //Clase encargada de acceder a la clase cars en la base de datos, proponiendo diferentes consultas necesarios

    //Método que convierte cada fila de la tablaa en un objeto Car
    private Car mapResultSetToCar(ResultSet resultSet) throws SQLException {
        Date registrationSqlDate = resultSet.getDate("registration_date"); //Interesa saber la fecha de la creación de los coches

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

    //Devuelve una lista con todos los objetos Car usando el metodo anterior
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

    //Método principal para los filtreos, permite encontrar según la característica type del coche
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
            statement.setString(1, carType); //Sustituye la "?" de la consulta por el tipo recibido

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

    //Método para poder mostrar todos los tipos a la hora de filtrar en la combobox
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

    //Método para los admins para crear un coche nuevoo
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

    //Método para los admins para actualizar un cche existente
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

    //Método para los admins para borrar coches en base al id proporcionado
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


    //Busca un coche en concreto por su id, para abrir la vista de detalles
    public Car findById(int carId) {
        String sql = """
            SELECT id, brand, model, horse_power, car_type, registration_date, image_path
            FROM cars
            WHERE id = ?
            """;

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, carId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToCar(resultSet);
                }
            }

        } catch (SQLException exception) {
            System.out.println("Error al buscar el coche por id: " + exception.getMessage());
        }

        return null;
    }


}

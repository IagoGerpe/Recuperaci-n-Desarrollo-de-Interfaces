package com.recuperacion.carmanager.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database { //Método básico para establecer la conexión con la base de datos
    
    private static final String URL =
            "jdbc:mysql://localhost:3306/recuperacion_di";

    private static final String USER = "root";
    private static final String PASSWORD = "";

    private Database() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
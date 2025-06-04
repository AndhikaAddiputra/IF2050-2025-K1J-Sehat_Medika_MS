package model;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private Connection connection;

    public Connection getConnection() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find db.properties");
                return null;
            }

            Properties prop = new Properties();
            prop.load(input);

            String url = prop.getProperty("db.url");
            String user = prop.getProperty("db.user");
            String password = prop.getProperty("db.password");

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected using config file.");
        } catch (IOException ex) {
            System.out.println("Error reading db.properties");
            ex.printStackTrace();
        } catch (SQLException ex) {
            System.out.println("Database connection error");
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            System.out.println("MySQL JDBC Driver not found");
            ex.printStackTrace();
        }

        return connection;
    }
}


package model.dao;

import model.DatabaseConnection;
import model.entity.Receptionist;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceptionistDAO {

    private final Connection connection;

    public ReceptionistDAO() {
        this.connection = new DatabaseConnection().getConnection();
    }

    // Add a new receptionist
    public void addReceptionist(Receptionist receptionist) {
        String sql = "INSERT INTO Receptionist (receptionistId, userId, department) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, receptionist.getReceptionistId());
            statement.setInt(2, receptionist.getUserId());
            statement.setString(3, receptionist.getDepartment());
            statement.executeUpdate();
            System.out.println("Receptionist added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding receptionist: " + e.getMessage());
        }
    }

    // Get a receptionist by their ID
    public Receptionist getReceptionistById(String receptionistId) {
        String sql = "SELECT * FROM Receptionist WHERE receptionistId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, receptionistId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Receptionist(
                            resultSet.getString("receptionistId"),
                            resultSet.getInt("userId"),
                            resultSet.getString("department")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching receptionist: " + e.getMessage());
        }
        return null;
    }

    // Get all receptionists
    public List<Receptionist> getAllReceptionists() {
        List<Receptionist> receptionists = new ArrayList<>();
        String sql = "SELECT * FROM Receptionist";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                receptionists.add(new Receptionist(
                        resultSet.getString("receptionistId"),
                        resultSet.getInt("userId"),
                        resultSet.getString("department")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching all receptionists: " + e.getMessage());
        }
        return receptionists;
    }

    // Update a receptionist's information
    public void updateReceptionist(Receptionist receptionist) {
        String sql = "UPDATE Receptionist SET userId = ?, department = ? WHERE receptionistId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, receptionist.getUserId());
            statement.setString(2, receptionist.getDepartment());
            statement.setString(3, receptionist.getReceptionistId());
            statement.executeUpdate();
            System.out.println("Receptionist updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating receptionist: " + e.getMessage());
        }
    }

    // Delete a receptionist
    public void deleteReceptionist(String receptionistId) {
        String sql = "DELETE FROM Receptionist WHERE receptionistId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, receptionistId);
            statement.executeUpdate();
            System.out.println("Receptionist deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error deleting receptionist: " + e.getMessage());
        }
    }
}
package model.dao;

import model.DatabaseConnection;
import model.entity.PrescriptionItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionItemDAO {

    private final Connection connection;

    public PrescriptionItemDAO() {
        this.connection = new DatabaseConnection().getConnection();
    }

    // Add a prescription item
    public void addPrescriptionItem(PrescriptionItem item) {
        String sql = "INSERT INTO PrescriptionItem (itemId, prescriptionId, dosage, frequency, duration, notes) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, item.getItemId());
            statement.setInt(2, item.getPrescriptionId());
            statement.setString(3, item.getDosage());
            statement.setString(4, item.getFrequency());
            statement.setString(5, item.getDuration());
            statement.setString(6, item.getNotes());
            statement.executeUpdate();
            System.out.println("PrescriptionItem added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding prescription item: " + e.getMessage());
        }
    }

    // Get a prescription item by its composite key
    public PrescriptionItem getPrescriptionItemById(int prescriptionId, int itemId) {
        String sql = "SELECT * FROM PrescriptionItem WHERE prescriptionId = ? AND itemId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, prescriptionId);
            statement.setInt(2, itemId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new PrescriptionItem(
                            resultSet.getInt("itemId"),
                            resultSet.getInt("prescriptionId"),
                            resultSet.getString("dosage"),
                            resultSet.getString("frequency"),
                            resultSet.getString("duration"),
                            resultSet.getString("notes")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching prescription item: " + e.getMessage());
        }
        return null;
    }

    // Get all items for a specific prescription
    public List<PrescriptionItem> getItemsForPrescription(int prescriptionId) {
        List<PrescriptionItem> items = new ArrayList<>();
        String sql = "SELECT * FROM PrescriptionItem WHERE prescriptionId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, prescriptionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(new PrescriptionItem(
                            resultSet.getInt("itemId"),
                            resultSet.getInt("prescriptionId"),
                            resultSet.getString("dosage"),
                            resultSet.getString("frequency"),
                            resultSet.getString("duration"),
                            resultSet.getString("notes")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching prescription items: " + e.getMessage());
        }
        return items;
    }

    // Update a prescription item
    public void updatePrescriptionItem(PrescriptionItem item) {
        String sql = "UPDATE PrescriptionItem SET dosage = ?, frequency = ?, duration = ?, notes = ? WHERE prescriptionId = ? AND itemId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, item.getDosage());
            statement.setString(2, item.getFrequency());
            statement.setString(3, item.getDuration());
            statement.setString(4, item.getNotes());
            statement.setInt(5, item.getPrescriptionId());
            statement.setInt(6, item.getItemId());
            statement.executeUpdate();
            System.out.println("PrescriptionItem updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating prescription item: " + e.getMessage());
        }
    }

    // Delete a prescription item
    public void deletePrescriptionItem(int prescriptionId, int itemId) {
        String sql = "DELETE FROM PrescriptionItem WHERE prescriptionId = ? AND itemId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, prescriptionId);
            statement.setInt(2, itemId);
            statement.executeUpdate();
            System.out.println("PrescriptionItem deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error deleting prescription item: " + e.getMessage());
        }
    }
}
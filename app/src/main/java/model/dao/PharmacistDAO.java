package model.dao;

import model.entity.Pharmacist;
import model.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PharmacistDAO {

    public List<Pharmacist> getAllPharmacists() {
        List<Pharmacist> pharmacists = new ArrayList<>();
        String sql = "SELECT * FROM Pharmacist";

        try (Connection conn = new DatabaseConnection().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) pharmacists.add(mapResultSetToPharmacist(rs));

        } catch (SQLException e) {
            System.err.println("Error retrieving pharmacists: " + e.getMessage());
            e.printStackTrace();
        }

        return pharmacists;
    }

    public Pharmacist getPharmacistById(String pharmacistId) {
        String sql = "SELECT * FROM Pharmacist WHERE pharmacistId = ?";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pharmacistId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapResultSetToPharmacist(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching pharmacist: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public boolean insertPharmacist(Pharmacist pharmacist) {
        String sql = "INSERT INTO Pharmacist (pharmacistId, userId, licenseNumber) VALUES (?, ?, ?)";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pharmacist.getPharmacistId());
            pstmt.setInt(2, pharmacist.getUserId());
            pstmt.setString(3, pharmacist.getLicenseNumber());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting pharmacist: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePharmacist(Pharmacist pharmacist) {
        String sql = "UPDATE Pharmacist SET userId = ?, licenseNumber = ? WHERE pharmacistId = ?";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pharmacist.getUserId());
            pstmt.setString(2, pharmacist.getLicenseNumber());
            pstmt.setString(3, pharmacist.getPharmacistId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating pharmacist: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePharmacist(String pharmacistId) {
        String sql = "DELETE FROM Pharmacist WHERE pharmacistId = ?";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pharmacistId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting pharmacist: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Pharmacist mapResultSetToPharmacist(ResultSet rs) throws SQLException {
        return new Pharmacist(
            rs.getString("pharmacistId"),
            rs.getInt("userId"),
            rs.getString("licenseNumber")
        );
    }
}
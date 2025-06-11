package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import model.DatabaseConnection;
import model.entity.Medication;

public class MedicationDAO {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<Medication> getAllMedications() {
        List<Medication> medications = new ArrayList<>();
        String sql = "SELECT * FROM medication";

        try (Connection conn = new DatabaseConnection().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) medications.add(mapResultSetToMedication(rs));

        } catch (SQLException e) {
            System.err.println("Error retrieving medications: " + e.getMessage());
            e.printStackTrace();
        }

        return medications;
    }

    public Medication getMedicationById(int medicationId) {
        String sql = "SELECT * FROM medication WHERE medicationId = ?";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, medicationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapResultSetToMedication(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving medication: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public boolean insertMedication(Medication medication) {
        String sql = "INSERT INTO medication (name, genericName, category, unit, stockQuantity, minStockLevel, expiryDate, sideEffects, contraindication) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setPreparedStatementFields(pstmt, medication, false);

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) medication.setMedicationId(generatedKeys.getInt(1));
                }
            }

            return affected > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting medication: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMedication(Medication medication) {
        String sql = "UPDATE medication SET name=?, genericName=?, category=?, unit=?, stockQuantity=?, minStockLevel=?, expiryDate=?, sideEffects=?, contraindication=? WHERE medicationId=?";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setPreparedStatementFields(pstmt, medication, true);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating medication: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMedication(int medicationId) {
        String sql = "DELETE FROM medication WHERE medicationId = ?";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, medicationId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting medication: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void setPreparedStatementFields(PreparedStatement pstmt, Medication med, boolean includeId) throws SQLException {
        pstmt.setString(1, med.getName());
        pstmt.setString(2, med.getGenericName());
        pstmt.setString(3, med.getCategory());
        pstmt.setString(4, med.getUnit());
        pstmt.setInt(5, med.getStockQuantity());
        pstmt.setInt(6, med.getMinStockLevel());
        pstmt.setString(7, med.getExpiryDate().format(formatter));
        pstmt.setString(8, med.getSideEffects());
        pstmt.setString(9, med.getContraindication());

        if (includeId) pstmt.setInt(10, med.getMedicationId());
    }

    private Medication mapResultSetToMedication(ResultSet rs) throws SQLException {
        Medication med = new Medication();
        med.setMedicationId(rs.getInt("medicationId"));
        med.setName(rs.getString("name"));
        med.setGenericName(rs.getString("genericName"));
        med.setCategory(rs.getString("category"));
        med.setUnit(rs.getString("unit"));
        med.setStockQuantity(rs.getInt("stockQuantity"));
        med.setMinStockLevel(rs.getInt("minStockLevel"));
        med.setExpiryDate(rs.getTimestamp("expiryDate").toLocalDateTime());
        med.setSideEffects(rs.getString("sideEffects"));
        med.setContraindication(rs.getString("contraindication"));
        return med;
    }
}

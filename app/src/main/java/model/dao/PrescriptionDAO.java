package model.dao;

import model.DatabaseConnection;
import model.entity.Prescription;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionDAO {

    private final Connection connection;

    public PrescriptionDAO() {
        this.connection = new DatabaseConnection().getConnection();
    }

    // Create a new prescription
    public void addPrescription(Prescription prescription) {
        String sql = "INSERT INTO Prescription (patientId, doctorId, prescriptionDate, medications, instructions) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, prescription.getPatientId());
            statement.setString(2, prescription.getDoctorId());
            statement.setString(3, prescription.getPrescriptionDate());
            statement.setString(4, prescription.getMedications());
            statement.setString(5, prescription.getInstructions());
            statement.executeUpdate();
            System.out.println("Prescription added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding prescription: " + e.getMessage());
        }
    }

    // Get a prescription by its ID
    public Prescription getPrescriptionById(int prescriptionId) {
        String sql = "SELECT * FROM Prescription WHERE prescriptionId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, prescriptionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Prescription(
                            resultSet.getInt("prescriptionId"),
                            resultSet.getString("patientId"),
                            resultSet.getString("doctorId"),
                            resultSet.getString("prescriptionDate"),
                            resultSet.getString("medications"),
                            resultSet.getString("instructions")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching prescription: " + e.getMessage());
        }
        return null;
    }

    public List<Prescription> getPrescriptionsByStatus(String status) throws SQLException {
        List<Prescription> prescriptions = new ArrayList<>();
        
        try (Connection conn = new DatabaseConnection().getConnection()) {
            // First check if the status column exists
            boolean statusColumnExists = false;
            try {
                DatabaseMetaData metaData = conn.getMetaData();
                ResultSet rs = metaData.getColumns(null, null, "Prescription", "status");
                statusColumnExists = rs.next();
            } catch (Exception e) {
                System.err.println("Error checking for status column: " + e.getMessage());
            }
            
            if (statusColumnExists) {
                // If status column exists, query by status
                String sql = "SELECT * FROM Prescription WHERE status = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, status);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            prescriptions.add(mapResultSetToPrescription(rs));
                        }
                    }
                }
            } else {
                // If status column doesn't exist, add it
                try {
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate("ALTER TABLE Prescription ADD COLUMN status VARCHAR(20) DEFAULT 'PENDING'");
                    stmt.executeUpdate("UPDATE Prescription SET status = 'PENDING'");
                    stmt.close();
                    
                    // Return all prescriptions as PENDING for first-time setup
                    if (status.equals("PENDING")) {
                        String sql = "SELECT * FROM Prescription";
                        try (Statement statement = conn.createStatement();
                            ResultSet rs = statement.executeQuery(sql)) {
                            while (rs.next()) {
                                prescriptions.add(mapResultSetToPrescription(rs));
                            }
                        }
                    }
                    // For PROCESSING or COMPLETED, return empty list on first run
                } catch (SQLException e) {
                    System.err.println("Error adding status column: " + e.getMessage());
                    // Return all prescriptions if we couldn't add the column
                    if (status.equals("PENDING")) {
                        String sql = "SELECT * FROM Prescription";
                        try (Statement statement = conn.createStatement();
                            ResultSet rs = statement.executeQuery(sql)) {
                            while (rs.next()) {
                                Prescription p = mapResultSetToPrescription(rs);
                                p.setStatus("PENDING");
                                prescriptions.add(p);
                            }
                        }
                    }
                }
            }
        }
        
        return prescriptions;
    }

    // Get all prescriptions
    public List<Prescription> getAllPrescriptions() {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT * FROM Prescription";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                prescriptions.add(new Prescription(
                        resultSet.getInt("prescriptionId"),
                        resultSet.getString("patientId"),
                        resultSet.getString("doctorId"),
                        resultSet.getString("prescriptionDate"),
                        resultSet.getString("medications"),
                        resultSet.getString("instructions")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching all prescriptions: " + e.getMessage());
        }
        return prescriptions;
    }

    // Update a prescription
    public void updatePrescription(Prescription prescription) {
        String sql = "UPDATE Prescription SET patientId = ?, doctorId = ?, prescriptionDate = ?, medications = ?, instructions = ? WHERE prescriptionId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, prescription.getPatientId());
            statement.setString(2, prescription.getDoctorId());
            statement.setString(3, prescription.getPrescriptionDate());
            statement.setString(4, prescription.getMedications());
            statement.setString(5, prescription.getInstructions());
            statement.setInt(6, prescription.getPrescriptionId());
            statement.executeUpdate();
            System.out.println("Prescription updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating prescription: " + e.getMessage());
        }
    }

    // Delete a prescription
    public void deletePrescription(int prescriptionId) {
        String sql = "DELETE FROM Prescription WHERE prescriptionId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, prescriptionId);
            statement.executeUpdate();
            System.out.println("Prescription deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error deleting prescription: " + e.getMessage());
        }
    }

    private Prescription mapResultSetToPrescription(ResultSet rs) throws SQLException {
        Prescription prescription = new Prescription();
        prescription.setPrescriptionId(rs.getInt("prescriptionId"));
        
        try {
            prescription.setPatientId(rs.getString("patientId"));
        } catch (SQLException e) {
            prescription.setPatientId(null);
        }
        
        try {
            prescription.setDoctorId(rs.getString("doctorId"));
        } catch (SQLException e) {
            prescription.setDoctorId(null);
        }
        
        try {
            Timestamp timestamp = rs.getTimestamp("prescriptionDate");
            if (timestamp != null) {
                prescription.setCreatedAt(timestamp.toLocalDateTime());
            }
        } catch (SQLException e) {
            // Handle missing column
        }
        
        try {
            prescription.setMedications(rs.getString("medications"));
        } catch (SQLException e) {
            prescription.setMedications("");
        }
        
        try {
            prescription.setInstructions(rs.getString("instructions"));
        } catch (SQLException e) {
            prescription.setInstructions("");
        }
        
        try {
            prescription.setStatus(rs.getString("status"));
        } catch (SQLException e) {
            // If status column doesn't exist, default to PENDING
            prescription.setStatus("PENDING");
        }
        
        return prescription;
    }
}
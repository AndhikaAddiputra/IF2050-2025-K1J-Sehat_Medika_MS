package model.dao;

import model.DatabaseConnection;
import model.entity.Prescription;
import model.entity.PrescriptionItem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.entity.Prescription;

/**
 * Data Access Object for Prescription-related database operations
 */
public class PrescriptionDAO {

    /**
     * Maps a ResultSet to a Prescription object
     * 
     * @param rs ResultSet containing prescription data
     * @return Prescription object
     * @throws SQLException if a database access error occurs
     */
    private Prescription mapResultSetToPrescription(ResultSet rs) throws SQLException {
        Prescription prescription = new Prescription();
        prescription.setPrescriptionId(rs.getInt("prescriptionId"));
        prescription.setPatientId(rs.getString("patientId"));
        prescription.setDoctorId(rs.getString("doctorId"));
        
        Timestamp prescriptionDateTimestamp = rs.getTimestamp("prescriptionDate");
        if (prescriptionDateTimestamp != null) {
            prescription.setCreatedAt(prescriptionDateTimestamp.toLocalDateTime());
        }
        
        prescription.setMedications(rs.getString("medications"));
        prescription.setInstructions(rs.getString("instructions"));
        
        // Set status if it exists in the ResultSet
        try {
            prescription.setStatus(rs.getString("status"));
        } catch (SQLException e) {
            // Column might not exist, set default status
            prescription.setStatus("PENDING");
        }
        
        return prescription;
    }

    /**
     * Gets all prescriptions from the database
     * 
     * @return List of all prescriptions
     * @throws SQLException if a database access error occurs
     */
    public List<Prescription> getAllPrescriptions() throws SQLException {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT * FROM Prescription ORDER BY prescriptionDate DESC";
        
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                prescriptions.add(mapResultSetToPrescription(rs));
            }
        }
        
        return prescriptions;
    }

    /**
     * Gets a prescription by its ID
     * 
     * @param prescriptionId ID of the prescription to retrieve
     * @return Prescription object or null if not found
     * @throws SQLException if a database access error occurs
     */
    public Prescription getPrescriptionById(int prescriptionId) throws SQLException {
        String sql = "SELECT * FROM Prescription WHERE prescriptionId = ?";
        
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, prescriptionId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPrescription(rs);
                }
            }
        }
        
        return null;
    }

    /**
     * Gets prescriptions for a specific patient
     * 
     * @param patientId ID of the patient
     * @return List of prescriptions for the patient
     * @throws SQLException if a database access error occurs
     */
    public List<Prescription> getPrescriptionsByPatientId(String patientId) throws SQLException {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT * FROM Prescription WHERE patientId = ? ORDER BY prescriptionDate DESC";
        
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, patientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    prescriptions.add(mapResultSetToPrescription(rs));
                }
            }
        }
        
        return prescriptions;
    }

    /**
     * Gets prescriptions prescribed by a specific doctor
     * 
     * @param doctorId ID of the doctor
     * @return List of prescriptions prescribed by the doctor
     * @throws SQLException if a database access error occurs
     */
    public List<Prescription> getPrescriptionsByDoctorId(String doctorId) throws SQLException {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT * FROM Prescription WHERE doctorId = ? ORDER BY prescriptionDate DESC";
        
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, doctorId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    prescriptions.add(mapResultSetToPrescription(rs));
                }
            }
        }
        
        return prescriptions;
    }

    /**
     * Gets prescriptions with a specific status
     * 
     * @param status Status of prescriptions to retrieve (e.g., "PENDING", "PROCESSING", "COMPLETED")
     * @return List of prescriptions with the specified status
     * @throws SQLException if a database access error occurs
     */
    public List<Prescription> getPrescriptionsByStatus(String status) throws SQLException {
        List<Prescription> prescriptions = new ArrayList<>();
        
        // First check if status column exists
        boolean statusColumnExists = false;
        try (Connection conn = new DatabaseConnection().getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getColumns(null, null, "Prescription", "status");
            statusColumnExists = rs.next();
        }
        
        String sql;
        if (statusColumnExists) {
            sql = "SELECT * FROM Prescription WHERE status = ? ORDER BY prescriptionDate DESC";
        } else {
            // Handle case where status column doesn't exist yet
            // Return empty list for "COMPLETED" or "PROCESSING"
            // Return all prescriptions for "PENDING" (default status)
            if (!status.equals("PENDING")) {
                return prescriptions;
            }
            sql = "SELECT * FROM Prescription ORDER BY prescriptionDate DESC";
        }
        
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (statusColumnExists) {
                pstmt.setString(1, status);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    prescriptions.add(mapResultSetToPrescription(rs));
                }
            }
        }
        
        return prescriptions;
    }

    /**
     * Gets prescriptions created within a specific date range
     * 
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return List of prescriptions created within the date range
     * @throws SQLException if a database access error occurs
     */
    public List<Prescription> getPrescriptionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT * FROM Prescription WHERE prescriptionDate BETWEEN ? AND ? ORDER BY prescriptionDate DESC";
        
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(2, Timestamp.valueOf(endDate));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    prescriptions.add(mapResultSetToPrescription(rs));
                }
            }
        }
        
        return prescriptions;
    }

    /**
     * Adds a new prescription to the database
     * 
     * @param prescription Prescription to add
     * @return true if successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean addPrescription(Prescription prescription) throws SQLException {
        String sql = "INSERT INTO Prescription (patientId, doctorId, prescriptionDate, medications, instructions) VALUES (?, ?, ?, ?, ?)";
        
        // Check if status column exists and add it to the query if it does
        boolean statusColumnExists = false;
        try (Connection conn = new DatabaseConnection().getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getColumns(null, null, "Prescription", "status");
            statusColumnExists = rs.next();
        }
        
        if (statusColumnExists) {
            sql = "INSERT INTO Prescription (patientId, doctorId, prescriptionDate, medications, instructions, status) VALUES (?, ?, ?, ?, ?, ?)";
        }
        
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, prescription.getPatientId());
            pstmt.setString(2, prescription.getDoctorId());
            
            LocalDateTime createdAt = prescription.getCreatedAt();
            if (createdAt == null) {
                createdAt = LocalDateTime.now();
                prescription.setCreatedAt(createdAt);
            }
            pstmt.setTimestamp(3, Timestamp.valueOf(createdAt));
            
            pstmt.setString(4, prescription.getMedications());
            pstmt.setString(5, prescription.getInstructions());
            
            if (statusColumnExists) {
                String status = prescription.getStatus();
                if (status == null || status.isEmpty()) {
                    status = "PENDING";
                }
                pstmt.setString(6, status);
            }
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    prescription.setPrescriptionId(generatedKeys.getInt(1));
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    /**
     * Updates an existing prescription in the database
     * 
     * @param prescription Prescription to update
     * @return true if successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean updatePrescription(Prescription prescription) throws SQLException {
        // Check if status column exists and add it to the query if it does
        boolean statusColumnExists = false;
        try (Connection conn = new DatabaseConnection().getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getColumns(null, null, "Prescription", "status");
            statusColumnExists = rs.next();
        }
        
        String sql;
        if (statusColumnExists) {
            sql = "UPDATE Prescription SET patientId = ?, doctorId = ?, prescriptionDate = ?, medications = ?, instructions = ?, status = ? WHERE prescriptionId = ?";
        } else {
            sql = "UPDATE Prescription SET patientId = ?, doctorId = ?, prescriptionDate = ?, medications = ?, instructions = ? WHERE prescriptionId = ?";
        }
        
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, prescription.getPatientId());
            pstmt.setString(2, prescription.getDoctorId());
            pstmt.setTimestamp(3, Timestamp.valueOf(prescription.getCreatedAt()));
            pstmt.setString(4, prescription.getMedications());
            pstmt.setString(5, prescription.getInstructions());
            
            if (statusColumnExists) {
                pstmt.setString(6, prescription.getStatus());
                pstmt.setInt(7, prescription.getPrescriptionId());
            } else {
                pstmt.setInt(6, prescription.getPrescriptionId());
            }
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Updates the status of a prescription
     * 
     * @param prescriptionId ID of the prescription
     * @param status New status
     * @return true if successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean updatePrescriptionStatus(int prescriptionId, String status) throws SQLException {
        // Check if status column exists
        boolean statusColumnExists = false;
        try (Connection conn = new DatabaseConnection().getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getColumns(null, null, "Prescription", "status");
            statusColumnExists = rs.next();
        }
        
        // If status column doesn't exist, add it
        if (!statusColumnExists) {
            try (Connection conn = new DatabaseConnection().getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("ALTER TABLE Prescription ADD COLUMN status VARCHAR(20) DEFAULT 'PENDING'");
                statusColumnExists = true;
            }
        }
        
        if (statusColumnExists) {
            String sql = "UPDATE Prescription SET status = ? WHERE prescriptionId = ?";
            try (Connection conn = new DatabaseConnection().getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, status);
                pstmt.setInt(2, prescriptionId);
                
                return pstmt.executeUpdate() > 0;
            }
        }
        
        return false;
    }

    /**
     * Deletes a prescription from the database
     * 
     * @param prescriptionId ID of the prescription to delete
     * @return true if successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean deletePrescription(int prescriptionId) throws SQLException {
        String sql = "DELETE FROM Prescription WHERE prescriptionId = ?";
        
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, prescriptionId);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Gets all prescription items for a specific prescription
     * 
     * @param prescriptionId ID of the prescription
     * @return List of prescription items
     * @throws SQLException if a database access error occurs
     */
    public List<PrescriptionItem> getPrescriptionItems(int prescriptionId) throws SQLException {
        List<PrescriptionItem> items = new ArrayList<>();
        String sql = "SELECT * FROM PrescriptionItem WHERE prescriptionId = ?";
        
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, prescriptionId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PrescriptionItem item = new PrescriptionItem();
                    item.setItemId(rs.getInt("itemId"));
                    item.setPrescriptionId(rs.getInt("prescriptionId"));
                    item.setDosage(rs.getString("dosage"));
                    item.setFrequency(rs.getString("frequency"));
                    item.setDuration(rs.getString("duration"));
                    item.setNotes(rs.getString("notes"));
                    items.add(item);
                }
            }
        }
        
        return items;
    }

    /**
     * Adds a prescription item to the database
     * 
     * @param item Prescription item to add
     * @return true if successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean addPrescriptionItem(PrescriptionItem item) throws SQLException {
        String sql = "INSERT INTO PrescriptionItem (prescriptionId, itemId, dosage, frequency, duration, notes) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, item.getPrescriptionId());
            pstmt.setInt(2, item.getItemId());
            pstmt.setString(3, item.getDosage());
            pstmt.setString(4, item.getFrequency());
            pstmt.setString(5, item.getDuration());
            pstmt.setString(6, item.getNotes());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Updates a prescription item in the database
     * 
     * @param item Prescription item to update
     * @return true if successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean updatePrescriptionItem(PrescriptionItem item) throws SQLException {
        String sql = "UPDATE PrescriptionItem SET dosage = ?, frequency = ?, duration = ?, notes = ? WHERE prescriptionId = ? AND itemId = ?";
        
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, item.getDosage());
            pstmt.setString(2, item.getFrequency());
            pstmt.setString(3, item.getDuration());
            pstmt.setString(4, item.getNotes());
            pstmt.setInt(5, item.getPrescriptionId());
            pstmt.setInt(6, item.getItemId());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a prescription item from the database
     * 
     * @param prescriptionId ID of the prescription
     * @param itemId ID of the item
     * @return true if successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean deletePrescriptionItem(int prescriptionId, int itemId) throws SQLException {
        String sql = "DELETE FROM PrescriptionItem WHERE prescriptionId = ? AND itemId = ?";
        
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, prescriptionId);
            pstmt.setInt(2, itemId);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Gets the count of prescriptions for different statuses
     * 
     * @return Map with status as key and count as value
     * @throws SQLException if a database access error occurs
     */
    public Map<String, Integer> getPrescriptionCounts() throws SQLException {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("PENDING", 0);
        counts.put("PROCESSING", 0);
        counts.put("COMPLETED", 0);
        
        // Check if status column exists
        boolean statusColumnExists = false;
        try (Connection conn = new DatabaseConnection().getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getColumns(null, null, "Prescription", "status");
            statusColumnExists = rs.next();
        }
        
        if (statusColumnExists) {
            String sql = "SELECT status, COUNT(*) as count FROM Prescription GROUP BY status";
            
            try (Connection conn = new DatabaseConnection().getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                
                while (rs.next()) {
                    String status = rs.getString("status");
                    int count = rs.getInt("count");
                    counts.put(status, count);
                }
            }
        } else {
            // If status column doesn't exist, count all as PENDING
            String sql = "SELECT COUNT(*) as count FROM Prescription";
            
            try (Connection conn = new DatabaseConnection().getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                
                if (rs.next()) {
                    counts.put("PENDING", rs.getInt("count"));
                }
            }
        }
        
        return counts;
    }
}
package model.dao;

import model.entity.BloodType; 
import model.entity.Patient;  
import model.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class PatientDAO {

    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setPatientId(rs.getString("patientId"));
        patient.setUserId(rs.getString("userId"));
        String bloodTypeStr = rs.getString("bloodType");
        patient.setBloodType(bloodTypeStr != null ? BloodType.valueOf(bloodTypeStr) : null);
        patient.setAllergies(rs.getString("allergies"));
        patient.setEmergencyContact(rs.getString("emergencyContact"));
        patient.setInsuranceInfo(rs.getString("insuranceInfo"));
        Timestamp registrationDateTimestamp = rs.getTimestamp("registrationDate");
        patient.setRegistrationDate(registrationDateTimestamp != null ? registrationDateTimestamp.toLocalDateTime() : null);
        return patient;
    }

    public void addPatient(Patient patient) throws SQLException {
        String sql = "INSERT INTO Patient (patientId, userId, bloodType, allergies, emergencyContact, insuranceInfo, registrationDate) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patient.getPatientId());
            pstmt.setString(2, patient.getUserId());
            pstmt.setString(3, patient.getBloodType() != null ? patient.getBloodType().name() : null);
            pstmt.setString(4, patient.getAllergies());
            pstmt.setString(5, patient.getEmergencyContact());
            pstmt.setString(6, patient.getInsuranceInfo());
            pstmt.setTimestamp(7, patient.getRegistrationDate() != null ? Timestamp.valueOf(patient.getRegistrationDate()) : null);
            pstmt.executeUpdate();
        }
    }

    public Patient getPatientById(String patientId) throws SQLException {
        Patient patient = null;
        String sql = "SELECT * FROM Patient WHERE patientId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    patient = mapResultSetToPatient(rs);
                }
            }
        }
        return patient;
    }

    public Patient getPatientByUserId(String userId) throws SQLException {
        Patient patient = null;
        String sql = "SELECT * FROM Patient WHERE userId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    patient = mapResultSetToPatient(rs);
                }
            }
        }
        return patient;
    }

    public Map<String, Object> getPatientMedicalInfo(String patientId) throws SQLException {
        String sql = "SELECT bloodType, allergies, insuranceInfo FROM Patient WHERE patientId = ?";
        Map<String, Object> result = new HashMap<>();
        
        try (Connection conn = new DatabaseConnection().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                result.put("bloodType", rs.getString("bloodType"));
                result.put("allergies", rs.getString("allergies"));
                result.put("insuranceInfo", rs.getString("insuranceInfo"));
            }
        }
        return result;
    }

    public void updatePatientMedicalInfo(String patientId, String bloodType, String allergies, String insuranceInfo) throws SQLException {
        String sql = "UPDATE Patient SET bloodType = ?, allergies = ?, insuranceInfo = ? WHERE patientId = ?";
        
        try (Connection conn = new DatabaseConnection().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bloodType);
            stmt.setString(2, allergies);
            stmt.setString(3, insuranceInfo);
            stmt.setString(4, patientId);
            stmt.executeUpdate();
        }
    }


    public List<Patient> getAllPatients() throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM Patient";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
        }
        return patients;
    }

    public void updatePatient(Patient patient) throws SQLException {
        String sql = "UPDATE Patient SET userId = ?, bloodType = ?, allergies = ?, emergencyContact = ?, insuranceInfo = ?, registrationDate = ? WHERE patientId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patient.getUserId());
             pstmt.setString(2, patient.getBloodType() != null ? patient.getBloodType().name() : null);
            pstmt.setString(3, patient.getAllergies());
            pstmt.setString(4, patient.getEmergencyContact());
            pstmt.setString(5, patient.getInsuranceInfo());
            pstmt.setTimestamp(6, patient.getRegistrationDate() != null ? Timestamp.valueOf(patient.getRegistrationDate()) : null);
            pstmt.setString(7, patient.getPatientId());
            pstmt.executeUpdate();
        }
    }

    public void deletePatient(String patientId) throws SQLException {
        String sql = "DELETE FROM Patient WHERE patientId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patientId);
            pstmt.executeUpdate();
        }
    }

    public String getPatientName(String patientId) {
        String sql = "SELECT u.username FROM User u JOIN Patient p ON u.userId = p.userId WHERE p.patientId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching patient name: " + e.getMessage());
            e.printStackTrace();
        }
        return "Unknown";
    }
}
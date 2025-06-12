package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.DatabaseConnection;
import model.entity.BloodType;
import model.entity.Patient;

public class PatientDAO {
    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setPatientId(rs.getString("patientId"));
        patient.setUserId(rs.getString("userId"));
        patient.setFullName(rs.getString("fullName")); // From JOIN with User table

        String bloodTypeStr = rs.getString("bloodType");
        patient.setBloodType(bloodTypeStr != null && !bloodTypeStr.isEmpty() ? BloodType.valueOf(bloodTypeStr) : null);

        patient.setAllergies(rs.getString("allergies"));
        patient.setHeight(rs.getInt("height"));
        patient.setWeight(rs.getInt("weight"));
        patient.setEmergencyContact(rs.getString("emergencyContact"));
        patient.setInsuranceInfo(rs.getString("insuranceInfo"));
        patient.setInsuranceNumber(rs.getString("insuranceNumber"));

        Timestamp registrationDateTimestamp = rs.getTimestamp("registrationDate");
        patient.setRegistrationDate(registrationDateTimestamp != null ? registrationDateTimestamp.toLocalDateTime() : null);

        return patient;
    }

    public boolean addPatient(Patient patient) {
        String sql = "INSERT INTO Patient (patientId, userId, bloodType, allergies, height, weight, emergencyContact, insuranceInfo, insuranceNumber, registrationDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patient.getPatientId());
            pstmt.setString(2, patient.getUserId());
            pstmt.setString(3, patient.getBloodType() != null ? patient.getBloodType().name() : null);
            pstmt.setString(4, patient.getAllergies());
            pstmt.setInt(5, patient.getHeight());
            pstmt.setInt(6, patient.getWeight());
            pstmt.setString(7, patient.getEmergencyContact());
            pstmt.setString(8, patient.getInsuranceInfo());
            pstmt.setString(9, patient.getInsuranceNumber());
            pstmt.setTimestamp(10, patient.getRegistrationDate() != null ? Timestamp.valueOf(patient.getRegistrationDate()) : null);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding patient: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE Patient SET userId = ?, bloodType = ?, allergies = ?, height = ?, weight = ?, emergencyContact = ?, insuranceInfo = ?, insuranceNumber = ?, registrationDate = ? WHERE patientId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patient.getUserId());
            pstmt.setString(2, patient.getBloodType() != null ? patient.getBloodType().name() : null);
            pstmt.setString(3, patient.getAllergies());
            pstmt.setInt(4, patient.getHeight());
            pstmt.setInt(5, patient.getWeight());
            pstmt.setString(6, patient.getEmergencyContact());
            pstmt.setString(7, patient.getInsuranceInfo());
            pstmt.setString(8, patient.getInsuranceNumber());
            pstmt.setTimestamp(9, patient.getRegistrationDate() != null ? Timestamp.valueOf(patient.getRegistrationDate()) : null);
            pstmt.setString(10, patient.getPatientId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating patient: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePatient(String patientId) {
        String sql = "DELETE FROM Patient WHERE patientId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patientId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting patient: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT p.*, u.fullName, u.email, u.phoneNumber FROM Patient p " + "JOIN User u ON p.userId = u.userId";
        
        try (Connection conn = new DatabaseConnection().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Patient patient = mapResultSetToPatient(rs);
                patients.add(patient);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all patients: " + e.getMessage());
            e.printStackTrace();
        }
        
        return patients;
    } 

    public Patient getPatientById(String patientId) {
        String sql = "SELECT p.*, u.fullName FROM Patient p JOIN User u ON p.userId = u.userId WHERE p.patientId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching patient by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Patient getPatientByUserId(String userId) {
        String sql = "SELECT p.*, u.fullName FROM Patient p JOIN User u ON p.userId = u.userId WHERE p.userId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching patient by User ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Object> getPatientMedicalInfo(String patientId) throws SQLException {
        String sql = "SELECT bloodType, allergies, height, weight, insuranceInfo, insuranceNumber FROM Patient WHERE patientId = ?";
        Map<String, Object> result = new HashMap<>();
        
        try (Connection conn = new DatabaseConnection().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                result.put("bloodType", rs.getString("bloodType"));
                result.put("allergies", rs.getString("allergies"));
                result.put("height", rs.getInt("height"));
                result.put("weight", rs.getInt("weight"));
                result.put("insuranceInfo", rs.getString("insuranceInfo"));
                result.put("insuranceNumber", rs.getString("insuranceNumber"));
            }
        }
        return result;
    }

    public void updatePatientMedicalInfo(String patientId, String bloodType, String allergies, String height, String weight, String insuranceInfo, String insuranceNumber) throws SQLException {
        String sql = "UPDATE Patient SET bloodType = ?, allergies = ?, height = ?, weight = ?, insuranceInfo = ?, insuranceNumber = ? WHERE patientId = ?";
        
        try (Connection conn = new DatabaseConnection().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bloodType);
            stmt.setString(2, allergies);
            stmt.setString(3, height);
            stmt.setString(4, weight);
            stmt.setString(5, insuranceInfo);
            stmt.setString(6, insuranceNumber);
            stmt.setString(7, patientId);
            stmt.executeUpdate();
        }
    }


    public String getPatientName(String patientId) {
        String sql = "SELECT u.fullName FROM User u JOIN Patient p ON u.userId = p.userId WHERE p.patientId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("fullName");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching patient name: " + e.getMessage());
            e.printStackTrace();
        }
        return "Unknown";
    }
}
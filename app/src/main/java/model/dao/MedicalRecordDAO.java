package model.dao;

import model.entity.MedicalRecord; 
import model.DatabaseConnection;

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

public class MedicalRecordDAO {

    private MedicalRecord mapResultSetToMedicalRecord(ResultSet rs) throws SQLException {
        MedicalRecord record = new MedicalRecord();
        record.setRecordID(rs.getInt("recordId")); 
        record.setPatientId(rs.getString("patientId"));
        record.setDoctorId(rs.getString("doctorId"));
        Timestamp recordDateTimestamp = rs.getTimestamp("recordDate");
        record.setRecordDate(recordDateTimestamp != null ? recordDateTimestamp.toLocalDateTime() : null);
        record.setDiagnosis(rs.getString("diagnosis"));
        record.setSymptoms(rs.getString("symptoms"));
        record.setNotes(rs.getString("notes"));
        record.setAttachments(rs.getString("attachments")); 
        return record;
    }

    public void addMedicalRecord(MedicalRecord record) throws SQLException {
        String sql = "INSERT INTO MedicalRecord (patientId, doctorId, recordDate, diagnosis, symptoms, notes, attachments) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = new DatabaseConnection().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, record.getPatientId());
            pstmt.setString(2, record.getDoctorId());
            pstmt.setTimestamp(3, record.getRecordDate() != null ? Timestamp.valueOf(record.getRecordDate()) : null);
            pstmt.setString(4, record.getDiagnosis());
            pstmt.setString(5, record.getSymptoms());
            pstmt.setString(6, record.getNotes());
            pstmt.setString(7, record.getAttachments()); 
            
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating medical record failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    record.setRecordID(generatedKeys.getInt(1)); 
                } else {
                    throw new SQLException("Creating medical record failed, no ID obtained.");
                }
            }
        }
    }

    public MedicalRecord getMedicalRecordById(int recordId) throws SQLException {
        MedicalRecord record = null;
        String sql = "SELECT * FROM MedicalRecord WHERE recordId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recordId); 
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    record = mapResultSetToMedicalRecord(rs);
                }
            }
        }
        return record;
    }

    public List<MedicalRecord> getMedicalRecordsByPatientId(String patientId) throws SQLException {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM MedicalRecord WHERE patientId = ? ORDER BY recordDate DESC";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToMedicalRecord(rs));
                }
            }
        }
        return records;
    }

    public List<Map<String, Object>> getMedicalRecordsForPatient(String patientId) throws SQLException {
        String sql = """
            SELECT mr.recordDate, u.username as doctorName, mr.diagnosis, mr.symptoms, mr.notes 
            FROM MedicalRecord mr 
            JOIN Doctor d ON mr.doctorId = d.doctorId 
            JOIN User u ON d.userId = u.userId 
            WHERE mr.patientId = ? 
            ORDER BY mr.recordDate DESC""";
        
        List<Map<String, Object>> records = new ArrayList<>();
        
        try (Connection conn = new DatabaseConnection().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> record = new HashMap<>();
                record.put("recordDate", rs.getTimestamp("recordDate"));
                record.put("doctorName", rs.getString("doctorName"));
                record.put("diagnosis", rs.getString("diagnosis"));
                record.put("symptoms", rs.getString("symptoms"));
                record.put("notes", rs.getString("notes"));
                records.add(record);
            }
        }
        return records;
    }

    public List<MedicalRecord> getMedicalRecordsByDoctorId(String doctorId) throws SQLException {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM MedicalRecord WHERE doctorId = ? ORDER BY recordDate DESC";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, doctorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToMedicalRecord(rs));
                }
            }
        }
        return records;
    }

    public List<MedicalRecord> getAllMedicalRecords() throws SQLException {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM MedicalRecord ORDER BY recordDate DESC";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                records.add(mapResultSetToMedicalRecord(rs));
            }
        }
        return records;
    }

    public void updateMedicalRecord(MedicalRecord record) throws SQLException {
        String sql = "UPDATE MedicalRecord SET patientId = ?, doctorId = ?, recordDate = ?, diagnosis = ?, symptoms = ?, notes = ?, attachments = ? WHERE recordId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, record.getPatientId());
            pstmt.setString(2, record.getDoctorId());
            pstmt.setTimestamp(3, record.getRecordDate() != null ? Timestamp.valueOf(record.getRecordDate()) : null);
            pstmt.setString(4, record.getDiagnosis());
            pstmt.setString(5, record.getSymptoms());
            pstmt.setString(6, record.getNotes());
            pstmt.setString(7, record.getAttachments()); 
            pstmt.setInt(8, record.getRecordID()); 
            pstmt.executeUpdate();
        }
    }

    public void deleteMedicalRecord(int recordId) throws SQLException {
        String sql = "DELETE FROM MedicalRecord WHERE recordId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recordId); 
            pstmt.executeUpdate();
        }
    }
}
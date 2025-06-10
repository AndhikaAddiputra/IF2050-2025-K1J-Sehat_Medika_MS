package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; 
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.DatabaseConnection;
import model.entity.Appointment;
import model.entity.AppointmentStatus;

public class AppointmentDAO {

    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(rs.getInt("appointmentId")); 
        appointment.setPatientId(rs.getString("patientId"));
        appointment.setDoctorId(rs.getString("doctorId"));
        Timestamp appointmentDateTimestamp = rs.getTimestamp("appointmentDate");
        appointment.setAppointmentDate(appointmentDateTimestamp != null ? appointmentDateTimestamp.toLocalDateTime() : null);
        appointment.setDuration(rs.getInt("duration")); 
        appointment.setReason(rs.getString("reason"));  
        String statusStr = rs.getString("appointmentStatus");
        appointment.setStatus(statusStr != null ? AppointmentStatus.valueOf(statusStr) : null); 
        appointment.setQueueNumber(rs.getInt("queueNumber"));
        appointment.setDoctorConfirmation(rs.getBoolean("doctorConfirmation")); 
        return appointment;
    }

    public void addAppointment(Appointment appointment) throws SQLException {
        String sql = "INSERT INTO Appointment (patientId, doctorId, appointmentDate, duration, reason, status, queueNumber, doctorConfirmation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = new DatabaseConnection().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, appointment.getPatientId());
            pstmt.setString(2, appointment.getDoctorId());
            pstmt.setTimestamp(3, appointment.getAppointmentDate() != null ? Timestamp.valueOf(appointment.getAppointmentDate()) : null);
            pstmt.setInt(4, appointment.getDuration());
            pstmt.setString(5, appointment.getReason());
            pstmt.setString(6, appointment.getStatus() != null ? appointment.getStatus().name() : null);
            pstmt.setInt(7, appointment.getQueueNumber());
            pstmt.setBoolean(8, appointment.isDoctorConfirmed());
            
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating appointment failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    appointment.setAppointmentId(generatedKeys.getInt(1)); 
                } else {
                    throw new SQLException("Creating appointment failed, no ID obtained.");
                }
            }
        }
    }

    public Appointment getAppointmentById(int appointmentId) throws SQLException {
        Appointment appointment = null;
        String sql = "SELECT * FROM Appointment WHERE appointmentId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, appointmentId); 
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    appointment = mapResultSetToAppointment(rs);
                }
            }
        }
        return appointment;
    }

    public List<Appointment> getAppointmentsByPatientId(String patientId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM Appointment WHERE patientId = ? ORDER BY appointmentDate DESC";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
        }
        return appointments;
    }

    public List<Appointment> getAppointmentsByDoctorId(String doctorId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM Appointment WHERE doctorId = ? ORDER BY appointmentDate DESC";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, doctorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
        }
        return appointments;
    }

    public List<Appointment> getAppointmentsByDate(LocalDateTime date) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        // Query for appointments on a specific date (ignoring time for comparison)
        String sql = "SELECT * FROM Appointment WHERE DATE(appointmentDate) = DATE(?) ORDER BY appointmentDate ASC";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(date));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
        }
        return appointments;
    }

    public List<Appointment> getAllAppointments() throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM Appointment ORDER BY appointmentDate DESC";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        }
        return appointments;
    }

    public List<Appointment> getActiveAppointments(String patientId) throws SQLException {
        List<Appointment> appointments =  new ArrayList<>();
        String sql = "Select * from Appointment where patientId = ? AND AppointmentStatus = 'SCHEDULED' order by appointmentDate desc";
        try (Connection conn = new DatabaseConnection().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
        }
        return appointments;
    }

    public void updateAppointment(Appointment appointment) throws SQLException {
        String sql = "UPDATE Appointment SET patientId = ?, doctorId = ?, appointmentDate = ?, duration = ?, reason = ?, status = ?, queueNumber = ?, doctorConfirmation = ? WHERE appointmentId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, appointment.getPatientId());
            pstmt.setString(2, appointment.getDoctorId());
            pstmt.setTimestamp(3, appointment.getAppointmentDate() != null ? Timestamp.valueOf(appointment.getAppointmentDate()) : null);
            pstmt.setInt(4, appointment.getDuration());
            pstmt.setString(5, appointment.getReason());
            pstmt.setString(6, appointment.getStatus() != null ? appointment.getStatus().name() : null);
            pstmt.setInt(7, appointment.getQueueNumber());
            pstmt.setBoolean(8, appointment.isDoctorConfirmed());
            pstmt.setInt(9, appointment.getAppointmentId()); 
            pstmt.executeUpdate();
        }
    }

    public void deleteAppointment(int appointmentId) throws SQLException {
        String sql = "DELETE FROM Appointment WHERE appointmentId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, appointmentId); 
            pstmt.executeUpdate();
        }
    }
}
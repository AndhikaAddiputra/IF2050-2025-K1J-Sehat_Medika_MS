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
        String statusStr = rs.getString("appointmentStatus"); // Pastikan ini 'appointmentStatus'
        appointment.setAppointmentStatus(statusStr != null ? AppointmentStatus.valueOf(statusStr) : null);
        appointment.setQueueNumber(rs.getInt("queueNumber"));
        return appointment;
    }

    public void addAppointment(Appointment appointment) throws SQLException {
        // SQL ini hanya menyertakan kolom yang ada di tabel Appointment Anda (7 kolom non-ID)
        String sql = "INSERT INTO Appointment (patientId, doctorId, appointmentDate, duration, reason, appointmentStatus, queueNumber) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, appointment.getPatientId());
            pstmt.setString(2, appointment.getDoctorId());
            pstmt.setTimestamp(3, appointment.getAppointmentDate() != null ? Timestamp.valueOf(appointment.getAppointmentDate()) : null);
            pstmt.setInt(4, appointment.getDuration());
            pstmt.setString(5, appointment.getReason());
            pstmt.setString(6, appointment.getAppointmentStatus() != null ? appointment.getAppointmentStatus().name() : null);
            pstmt.setInt(7, appointment.getQueueNumber());

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
        // Menggunakan 'appointmentStatus' bukan 'AppointmentStatus' (case-sensitive)
        // Dan menggunakan 'REQUESTED' atau 'ACCEPTED' sesuai ENUM DB
        String sql = "SELECT * FROM Appointment WHERE patientId = ? AND (appointmentStatus = 'REQUESTED' OR appointmentStatus = 'ACCEPTED') ORDER BY appointmentDate DESC";
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
        // Menggunakan 'appointmentStatus' dan tidak ada 'doctorConfirmation'
        String sql = "UPDATE Appointment SET patientId = ?, doctorId = ?, appointmentDate = ?, duration = ?, reason = ?, appointmentStatus = ?, queueNumber = ? WHERE appointmentId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, appointment.getPatientId());
            pstmt.setString(2, appointment.getDoctorId());
            pstmt.setTimestamp(3, appointment.getAppointmentDate() != null ? Timestamp.valueOf(appointment.getAppointmentDate()) : null);
            pstmt.setInt(4, appointment.getDuration());
            pstmt.setString(5, appointment.getReason());
            pstmt.setString(6, appointment.getAppointmentStatus() != null ? appointment.getAppointmentStatus().name() : null);
            pstmt.setInt(7, appointment.getQueueNumber());
            pstmt.setInt(8, appointment.getAppointmentId()); // Ini adalah parameter untuk WHERE clause
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

    public Appointment getAppointmentByDetails(String doctorName, String date, String time) throws SQLException {
        String sql = """
                 SELECT a.* FROM Appointment a
                 JOIN Doctor d ON a.doctorId = d.doctorId
                 WHERE d.fullName = ? AND DATE(a.appointmentDate) = STR_TO_DATE(?, '%d-%m-%Y')
                 AND TIME(a.appointmentDate) = STR_TO_DATE(?, '%H:%i')
                 LIMIT 1
             """;

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, doctorName); // Pastikan ini adalah nama dokter, dari kolom fullName
            pstmt.setString(2, date);
            pstmt.setString(3, time);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAppointment(rs);
                }
            }
        }

        throw new SQLException("Appointment not found for given doctor, date, and time.");
    }

    public List<Appointment> getAppointmentsByDoctorAndDate(String doctorId, LocalDateTime dateTime) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        
        // Get the date in YYYY-MM-DD format
        String dateStr = dateTime.toLocalDate().toString();
        
        // Add more detailed logging
        System.out.println("Querying appointments for doctor: " + doctorId + " on date: " + dateStr);
        
        // Modified SQL to properly compare only the date part
        String sql = "SELECT * FROM Appointment WHERE doctorId = ? AND DATE(appointmentDate) = ? AND appointmentStatus = 'ACCEPTED'";
        
        try (Connection conn = new DatabaseConnection().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, doctorId);
            pstmt.setString(2, dateStr);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Appointment appointment = mapResultSetToAppointment(rs);
                    appointments.add(appointment);
                    System.out.println("Found appointment: ID=" + appointment.getAppointmentId() + 
                                    ", Date=" + appointment.getAppointmentDate() + 
                                    ", Status=" + appointment.getAppointmentStatus());
                }
            }
        }
        
        // If no results, run a broader query to help debug
        if (appointments.isEmpty()) {
            System.out.println("No ACCEPTED appointments found. Checking if any appointments exist for this doctor...");
            String debugSql = "SELECT COUNT(*) as count FROM Appointment WHERE doctorId = ?";
            try (Connection conn = new DatabaseConnection().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(debugSql)) {
                
                pstmt.setString(1, doctorId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int count = rs.getInt("count");
                        System.out.println("Total appointments for doctor " + doctorId + ": " + count);
                        
                        // If appointments exist, check their statuses
                        if (count > 0) {
                            debugSql = "SELECT appointmentId, appointmentDate, appointmentStatus FROM Appointment WHERE doctorId = ?";
                            try (PreparedStatement pstmt2 = conn.prepareStatement(debugSql)) {
                                pstmt2.setString(1, doctorId);
                                try (ResultSet rs2 = pstmt2.executeQuery()) {
                                    while (rs2.next()) {
                                        System.out.println("Appointment ID: " + rs2.getInt("appointmentId") + 
                                                        ", Date: " + rs2.getTimestamp("appointmentDate") + 
                                                        ", Status: " + rs2.getString("appointmentStatus"));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return appointments;
    }

    public boolean updateAppointmentStatus(Appointment appointment) throws SQLException {
        String sql = "UPDATE Appointment SET appointmentStatus = ? WHERE appointmentId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, appointment.getAppointmentStatus().name());
            pstmt.setInt(2, appointment.getAppointmentId());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Return true if at least one row was updated
        }
    }

    public int acceptAllPendingAppointmentsForDoctor(String doctorId) throws SQLException {
    String sql = "UPDATE Appointment SET appointmentStatus = 'ACCEPTED' WHERE doctorId = ? AND appointmentStatus = 'REQUESTED'";
    
    try (Connection conn = new DatabaseConnection().getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, doctorId);
        return pstmt.executeUpdate();
    }
}
}

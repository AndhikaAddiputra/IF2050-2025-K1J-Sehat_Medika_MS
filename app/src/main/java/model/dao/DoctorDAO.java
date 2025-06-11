package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; 
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import model.DatabaseConnection;
import model.entity.Doctor;

public class DoctorDAO {
    private List<DayOfWeek> parseDays(String daysString) {
        List<DayOfWeek> days = new ArrayList<>();
        if (daysString != null && !daysString.isEmpty()) {
            for (String day : daysString.split(",")) {
                days.add(DayOfWeek.valueOf(day.trim().toUpperCase()));
            }
        }
        return days;
    }

    private List<LocalTime[]> parseHours(String hoursString) {
        List<LocalTime[]> hoursList = new ArrayList<>();
        if (hoursString != null && !hoursString.isEmpty()) {
            for (String range : hoursString.split(",")) {
                String[] times = range.split("-");
                if (times.length == 2) {
                    hoursList.add(new LocalTime[]{LocalTime.parse(times[0].trim()), LocalTime.parse(times[1].trim())});
                }
            }
        }
        return hoursList;
    }

    private String formatDays(List<DayOfWeek> days) {
        return String.join(",", days.stream().map(DayOfWeek::name).toList());
    }

    private String formatHours(List<LocalTime[]> hoursList) {
        return String.join(",", hoursList.stream()
                .map(range -> range[0].toString() + "-" + range[1].toString()).toList());
    }

    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM Doctor";

        try (Connection conn = new DatabaseConnection().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) doctors.add(mapResultSetToDoctor(rs));

        } catch (SQLException e) {
            System.err.println("Error retrieving doctors: " + e.getMessage());
            e.printStackTrace();
        }

        return doctors;
    }

    public Doctor getDoctorById(String doctorId) {
        String sql = "SELECT * FROM Doctor WHERE doctorId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, doctorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapResultSetToDoctor(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching doctor: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public String getDoctorNameById(String doctorId) {
        String sql = "SELECT u.username FROM User u JOIN Doctor d ON u.userId = d.userId WHERE d.doctorId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, doctorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching doctor name: " + e.getMessage());
            e.printStackTrace();
        }
        return "Unknown";
    }

    public List<String> getAllSpecializations() {
        List<String> specializations = new ArrayList<>();
        String sql = "SELECT DISTINCT specialization FROM Doctor";
        try (Connection conn = new DatabaseConnection().getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String specialization = rs.getString("specialization");
                if (specialization != null && !specialization.isEmpty()) {
                    specializations.add(specialization);
                }
            }

        } 
        catch (SQLException e) {
            System.err.println("Error retrieving specializations: " + e.getMessage());
            e.printStackTrace();
        }

        return specializations;
    }

    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM Doctor WHERE specialization = ?";
        try (Connection conn = new DatabaseConnection().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, specialization);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    doctors.add(mapResultSetToDoctor(rs));
                }
            }

        } 
        catch (SQLException e) {
            System.err.println("Error fetching doctors by specialization: " + e.getMessage());
            e.printStackTrace();
        }

        return doctors;
    }

    public boolean insertDoctor(Doctor doctor) {
        String sql = "INSERT INTO Doctor (doctorId, userId, specialization, licenseNumber, availableDays, availableHours) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, doctor.getDoctorId());
            pstmt.setInt(2, doctor.getUserId());
            pstmt.setString(3, doctor.getSpecialization());
            pstmt.setString(4, doctor.getLicenceNumber());
            pstmt.setString(5, formatDays(doctor.getAvailableDays()));
            pstmt.setString(6, formatHours(doctor.getAvailableHours()));

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting doctor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDoctor(Doctor doctor) {
        String sql = "UPDATE Doctor SET userId = ?, specialization = ?, licenseNumber = ?, availableDays = ?, availableHours = ? WHERE doctorId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctor.getUserId());
            pstmt.setString(2, doctor.getSpecialization());
            pstmt.setString(3, doctor.getLicenceNumber());
            pstmt.setString(4, formatDays(doctor.getAvailableDays()));
            pstmt.setString(5, formatHours(doctor.getAvailableHours()));
            pstmt.setString(6, doctor.getDoctorId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating doctor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDoctor(String doctorId) {
        String sql = "DELETE FROM Doctor WHERE doctorId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, doctorId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting doctor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Doctor mapResultSetToDoctor(ResultSet rs) throws SQLException {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(rs.getString("doctorId"));
        doctor.setUserId(rs.getInt("userId"));
        doctor.setSpecialization(rs.getString("specialization"));
        doctor.setLicenceNumber(rs.getString("licenseNumber"));
        doctor.setAvailableDays(parseDays(rs.getString("availableDays")));
        doctor.setAvailableHours(parseHours(rs.getString("availableHours")));
        return doctor;
    }
}
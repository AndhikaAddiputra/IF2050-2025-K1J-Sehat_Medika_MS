package model.entity;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;


public class Doctor {
    private String doctorId;
    private int userId;
    private String specialization;
    private String licenseNumber;
    private List<DayOfWeek> availableDays;
    private List<LocalTime[]> availableHours;

    public Doctor() {}
    public Doctor(String doctorId, int userId, String specialization, String licenceNumber, List<DayOfWeek> availableDays, List<LocalTime[]> availableHours){
        this.doctorId = doctorId;
        this.userId = userId;
        this.specialization = specialization;
        this.licenseNumber = licenceNumber;
        this.availableDays = availableDays;
        this.availableHours = availableHours;
    }

    public String getDoctorId(){ return doctorId; }
    public int getUserId(){ return userId; }
    public String getSpecialization(){ return specialization; }
    public String getLicenceNumber(){ return licenseNumber; }
    public List<DayOfWeek> getAvailableDays(){ return availableDays; }
    public List<LocalTime[]> getAvailableHours(){ return availableHours; }

    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setLicenceNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public void setAvailableDays(List<DayOfWeek> availableDays) { this.availableDays = availableDays; }
    public void setAvailableHours(List<LocalTime[]> availableHours) { this.availableHours = availableHours; }

    @Override
    public String toString() {
        return "Doctor{" +
            "doctorId='" + doctorId + '\'' +
            ", userId=" + userId +
            ", specialization='" + specialization + '\'' +
            ", licenseNumber='" + licenseNumber + '\'' +
            ", availableDays=" + availableDays +
            ", availableHours=" + Arrays.deepToString(availableHours.toArray()) +
            '}';
    }
}

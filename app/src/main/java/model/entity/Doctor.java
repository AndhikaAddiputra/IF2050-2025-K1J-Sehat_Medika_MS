package model.entity;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class Doctor {
    private String doctorId;
    private int userId;
    private String fullName;
    private int salaryDoctor;
    private String specialization;
    private String licenseNumber;
    private List<DayOfWeek> availableDays;
    private List<LocalTime[]> availableHours;

    public Doctor() {}

    public Doctor(String doctorId, int userId, String fullName, int salaryDoctor, String specialization, String licenseNumber, List<DayOfWeek> availableDays, List<LocalTime[]> availableHours){
        this.doctorId = doctorId;
        this.userId = userId;
        this.fullName = fullName;
        this.salaryDoctor = salaryDoctor;
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.availableDays = availableDays;
        this.availableHours = availableHours;
    }

    public String getDoctorId(){ return doctorId; }
    public int getUserId(){ return userId; }
    public String getFullName(){ return fullName; }
    public int getSalaryDoctor() { return salaryDoctor; }
    public String getSpecialization(){ return specialization; }
    public String getLicenseNumber(){ return licenseNumber; }
    public List<DayOfWeek> getAvailableDays(){ return availableDays; }
    public List<LocalTime[]> getAvailableHours(){ return availableHours; }

    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setSalaryDoctor(int salaryDoctor) { this.salaryDoctor = salaryDoctor; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public void setAvailableDays(List<DayOfWeek> availableDays) { this.availableDays = availableDays; }
    public void setAvailableHours(List<LocalTime[]> availableHours) { this.availableHours = availableHours; }

    @Override
    public String toString() {
        return fullName != null ? fullName : super.toString();
    }
}

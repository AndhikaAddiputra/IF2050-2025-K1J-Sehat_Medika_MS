package model.entity;

import java.time.LocalDateTime;


public class Patient {
    private String patientId;
    private String userId;
    private BloodType bloodType;
    private String allergies;
    private String emergencyContact;
    private String insuranceInfo;
    private LocalDateTime registrationDate;

    public Patient() {}

    public Patient(String patientId, String userId, BloodType bloodType, String allergies, String emergencyContact, String insuranceInfo, LocalDateTime registrationDate) {
        this.patientId = patientId;
        this.userId = userId;
        this.bloodType = bloodType;
        this.allergies = allergies;
        this.emergencyContact = emergencyContact;
        this.insuranceInfo = insuranceInfo;
        this.registrationDate = registrationDate;
    }

    public String getPatientId() {
        return patientId;
    }
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public BloodType getBloodType() {
        return bloodType;
    }
    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }
    public String getAllergies() {
        return allergies;
    }
    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }
    public String getEmergencyContact() {
        return emergencyContact;
    }
    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
    public String getInsuranceInfo() {
        return insuranceInfo;
    }
    public void setInsuranceInfo(String insuranceInfo) {
        this.insuranceInfo = insuranceInfo;
    }
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "patientId='" + patientId + '\'' +
                ", userId='" + userId + '\'' +
                ", bloodType=" + bloodType +
                ", allergies='" + allergies + '\'' +
                ", emergencyContact='" + emergencyContact + '\'' +
                ", insuranceInfo='" + insuranceInfo + '\'' +
                ", registrationDate=" + registrationDate +
                '}';
    }
}

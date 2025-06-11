package model.entity;

import java.time.LocalDateTime;


public class Patient {
    private String patientId;
    private String userId;
    private String fullname;
    private BloodType bloodType;
    private String allergies;
    private int height;
    private int weight;
    private String emergencyContact;
    private String insuranceInfo;
    private String insuranceNumber;
    private LocalDateTime registrationDate;
    
    public Patient(){}

    public Patient(String patientId, String userId, String fullname, BloodType bloodType, String allergies, int height, int weight, String emergencyContact, String insuranceInfo, String insuranceNumber, LocalDateTime registrationDate) {
        this.patientId = patientId;
        this.userId = userId;
        this.fullname = fullname;
        this.bloodType = bloodType;
        this.allergies = allergies;
        this.height = height;
        this.weight = weight;
        this.emergencyContact = emergencyContact;
        this.insuranceInfo = insuranceInfo;
        this.insuranceNumber = insuranceNumber;
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

    public String getFullname(){
        return fullname;
    }

    public void setFullName(String fullname){
        this.fullname = fullname;
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

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
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

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
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
            ", fullname='" + fullname + '\'' +
            ", bloodType=" + bloodType +
            ", allergies='" + allergies + '\'' +
            ", height=" + height +
            ", weight=" + weight +
            ", emergencyContact='" + emergencyContact + '\'' +
            ", insuranceInfo='" + insuranceInfo + '\'' +
            ", insuranceNumber='" + insuranceNumber + '\'' +
            ", registrationDate=" + registrationDate +
            '}';
    }
}

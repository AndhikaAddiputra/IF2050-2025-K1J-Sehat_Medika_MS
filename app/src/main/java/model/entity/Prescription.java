package model.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Prescription {
    private int prescriptionId;
    private String patientId;
    private String doctorId;
    private String prescriptionDate; // For backward compatibility
    private LocalDateTime createdAt; // Actual timestamp
    private String medications;
    private String instructions;
    private String status; // PENDING, PROCESSING, COMPLETED

    public Prescription(int prescriptionId, String patientId, String doctorId, String prescriptionDate, String medications, String instructions) {
        this.prescriptionId = prescriptionId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.prescriptionDate = prescriptionDate;
        this.medications = medications;
        this.instructions = instructions;
        this.status = "PENDING";
    }
    
    public Prescription() {
        this.status = "PENDING";
    }
    
    public int getPrescriptionId() {
        return prescriptionId;
    }
    
    public void setPrescriptionId(int prescriptionId) {
        this.prescriptionId = prescriptionId;
    }
    
    public String getPrescriptionDate() {
        return prescriptionDate;
    }
    
    public void setPrescriptionDate(String prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public String getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        if (createdAt != null) {
            // Update the string prescriptionDate for backward compatibility
            this.prescriptionDate = createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }
    
    // Method to handle string input for createdAt
    public void setCreatedAt(String dateTime) {
        this.prescriptionDate = dateTime;
        try {
            // Try to parse string to LocalDateTime if it follows standard format
            this.createdAt = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            // If parsing fails, set createdAt to now
            this.createdAt = LocalDateTime.now();
        }
    }
    
    public String getMedications() {
        return medications;
    }
    
    public void setMedications(String medications) {
        this.medications = medications;
    }
    
    public String getInstructions() {
        return instructions;
    }
    
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    // Helper method to check if prescription is in processing state
    public boolean isProcessing() {
        return "PROCESSING".equals(status);
    }
    
    // Helper method to check if prescription is completed
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
    
    // Helper method to check if prescription is pending
    public boolean isPending() {
        return "PENDING".equals(status) || status == null;
    }
    
    @Override
    public String toString() {
        return "Prescription{" +
            "prescriptionId=" + prescriptionId +
            ", patientId='" + patientId + '\'' +
            ", doctorId='" + doctorId + '\'' +
            ", prescriptionDate='" + prescriptionDate + '\'' +
            ", createdAt=" + createdAt +
            ", medications='" + medications + '\'' +
            ", instructions='" + instructions + '\'' +
            ", status='" + status + '\'' +
            '}';
    }
}
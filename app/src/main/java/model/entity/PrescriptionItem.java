package model.entity;

import java.time.LocalDateTime;

public class PrescriptionItem {
    private int itemId;
    private int prescriptionId;
    private String dosage;
    private String frequency;
    private String duration;
    private String notes;
    private String medicationName; // Added to store medication name for display
    private int quantity; // Added to track prescription quantity
    private LocalDateTime dispensedDate; // Added to track when dispensed
    private String status; // PENDING, PREPARED, DISPENSED

    public PrescriptionItem(int itemId, int prescriptionId, String dosage, String frequency, String duration, String notes) {
        this.itemId = itemId;
        this.prescriptionId = prescriptionId;
        this.dosage = dosage;
        this.frequency = frequency;
        this.duration = duration;
        this.notes = notes;
        this.status = "PENDING";
    }

    public PrescriptionItem() {
        this.status = "PENDING";
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(int prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getDispensedDate() {
        return dispensedDate;
    }

    public void setDispensedDate(LocalDateTime dispensedDate) {
        this.dispensedDate = dispensedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Helper method to check if the item is pending
    public boolean isPending() {
        return "PENDING".equals(status) || status == null;
    }

    // Helper method to check if the item is prepared
    public boolean isPrepared() {
        return "PREPARED".equals(status);
    }

    // Helper method to check if the item is dispensed
    public boolean isDispensed() {
        return "DISPENSED".equals(status);
    }

    // Helper method to get the full medication details including dosage
    public String getFullMedicationDetails() {
        StringBuilder sb = new StringBuilder();
        if (medicationName != null && !medicationName.isEmpty()) {
            sb.append(medicationName);
        }
        if (dosage != null && !dosage.isEmpty()) {
            sb.append(" ").append(dosage);
        }
        return sb.toString();
    }

    // Helper method to get the full administration instructions
    public String getFullInstructions() {
        StringBuilder sb = new StringBuilder();
        if (frequency != null && !frequency.isEmpty()) {
            sb.append(frequency);
        }
        if (duration != null && !duration.isEmpty()) {
            sb.append(", for ").append(duration);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "PrescriptionItem{" +
            "itemId=" + itemId +
            ", prescriptionId=" + prescriptionId +
            ", dosage='" + dosage + '\'' +
            ", frequency='" + frequency + '\'' +
            ", duration='" + duration + '\'' +
            ", notes='" + notes + '\'' +
            ", medicationName='" + medicationName + '\'' +
            ", quantity=" + quantity +
            ", dispensedDate=" + dispensedDate +
            ", status='" + status + '\'' +
            '}';
    }
}
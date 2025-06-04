package model.entity;

public class PrescriptionItem {
    private int itemId;
    private int prescriptionId;
    private String dosage;
    private String frequency;
    private String duration;
    private String notes;

    public PrescriptionItem(int itemId, int prescriptionId, String dosage, String frequency, String duration, String notes) {
        this.itemId = itemId;
        this.prescriptionId = prescriptionId;
        this.dosage = dosage;
        this.frequency = frequency;
        this.duration = duration;
        this.notes = notes;
    }
    public PrescriptionItem() {}
    public int getItemId() {
        return itemId;
    }
    public int getPrescriptionId() {
        return prescriptionId;
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
    @Override
    public String toString() {
        return "PrescriptionItem{" +
            "itemId=" + itemId +
            ", prescriptionId=" + prescriptionId +
            ", dosage='" + dosage + '\'' +
            ", frequency='" + frequency + '\'' +
            ", duration='" + duration + '\'' +
            ", notes='" + notes + '\'' +
            '}';
    }
}

package model.entity;

public class Prescription {
    private int prescriptionId;
    private String patientId;
    private String doctorId;
    private String prescriptionDate;
    private String medications;
    private String instructions;

    public Prescription(int prescriptionId, int patientId, String doctorId, String prescriptionDate, String medications, String instructions) {
        this.prescriptionId = prescriptionId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.prescriptionDate = prescriptionDate;
        this.medications = medications;
        this.instructions = instructions;
    }
    public Prescription() {}
    public int getPrescriptionId() {
        return prescriptionId;
    }
    public String getPrescriptionDate() {
        return prescriptionDate;
    }
    public String getPatientId() {
        return patientId;
    }
    public String getDoctorId() {
        return doctorId;
    }
    public void setPrescriptionDate(String prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
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
    @Override
    public String toString() {
        return "Prescription{" +
           "prescriptionId=" + prescriptionId +
           ", patientId='" + patientId + '\'' +
           ", doctorId='" + doctorId + '\'' +
           ", prescriptionDate='" + prescriptionDate + '\'' +
           ", medications='" + medications + '\'' +
           ", instructions='" + instructions + '\'' +
           '}';
    }
}

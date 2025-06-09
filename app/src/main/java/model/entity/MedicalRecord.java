package model.entity;

import java.time.LocalDateTime;

public class MedicalRecord {
    private int recordID;
    private String patientId;
    private String doctorId;
    private LocalDateTime recordDate;
    private String diagnosis;
    private String symptoms;
    private String notes;
    private String attachments;


    public MedicalRecord() {}

    public MedicalRecord(int recordID, String patientId, String doctorId, LocalDateTime recordDate, String diagnosis, String symptoms, String notes, String attachments) {
        this.recordID = recordID;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.recordDate = recordDate;
        this.diagnosis = diagnosis;
        this.symptoms = symptoms;
        this.notes = notes;
        this.attachments = attachments;
    }

    public int getRecordID() {
        return recordID;
    }
    public void setRecordID(int recordID) {
        this.recordID = recordID;
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
    public LocalDateTime getRecordDate() {
        return recordDate;
    }
    public void setRecordDate(LocalDateTime recordDate) {
        this.recordDate = recordDate;
    }
    public String getDiagnosis() {
        return diagnosis;
    }
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
    public String getSymptoms() {
        return symptoms;
    }
    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getAttachments() {
        return attachments;
    }
    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "MedicalRecord{" +
                "recordID=" + recordID +
                ", patientId='" + patientId + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", recordDate=" + recordDate +
                ", diagnosis='" + diagnosis + '\'' +
                ", symptoms='" + symptoms + '\'' +
                ", notes='" + notes + '\'' +
                ", attachments='" + attachments + '\'' +
                '}';
    }
}

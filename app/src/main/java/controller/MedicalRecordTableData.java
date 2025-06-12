package controller;

public class MedicalRecordTableData {
    private int recordId;
    private String patientName;
    private String recordDate;
    private String diagnosis;
    private String symptoms;
    private String treatment;
    
    public MedicalRecordTableData() {}
    
    public MedicalRecordTableData(int recordId, String patientName, String recordDate, String diagnosis, String symptoms, String treatment) {
        this.recordId = recordId;
        this.patientName = patientName;
        this.recordDate = recordDate;
        this.diagnosis = diagnosis;
        this.symptoms = symptoms;
        this.treatment = treatment;
    }

    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }
    
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    
    public String getRecordDate() { return recordDate; }
    public void setRecordDate(String recordDate) { this.recordDate = recordDate; }
    
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    
    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    
    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }
}
package model.entity;

import java.time.LocalDateTime;

public class Appointment {
    private int appointmentId;
    private String patientId;
    private String doctorId;
    private LocalDateTime appointmentDate;
    private int duration; 
    private String reason;
    private AppointmentStatus appointmentStatus;
    private int queueNumber;

    public Appointment() {}

    public Appointment(int appointmentId, String patientId, String doctorId, LocalDateTime appointmentDate, int duration, String reason, AppointmentStatus appointmentStatus, int queueNumber, boolean doctorConfirmation) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.duration = duration;
        this.reason = reason;
        this.appointmentStatus = appointmentStatus;
        this.queueNumber = queueNumber;
    }

    public int getAppointmentId() {
        return appointmentId;
    }
    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
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
    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }
    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public AppointmentStatus getAppointmentStatus() {
        return appointmentStatus;
    }
    public void setAppointmentStatus(AppointmentStatus appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }
    public int getQueueNumber() {
        return queueNumber;
    }
    public void setQueueNumber(int queueNumber) {
        this.queueNumber = queueNumber;
    }
    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId=" + appointmentId +
                ", patientId='" + patientId + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", appointmentDate=" + appointmentDate +
                ", duration=" + duration +
                ", reason='" + reason + '\'' +
                ", status=" + appointmentStatus +
                ", queueNumber=" + queueNumber +
                '}';
    }

}

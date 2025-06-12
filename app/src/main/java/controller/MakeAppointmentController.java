package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import model.dao.AppointmentDAO;
import model.dao.DoctorDAO;
import model.dao.PatientDAO;
import model.dao.UserDAO;
import model.entity.Appointment;
import model.entity.AppointmentStatus;
import model.entity.Doctor;
import model.entity.Patient;
import model.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class MakeAppointmentController {

    @FXML private ComboBox<String> pilihanSpesialis;
    @FXML private ComboBox<Doctor> pilihanDokter;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> pilihanWaktu;
    @FXML private Button simpanButton;
    @FXML private Button batalkanButton;

    private DoctorDAO doctorDAO = new DoctorDAO();
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private UserDAO userDAO = new UserDAO();
    private User currentUser;

    @FXML
    public void initialize() {
        loadSpecializations();
        setupTimeSlots();
        setupEventHandlers();
    }

    public void setUser(User user) {
        this.currentUser = user;
    }

    private void loadSpecializations() {
        try {
            List<String> specializations = doctorDAO.getAllSpecializations();
            ObservableList<String> specializationList = FXCollections.observableArrayList(specializations);
            pilihanSpesialis.setItems(specializationList);
        } catch (Exception e) {
            showError("Error loading specializations: " + e.getMessage());
        }
    }

    private void setupTimeSlots() {
        ObservableList<String> timeSlots = FXCollections.observableArrayList(
            "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
            "11:00", "11:30", "13:00", "13:30", "14:00", "14:30",
            "15:00", "15:30", "16:00", "16:30", "17:00"
        );
        pilihanWaktu.setItems(timeSlots);
    }

    private void setupEventHandlers() {
        pilihanSpesialis.setOnAction(e -> loadDoctorsBySpecialization());
    }

    private void loadDoctorsBySpecialization() {
        String selectedSpecialization = pilihanSpesialis.getValue();
        if (selectedSpecialization != null) {
            try {
                List<Doctor> doctors = doctorDAO.getDoctorsBySpecialization(selectedSpecialization);
                ObservableList<Doctor> doctorList = FXCollections.observableArrayList(doctors);
                pilihanDokter.setItems(doctorList);
            } catch (Exception e) {
                showError("Error loading doctors: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSimpan(ActionEvent event) {
        if (!validateInput()) {
            return;
        }

        try {
            Patient patient = patientDAO.getPatientByUserId(currentUser.getUserId());
            if (patient == null) {
                showError("Patient information not found.");
                return;
            }

            Appointment appointment = new Appointment();
            appointment.setPatientId(patient.getPatientId());
            appointment.setDoctorId(pilihanDokter.getValue().getDoctorId());

            LocalDate selectedDate = datePicker.getValue();
            LocalTime selectedTime = LocalTime.parse(pilihanWaktu.getValue());
            appointment.setAppointmentDate(LocalDateTime.of(selectedDate, selectedTime));

            appointment.setDuration(30);
            appointment.setReason("General consultation");
            appointment.setAppointmentStatus(AppointmentStatus.REQUESTED); // Set status ke REQUESTED
            appointment.setQueueNumber(getNextQueueNumber(selectedDate));
            // Baris 'appointment.setDoctorConfirmation(false);' dihapus karena tidak ada di DB

            appointmentDAO.addAppointment(appointment);

            showSuccess("Appointment created successfully!");
            closeWindow();
        } catch (Exception e) {
            showError("Error creating appointment: " + e.getMessage());
        }
    }

    @FXML
    private void handleBatalkan(ActionEvent event) {
        closeWindow();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }

    public String validateInput(String specialization, Doctor doctor, LocalDate date, String time) {
        if (specialization == null) {
            return "Please select a specialization.";
        }
        if (doctor == null) {
            return "Please select a doctor.";
        }
        if (date == null) {
            return "Please select a date.";
        }
        if (date.isBefore(LocalDate.now())) {
            return "Please select a future date.";
        }
        if (time == null) {
            return "Please select a time.";
        }
        return null;
    }

    private boolean validateInput() {
        if (pilihanSpesialis.getValue() == null) {
            showError("Please select a specialization.");
            return false;
        }
        if (pilihanDokter.getValue() == null) {
            showError("Please select a doctor.");
            return false;
        }
        if (datePicker.getValue() == null) {
            showError("Please select a date.");
            return false;
        }
        if (datePicker.getValue().isBefore(LocalDate.now())) {
            showError("Please select a future date.");
            return false;
        }
        if (pilihanWaktu.getValue() == null) {
            showError("Please select a time.");
            return false;
        }
        return true;
    }

    public int getNextQueueNumber(LocalDate date) {
        try {
            List<Appointment> appointmentsOnDate = appointmentDAO.getAppointmentsByDate(date.atStartOfDay());
            return appointmentsOnDate.size() + 1;
        } catch (Exception e) {
            System.err.println("Error getting next queue number: " + e.getMessage());
            return 1;
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) simpanButton.getScene().getWindow();
        stage.close();
    }
}

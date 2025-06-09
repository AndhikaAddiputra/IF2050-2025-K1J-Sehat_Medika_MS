package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.dao.AppointmentDAO;
import model.dao.PatientDAO;
import model.dao.UserDAO;
import model.entity.Appointment;
import model.entity.AppointmentStatus;
import model.entity.Patient;
import model.entity.User;
import view.LoginView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AppointmentReceptionistController {

    @FXML private Button profilSidebarButton;
    @FXML private Button janjiTemuSidebarButton;
    @FXML private Button daftarPasienBaruSidebarButton;
    @FXML private Button jadwalDokterSidebarButton;
    @FXML private Button notifikasiSidebarButton;
    @FXML private Button keluarSidebarButton;
    
    @FXML private TableView<ReceptionistAppointmentTableData> dataAppointmentTable;
    @FXML private TableColumn<ReceptionistAppointmentTableData, String> colPatientName;
    @FXML private TableColumn<ReceptionistAppointmentTableData, String> colDate;
    @FXML private TableColumn<ReceptionistAppointmentTableData, String> colTime;
    @FXML private TableColumn<ReceptionistAppointmentTableData, String> colMedicalRecord;
    @FXML private TableColumn<ReceptionistAppointmentTableData, String> colDiagnose;
    @FXML private TableColumn<ReceptionistAppointmentTableData, String> colPrescribe;
    
    @FXML private ToggleButton todayToggle;
    @FXML private ToggleButton soonToggle;
    @FXML private Button addAppointmentButton;

    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private UserDAO userDAO = new UserDAO();
    private User currentUser;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupToggleButtons();
        loadAppointments();
    }

    public void setUser(User user) {
        this.currentUser = user;
        loadAppointments();
    }

    private void setupTableColumns() {
        colPatientName.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colMedicalRecord.setCellValueFactory(new PropertyValueFactory<>("medicalRecordInfo"));
        colDiagnose.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
        colPrescribe.setCellValueFactory(new PropertyValueFactory<>("prescriptionInfo"));
    }

    private void setupToggleButtons() {
        ToggleGroup filterGroup = new ToggleGroup();
        todayToggle.setToggleGroup(filterGroup);
        soonToggle.setToggleGroup(filterGroup);
        todayToggle.setSelected(true);
    }

    private void loadAppointments() {
        try {
            List<Appointment> appointments = appointmentDAO.getAllAppointments();
            if (todayToggle.isSelected()) {
                appointments = appointments.stream()
                        .filter(a -> a.getAppointmentDate().toLocalDate().equals(LocalDate.now()))
                        .collect(Collectors.toList());
            } 
            else if (soonToggle.isSelected()) {
                appointments = appointments.stream()
                        .filter(a -> a.getAppointmentDate().toLocalDate().isAfter(LocalDate.now()))
                        .collect(Collectors.toList());
            }
            ObservableList<ReceptionistAppointmentTableData> tableData = FXCollections.observableArrayList();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
            for (Appointment a : appointments) {
                ReceptionistAppointmentTableData data = new ReceptionistAppointmentTableData();
                Patient patient = patientDAO.getPatientById(a.getPatientId());
                String patientName = "Unknown";
                if (patient != null) {
                    User pUser = userDAO.getUserById(patient.getUserId());
                    if (pUser != null) {
                        patientName = pUser.getUsername();
                    }
                }
                data.setPatientName(patientName);
                data.setDate(a.getAppointmentDate().format(dateFormat));
                data.setTime(a.getAppointmentDate().format(timeFormat));
                data.setMedicalRecordInfo("N/A");
                data.setDiagnosis("N/A");
                data.setPrescriptionInfo("N/A");
                data.setAppointment(a);
                tableData.add(data);
            }
            dataAppointmentTable.setItems(tableData);
        } catch (Exception e) {
            showError("Error loading appointments: " + e.getMessage());
        }
    }

    @FXML
    private void handleTodayFilter(ActionEvent event) {
        loadAppointments();
    }

    @FXML
    private void handleSoonFilter(ActionEvent event) {
        loadAppointments();
    }

    @FXML
    private void handleAddAppointmentClick(ActionEvent event) {
        System.out.println("Add appointment clicked");
    }

    @FXML
    private void handleProfilClick(ActionEvent event) {
        System.out.println("Profile clicked");
    }

    @FXML
    private void handleJanjiTemuClick(ActionEvent event) {
        System.out.println("Appointment page clicked");
    }

    @FXML
    private void handleDaftarPasienBaruClick(ActionEvent event) {
        System.out.println("Register patient clicked");
    }

    @FXML
    private void handleJadwalDokterClick(ActionEvent event) {
        System.out.println("Doctor schedule clicked");
    }
    
    @FXML
    private void handleNotifikasiClick(ActionEvent event) {
        System.out.println("Notifications clicked");
    }
    
    @FXML
    private void handleKeluarClick(ActionEvent event) {
        try {
            Stage stage = (Stage) keluarSidebarButton.getScene().getWindow();
            stage.close();
            LoginView loginView = new LoginView();
            Stage loginStage = new Stage();
            loginView.start(loginStage);
        } 
        catch (Exception e) {
            showError("Error logging out: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class ReceptionistAppointmentTableData {
        private SimpleStringProperty patientName = new SimpleStringProperty();
        private SimpleStringProperty date = new SimpleStringProperty();
        private SimpleStringProperty time = new SimpleStringProperty();
        private SimpleStringProperty medicalRecordInfo = new SimpleStringProperty();
        private SimpleStringProperty diagnosis = new SimpleStringProperty();
        private SimpleStringProperty prescriptionInfo = new SimpleStringProperty();
        private Appointment appointment;

        public String getPatientName() { return patientName.get(); }
        public void setPatientName(String name) { this.patientName.set(name); }
        public SimpleStringProperty patientNameProperty() { return patientName; }

        public String getDate() { return date.get(); }
        public void setDate(String date) { this.date.set(date); }
        public SimpleStringProperty dateProperty() { return date; }

        public String getTime() { return time.get(); }
        public void setTime(String time) { this.time.set(time); }
        public SimpleStringProperty timeProperty() { return time; }

        public String getMedicalRecordInfo() { return medicalRecordInfo.get(); }
        public void setMedicalRecordInfo(String info) { this.medicalRecordInfo.set(info); }
        public SimpleStringProperty medicalRecordInfoProperty() { return medicalRecordInfo; }

        public String getDiagnosis() { return diagnosis.get(); }
        public void setDiagnosis(String diag) { this.diagnosis.set(diag); }
        public SimpleStringProperty diagnosisProperty() { return diagnosis; }

        public String getPrescriptionInfo() { return prescriptionInfo.get(); }
        public void setPrescriptionInfo(String info) { this.prescriptionInfo.set(info); }
        public SimpleStringProperty prescriptionInfoProperty() { return prescriptionInfo; }

        public Appointment getAppointment() { return appointment; }
        public void setAppointment(Appointment appointment) { this.appointment = appointment; }
    }
}
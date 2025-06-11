package controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.dao.AppointmentDAO;
import model.dao.DoctorDAO;
import model.dao.PatientDAO;
import model.entity.Appointment;
import model.entity.AppointmentStatus;
import model.entity.Doctor;
import model.entity.Patient;
import model.entity.User;
import view.LoginView;

import java.time.LocalDateTime;

public class AppointmentPatientController {

    @FXML private Button dashboardSidebarButton;
    @FXML private Button profilSidebarButton;
    @FXML private Button riwayatMedisSidebarButton;
    @FXML private Button resepObatSidebarButton;
    @FXML private Button notifikasiSidebarButton;
    @FXML private Button keluarSidebarButton;
    @FXML private Button newAppointmentButton;

    @FXML private TableView<AppointmentTableData> dataAppointmentTable;
    @FXML private TableColumn<AppointmentTableData, String> colDoctorName;
    @FXML private TableColumn<AppointmentTableData, String> colSpesialist;
    @FXML private TableColumn<AppointmentTableData, String> colDate;
    @FXML private TableColumn<AppointmentTableData, String> colTime;
    @FXML private TableColumn<AppointmentTableData, String> colAction;

    @FXML private ToggleButton aktifToggle;
    @FXML private ToggleButton selesaiToggle;
    @FXML private ToggleButton batalToggle;

    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private DoctorDAO doctorDAO = new DoctorDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private User currentUser;
    private Patient currentPatient;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupToggleButtons();
    }

    public void setUser(User user) {
        this.currentUser = user;
        loadPatientData();
        loadAppointments();
    }

    private void loadPatientData() {
        try {
            currentPatient = patientDAO.getPatientByUserId(currentUser.getUserId());
        } catch (Exception e) {
            showError("Error loading patient data: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        colDoctorName.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        colSpesialist.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("action"));
    }

    private void setupToggleButtons() {
        ToggleGroup filterGroup = new ToggleGroup();
        aktifToggle.setToggleGroup(filterGroup);
        selesaiToggle.setToggleGroup(filterGroup);
        batalToggle.setToggleGroup(filterGroup);
        aktifToggle.setSelected(true);
    }

    private void loadAppointments() {
        if (currentPatient == null) return;
        try {
            List<Appointment> appointments = appointmentDAO.getAppointmentsByPatientId(currentPatient.getPatientId());
            List<Appointment> filteredAppointments = filterAppointments(appointments);

            ObservableList<AppointmentTableData> tableData = FXCollections.observableArrayList();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

            for (Appointment appointment : filteredAppointments) {
                Doctor doctor = doctorDAO.getDoctorById(appointment.getDoctorId());
                AppointmentTableData data = new AppointmentTableData();
                data.setDoctorName(doctor != null ? doctorDAO.getDoctorNameById(doctor.getDoctorId()) : "Unknown");
                data.setSpecialization(doctor != null ? doctor.getSpecialization() : "Unknown");
                data.setDate(appointment.getAppointmentDate().format(dateFormat));
                data.setTime(appointment.getAppointmentDate().format(timeFormat));
                data.setMedicalRecordInfo("N/A");
                data.setDiagnosis("N/A");
                data.setPrescriptionInfo("N/A");
                data.setAppointment(appointment);

                tableData.add(data);
            }
            dataAppointmentTable.setItems(tableData);
        }
        catch (Exception e) {
            showError("Error loading appointments: " + e.getMessage());
        }
    }

    private List<Appointment> filterAppointments(List<Appointment> appointments) {
        if (aktifToggle.isSelected()) {
            return appointments.stream()
                    .filter(a -> (a.getAppointmentStatus() == AppointmentStatus.REQUESTED || a.getAppointmentStatus() == AppointmentStatus.ACCEPTED)
                                 && a.getAppointmentDate().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList());
        } else if (selesaiToggle.isSelected()) {
            return appointments.stream()
                    .filter(a -> a.getAppointmentStatus() == AppointmentStatus.ACCEPTED
                                 && a.getAppointmentDate().isBefore(LocalDateTime.now()))
                    .collect(Collectors.toList());
        } else if (batalToggle.isSelected()) {
            return appointments.stream()
                    .filter(a -> false)
                    .collect(Collectors.toList());
        }
        return appointments;
    }

    @FXML
    private void handleAktifFilter(ActionEvent event) {
        loadAppointments();
    }

    @FXML
    private void handleSelesaiFilter(ActionEvent event) {
        loadAppointments();
    }

    @FXML
    private void handleBatalFilter(ActionEvent event) {
        loadAppointments();
    }

    @FXML
    private void handleNewAppointmentClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MakeAppointmentPatient.fxml"));
            Parent root = loader.load();

            MakeAppointmentController controller = loader.getController();
            controller.setUser(currentUser);

            Stage stage = new Stage();
            stage.setTitle("Buat Janji Temu - Klinik Sehat Medika");
            stage.setScene(new Scene(root, 600, 600));
            stage.show();
            stage.setOnHidden(e -> loadAppointments());
        } catch (Exception e) {
            showError("Error opening appointment form: " + e.getMessage());
        }
    }

    @FXML
    private void handleDashboardClick(ActionEvent event) {
        navigateToPatientDashboard();
    }

    @FXML
    private void handleProfilClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PatientProfile.fxml"));
            Parent root = loader.load();

            PatientProfileController controller = loader.getController();
            controller.setUser(currentUser);

            Stage currentStage = (Stage) profilSidebarButton.getScene().getWindow();
            currentStage.close();

            Stage newStage = new Stage();
            newStage.setTitle("Profil Pasien - Klinik Sehat Medika");
            newStage.setScene(new Scene(root, 1200, 800));
            newStage.show();

        } catch (Exception e) {
            showError("Error opening profile: " + e.getMessage());
        }
    }

    @FXML
    private void handleRiwayatMedisClick(ActionEvent event) {
        System.out.println("Medical records clicked");
    }

    @FXML
    private void handleResepObatClick(ActionEvent event) {
        System.out.println("Prescription clicked");
    }

    @FXML
    private void handleNotifikasiClick(ActionEvent event) {
        System.out.println("Notifications clicked");
    }

    @FXML
    private void handleKeluarClick(ActionEvent event) {
        try {
        Stage currentStage = (Stage) keluarSidebarButton.getScene().getWindow();
        currentStage.close();

        LoginView loginView = new LoginView();
        Stage loginStage = new Stage();
        loginView.start(loginStage);
        }
        catch (Exception e) {
            System.err.println("Error switching to login view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateToPatientDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/patientDashboard.fxml"));
            Parent root = loader.load();

            PatientDashboardController controller = loader.getController();
            controller.setUser(currentUser);

            Stage currentStage = (Stage) keluarSidebarButton.getScene().getWindow();
            currentStage.close();

            Stage newStage = new Stage();
            newStage.setTitle("Dashboard Pasien - Klinik Sehat Medika");
            newStage.setScene(new Scene(root, 1200, 800));
            newStage.show();

        } catch (Exception e) {
            showError("Error returning to dashboard: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class AppointmentTableData {
        private SimpleStringProperty doctorName = new SimpleStringProperty();
        private SimpleStringProperty specialization = new SimpleStringProperty();
        private SimpleStringProperty date = new SimpleStringProperty();
        private SimpleStringProperty time = new SimpleStringProperty();
        private SimpleStringProperty medicalRecordInfo = new SimpleStringProperty();
        private SimpleStringProperty diagnosis = new SimpleStringProperty();
        private SimpleStringProperty prescriptionInfo = new SimpleStringProperty();
        private Appointment appointment; // Objek Appointment penuh

        public String getDoctorName() { return doctorName.get(); }
        public void setDoctorName(String name) { this.doctorName.set(name); }
        public SimpleStringProperty doctorNameProperty() { return doctorName; }

        public String getSpecialization() { return specialization.get(); }
        public void setSpecialization(String spec) { this.specialization.set(spec); }
        public SimpleStringProperty specializationProperty() { return specialization; }

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

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
import javafx.scene.control.TableCell; 
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.util.Callback; 
import javafx.scene.control.cell.PropertyValueFactory; 

import model.dao.AppointmentDAO;
import model.dao.DoctorDAO;
import model.dao.PatientDAO;
import model.entity.Appointment;
import model.entity.AppointmentStatus;
import model.entity.Doctor;
import model.entity.Patient;
import model.entity.User;
import view.LoginView;
import java.sql.SQLException; 

import java.time.LocalDateTime;
import java.util.ArrayList; 

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
    @FXML private TableColumn<AppointmentTableData, Void> colAction; 

    @FXML private ToggleButton aktifToggle;
    @FXML private ToggleButton selesaiToggle;

    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private DoctorDAO doctorDAO = new DoctorDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private User currentUser;
    private Patient currentPatient;

    private List<Appointment> allAppointmentsInMemory = new ArrayList<>();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupToggleButtons();
    }

    public void setUser(User user) {
        this.currentUser = user;
        loadPatientData();
        loadAllAppointmentsFromDb();
        loadAppointments(); 
    }

    private void loadPatientData() {
        try {
            currentPatient = patientDAO.getPatientByUserId(currentUser.getUserId());
        } catch (Exception e) {
            showError("Error loading patient data: " + e.getMessage());
        }
    }


    private void loadAllAppointmentsFromDb() {
        if (currentPatient == null) return;
        try {
            allAppointmentsInMemory = appointmentDAO.getAppointmentsByPatientId(currentPatient.getPatientId());
        } catch (Exception e) {
            showError("Error loading all appointments from database: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        colDoctorName.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        colSpesialist.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colAction.setCellFactory(new Callback<TableColumn<AppointmentTableData, Void>, TableCell<AppointmentTableData, Void>>() {
            @Override
            public TableCell<AppointmentTableData, Void> call(final TableColumn<AppointmentTableData, Void> param) {
                final TableCell<AppointmentTableData, Void> cell = new TableCell<AppointmentTableData, Void>() {
                    private final Button batalButton = new Button("Batal"); 

                    { 
                        batalButton.setOnAction(event -> {
                            AppointmentTableData rowData = getTableView().getItems().get(getIndex());
                            handleCancelAppointment(rowData.getAppointment());
                        });
                        batalButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                    }
                    

                    @Override
                    public void updateItem(Void item, boolean empty) { 
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            AppointmentTableData rowData = getTableView().getItems().get(getIndex());
                            if (rowData != null && (rowData.getAppointment().getAppointmentStatus() == AppointmentStatus.REQUESTED ||
                                                    rowData.getAppointment().getAppointmentStatus() == AppointmentStatus.ACCEPTED) &&
                                                    rowData.getAppointment().getAppointmentDate().isAfter(LocalDateTime.now())) {
                                setGraphic(batalButton);
                            } else {
                                setGraphic(null); 
                            }
                        }
                    }
                };
                return cell;
            }
        });
    }

    private void setupToggleButtons() {
        ToggleGroup filterGroup = new ToggleGroup();
        aktifToggle.setToggleGroup(filterGroup);
        selesaiToggle.setToggleGroup(filterGroup);
        aktifToggle.setSelected(true);
    }

    private void loadAppointments() {
        if (currentPatient == null || allAppointmentsInMemory == null) return;
        List<Appointment> filteredAppointments = filterAppointments(allAppointmentsInMemory);
        ObservableList<AppointmentTableData> tableData = FXCollections.observableArrayList();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

        for (Appointment appointment : filteredAppointments) {
            try {
                Doctor doctor = doctorDAO.getDoctorById(appointment.getDoctorId());
                AppointmentTableData data = new AppointmentTableData();
                data.setDoctorName(doctor != null ? doctor.getFullName() : "Unknown");
                data.setSpecialization(doctor != null ? doctor.getSpecialization() : "Unknown");
                data.setDate(appointment.getAppointmentDate().format(dateFormat));
                data.setTime(appointment.getAppointmentDate().format(timeFormat));
                data.setMedicalRecordInfo("N/A");
                data.setDiagnosis("N/A");
                data.setPrescriptionInfo("N/A");
                data.setAppointment(appointment);

                tableData.add(data);
            } catch (Exception e) {
                System.err.println("Error getting doctor info for appointment: " + appointment.getAppointmentId() + " - " + e.getMessage());
            }
        }
        dataAppointmentTable.setItems(tableData);
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
    private void handleCancelAppointment(Appointment appointment) {
        try {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Konfirmasi Pembatalan");
            confirmationAlert.setHeaderText(null);
            
            String doctorNameForConfirmation = "Unknown";
            try {
                Doctor doc = doctorDAO.getDoctorById(appointment.getDoctorId());
                if(doc != null && doc.getFullName() != null) {
                    doctorNameForConfirmation = doc.getFullName();
                }
            } catch (Exception e) {
                System.err.println("Error fetching doctor information: " + e.getMessage());
            }

            confirmationAlert.setContentText("Apakah Anda yakin ingin membatalkan janji temu ini dengan Dr. " + doctorNameForConfirmation + " pada tanggal " + appointment.getAppointmentDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) + "?");
            
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    try {
                        // Delete the appointment from the database
                        appointmentDAO.deleteAppointment(appointment.getAppointmentId());
                        
                        // Remove the appointment from the in-memory list
                        allAppointmentsInMemory.removeIf(a -> a.getAppointmentId() == appointment.getAppointmentId());
                        
                        showSuccess("Janji temu berhasil dibatalkan.");
                        loadAppointments();
                    } catch (SQLException e) {
                        showError("Gagal menghapus janji temu: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            showError("Terjadi kesalahan saat mencoba membatalkan janji temu: " + e.getMessage());
            e.printStackTrace();
        }
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
            stage.setOnHidden(e -> {
                loadAllAppointmentsFromDb(); 
                loadAppointments(); 
            });
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

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sukses");
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
        private Appointment appointment; 

        public AppointmentTableData() {;
        }

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
package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
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
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import model.dao.AppointmentDAO;
import model.dao.DoctorDAO;
import model.entity.Appointment;
import model.entity.AppointmentStatus;
import model.entity.Doctor;
import model.entity.User;
import view.LoginView;

public class AppointmentReceptionistController {

    @FXML private Button dashboardSidebarButton;
    @FXML private Button profilSidebarButton;
    @FXML private Button janjiTemuSidebarButton;
    @FXML private Button daftarPasienBaruSidebarButton;
    @FXML private Button jadwalDokterSidebarButton;
    @FXML private Button notifikasiSidebarButton;
    @FXML private Button keluarSidebarButton;

    @FXML private TableView<AppointmentTableData> dataAppointmentTable;
    @FXML private TableColumn<AppointmentTableData, String> colDoctor;
    @FXML private TableColumn<AppointmentTableData, String> colSpesialis;
    @FXML private TableColumn<AppointmentTableData, String> colDate;
    @FXML private TableColumn<AppointmentTableData, String> colTime;
    @FXML private TableColumn<AppointmentTableData, String> colStatus; // Changed from colJumlahPasien
    @FXML private TableColumn<AppointmentTableData, String> colAction;

    @FXML private ToggleButton acceptToggle;
    @FXML private ToggleButton requestToggle;
    @FXML private ToggleButton todayToggle;
    @FXML private ToggleButton soonToggle;

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private User currentUser;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupToggleButtons();
        loadAppointments();
    }

    private void setupTableColumns() {
        colDoctor.setCellValueFactory(data -> data.getValue().doctorNameProperty());
        colSpesialis.setCellValueFactory(data -> data.getValue().specializationProperty());
        colDate.setCellValueFactory(data -> data.getValue().dateProperty());
        colTime.setCellValueFactory(data -> data.getValue().timeProperty());
        colStatus.setCellValueFactory(data -> data.getValue().statusProperty()); // Changed from colJumlahPasien
        colAction.setCellFactory(getActionCellFactory());
    }

    private Callback<TableColumn<AppointmentTableData, String>, TableCell<AppointmentTableData, String>> getActionCellFactory() {
        return column -> new TableCell<AppointmentTableData, String>() {
            private final Button acceptButton = new Button("Terima");

            {
                acceptButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                // Only show action button for REQUESTED appointments
                if (getIndex() < getTableView().getItems().size()) {
                    AppointmentTableData data = getTableView().getItems().get(getIndex());
                    
                    if (requestToggle.isSelected()) {
                        setGraphic(acceptButton);

                        acceptButton.setOnAction(event -> {
                            try {
                                Appointment appointment = appointmentDAO.getAppointmentById(
                                    Integer.parseInt(data.getAppointmentId()));
                                appointment.setAppointmentStatus(AppointmentStatus.ACCEPTED);
                                appointmentDAO.updateAppointmentStatus(appointment);
                                loadAppointments();
                                showSuccess("Janji temu berhasil diterima!");
                            } catch (Exception e) {
                                showError("Gagal memperbarui status: " + e.getMessage());
                            }
                        });
                    } else {
                        setGraphic(null);
                    }
                } else {
                    setGraphic(null);
                }
            }
        };
    }

    private void setupToggleButtons() {
        ToggleGroup filterGroup = new ToggleGroup();
        acceptToggle.setToggleGroup(filterGroup);
        requestToggle.setToggleGroup(filterGroup);
        
        if (todayToggle != null && soonToggle != null) {
            ToggleGroup timeGroup = new ToggleGroup();
            todayToggle.setToggleGroup(timeGroup);
            soonToggle.setToggleGroup(timeGroup);
        }
        
        // Set default selection
        requestToggle.setSelected(true);
        
        // Add event listeners
        acceptToggle.setOnAction(event -> loadAppointments());
        requestToggle.setOnAction(event -> loadAppointments());
        
        if (todayToggle != null) {
            todayToggle.setOnAction(event -> loadAppointments());
        }
        
        if (soonToggle != null) {
            soonToggle.setOnAction(event -> loadAppointments());
        }
    }

    public void setUser(User user) {
        this.currentUser = user;
        loadAppointments();
    }

    private void loadAppointments() {
        try {
            List<Appointment> appointments = appointmentDAO.getAllAppointments();
            if (todayToggle != null && todayToggle.isSelected()) {
                appointments = appointments.stream()
                        .filter(a -> a.getAppointmentDate().toLocalDate().equals(LocalDate.now()))
                        .collect(Collectors.toList());
            } 
            else if (soonToggle != null && soonToggle.isSelected()) {
                appointments = appointments.stream()
                        .filter(a -> a.getAppointmentDate().toLocalDate().isAfter(LocalDate.now()))
                        .collect(Collectors.toList());
            }
            
            ObservableList<AppointmentTableData> tableData = FXCollections.observableArrayList();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

            for (Appointment appointment : filterAppointments(appointments)) {
                Doctor doctor = doctorDAO.getDoctorById(appointment.getDoctorId());
                AppointmentTableData data = new AppointmentTableData();
                data.setDoctorName(doctor != null ? doctor.getFullName() : "Unknown");
                data.setSpecialization(doctor != null ? doctor.getSpecialization() : "Unknown");
                data.setDate(appointment.getAppointmentDate().format(dateFormat));
                data.setTime(appointment.getAppointmentDate().format(timeFormat));
                
                // Set the status
                String statusText;
                if (appointment.getAppointmentStatus() == AppointmentStatus.REQUESTED) {
                    statusText = "Diajukan";
                } else if (appointment.getAppointmentStatus() == AppointmentStatus.ACCEPTED) {
                    statusText = "Diterima";
                } else {
                    statusText = appointment.getAppointmentStatus().toString();
                }
                data.setStatus(statusText);
                
                // Store the appointment ID for reference
                data.setAppointmentId(String.valueOf(appointment.getAppointmentId()));

                tableData.add(data);
            }
            dataAppointmentTable.setItems(tableData);
        } catch (Exception e) {
            showError("Error loading appointments: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Appointment> filterAppointments(List<Appointment> appointments) {
        if (acceptToggle.isSelected()) {
            return appointments.stream()
                .filter(a -> a.getAppointmentStatus() == AppointmentStatus.ACCEPTED)
                .collect(Collectors.toList());
        } else if (requestToggle.isSelected()) {
            return appointments.stream()
                .filter(a -> a.getAppointmentStatus() == AppointmentStatus.REQUESTED)
                .collect(Collectors.toList());
        }
        return appointments;
    }

    @FXML
    private void handleDashboardClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReceptionistDashboard.fxml"));
            Parent root = loader.load();
            
            ReceptionistDashboardController controller = loader.getController();
            controller.setUser(currentUser);
            
            Stage currentStage = (Stage) keluarSidebarButton.getScene().getWindow();
            currentStage.close();
            
            Stage newStage = new Stage();
            newStage.setTitle("Dashboard Receptionist - Klinik Sehat Medika");
            newStage.setScene(new Scene(root, 1200, 800));
            newStage.show();
            
        } catch (Exception e) {
            showError("Error returning to dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProfilClick(ActionEvent event) {
        System.out.println("Opening profile");
    }

    @FXML
    private void handleDaftarPasienBaruClick(ActionEvent event) {
        System.out.println("Registering new patient");
    }

    @FXML
    private void handleJanjiTemuClick(ActionEvent event) {
        System.out.println("Opening Janji Temu (current view)");
    }

    @FXML
    private void handleJadwalDokterClick(ActionEvent event) {
        System.out.println("Managing doctor schedule");
    }
    
    @FXML
    private void handleNotifikasiClick(ActionEvent event) {
        System.out.println("Opening notifications");
    }
    
    @FXML
    private void handleKeluarClick(ActionEvent event) {
        try {
            // Close the current window
            Stage currentStage = (Stage) keluarSidebarButton.getScene().getWindow();
            currentStage.close();
            
            // Create and show login view directly instead of using Application.launch()
            Stage loginStage = new Stage();
            LoginView loginView = new LoginView();
            loginView.start(loginStage);
        } catch (Exception e) {
            showError("Error logging out: " + e.getMessage());
            e.printStackTrace();
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

    public static class AppointmentTableData {
        private final SimpleStringProperty doctorName = new SimpleStringProperty();
        private final SimpleStringProperty specialization = new SimpleStringProperty();
        private final SimpleStringProperty date = new SimpleStringProperty();
        private final SimpleStringProperty time = new SimpleStringProperty();
        private final SimpleStringProperty status = new SimpleStringProperty();
        private final SimpleStringProperty appointmentId = new SimpleStringProperty();

        public String getDoctorName() { return doctorName.get(); }
        public void setDoctorName(String name) { doctorName.set(name); }
        public SimpleStringProperty doctorNameProperty() { return doctorName; }

        public String getSpecialization() { return specialization.get(); }
        public void setSpecialization(String spec) { specialization.set(spec); }
        public SimpleStringProperty specializationProperty() { return specialization; }

        public String getDate() { return date.get(); }
        public void setDate(String dateStr) { date.set(dateStr); }
        public SimpleStringProperty dateProperty() { return date; }

        public String getTime() { return time.get(); }
        public void setTime(String timeStr) { time.set(timeStr); }
        public SimpleStringProperty timeProperty() { return time; }

        public String getStatus() { return status.get(); }
        public void setStatus(String statusStr) { status.set(statusStr); }
        public SimpleStringProperty statusProperty() { return status; }
        
        public String getAppointmentId() { return appointmentId.get(); }
        public void setAppointmentId(String id) { appointmentId.set(id); }
        public SimpleStringProperty appointmentIdProperty() { return appointmentId; }
    }
}
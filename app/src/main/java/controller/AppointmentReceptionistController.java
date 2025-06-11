package controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.util.Callback;
import model.dao.AppointmentDAO;
import model.dao.DoctorDAO;
import model.entity.Appointment;
import model.entity.AppointmentStatus;
import model.entity.Doctor;
import model.entity.User;

public class AppointmentReceptionistController {

    @FXML private Button dashboardSidebarButton;
    @FXML private Button profilSidebarButton;
    @FXML private Button daftarPasienBaruSidebarButton;
    @FXML private Button jadwalDokterSidebarButton;
    @FXML private Button notifikasiSidebarButton;
    @FXML private Button keluarSidebarButton;

    @FXML private TableView<AppointmentTableData> dataAppointmentTable;
    @FXML private TableColumn<AppointmentTableData, String> colDoctor;
    @FXML private TableColumn<AppointmentTableData, String> colSpesialis;
    @FXML private TableColumn<AppointmentTableData, String> colDate;
    @FXML private TableColumn<AppointmentTableData, String> colTime;
    @FXML private TableColumn<AppointmentTableData, String> colStatus;
    @FXML private TableColumn<AppointmentTableData, String> colAction;

    @FXML private ToggleButton acceptToggle;
    @FXML private ToggleButton requestToggle;

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
        colDoctor.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        colSpesialis.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        colAction.setCellFactory(getActionCellFactory());
    }

    private Callback<TableColumn<AppointmentTableData, String>, TableCell<AppointmentTableData, String>> getActionCellFactory() {
        return column -> new TableCell<>() {
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
                    if (data.getAppointment() != null && 
                        data.getAppointment().getAppointmentStatus() == AppointmentStatus.REQUESTED) {
                        
                        acceptButton.setOnAction(event -> {
                            try {
                                Appointment appointment = data.getAppointment();
                                appointment.setAppointmentStatus(AppointmentStatus.ACCEPTED);
                                boolean success = appointmentDAO.updateAppointmentStatus(appointment);
                                
                                if (success) {
                                    showSuccess("Janji temu berhasil diterima!");
                                    loadAppointments(); // Reload to update the UI
                                } else {
                                    showError("Gagal memperbarui status: Tidak ada perubahan tercatat");
                                }
                            } catch (Exception e) {
                                showError("Gagal memperbarui status: " + e.getMessage());
                                e.printStackTrace();
                            }
                        });
                        
                        setGraphic(acceptButton);
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
        
        // Set default selection
        requestToggle.setSelected(true);
        
        // Add event listeners to toggle buttons
        requestToggle.setOnAction(event -> loadAppointments());
        acceptToggle.setOnAction(event -> loadAppointments());
    }

    public void setUser(User user) {
        this.currentUser = user;
        loadAppointments();
    }

    private void loadAppointments() {
        try {
            List<Appointment> appointments = appointmentDAO.getAllAppointments();
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
                
                // Set the status display text
                String statusText;
                if (appointment.getAppointmentStatus() == AppointmentStatus.REQUESTED) {
                    statusText = "Diajukan";
                } else if (appointment.getAppointmentStatus() == AppointmentStatus.ACCEPTED) {
                    statusText = "Diterima";
                } else {
                    statusText = appointment.getAppointmentStatus().toString();
                }
                data.setStatus(statusText);
                
                // Store the appointment object in the data for use in the action button
                data.setAppointment(appointment);

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
        System.out.println("Navigating to dashboard");
        // Implement navigation to dashboard here
    }

    @FXML
    private void handleProfilClick(ActionEvent event) {
        System.out.println("Opening profile");
        // Implement profile navigation here
    }

    @FXML
    private void handleDaftarPasienBaruClick(ActionEvent event) {
        System.out.println("Registering new patient");
        // Implement new patient registration here
    }

    @FXML
    private void handleJadwalDokterClick(ActionEvent event) {
        System.out.println("Managing doctor schedule");
        // Implement doctor schedule management here
    }

    @FXML
    private void handleNotifikasiClick(ActionEvent event) {
        System.out.println("Opening notifications");
        // Implement notifications here
    }

    @FXML
    private void handleKeluarClick(ActionEvent event) {
        System.out.println("Logging out");
        // Implement logout here
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
        private Appointment appointment;

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
        
        public Appointment getAppointment() { return appointment; }
        public void setAppointment(Appointment appointment) { this.appointment = appointment; }
    }
}
package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.util.Callback;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import model.dao.AppointmentDAO;
import model.dao.DoctorDAO;
import model.dao.PatientDAO;
import model.dao.UserDAO;
import model.entity.Appointment;
import model.entity.AppointmentStatus;
import model.entity.Doctor;
import model.entity.User;

public class AppointmentReceptionistController {

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
    @FXML private TableColumn<AppointmentTableData, String> colJumlahPasien;
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

    // Removed duplicate method definition

    private void setupTableColumns() {
        colDoctor.setCellValueFactory(data -> data.getValue().doctorNameProperty());
        colSpesialis.setCellValueFactory(data -> data.getValue().specializationProperty());
        colDate.setCellValueFactory(data -> data.getValue().dateProperty());
        colTime.setCellValueFactory(data -> data.getValue().timeProperty());
        colJumlahPasien.setCellValueFactory(data -> data.getValue().jumlahPasienProperty());

        colAction.setCellFactory(getActionCellFactory());
    }

    private Callback<TableColumn<AppointmentTableData, String>, TableCell<AppointmentTableData, String>> getActionCellFactory() {
        return column -> new TableCell<>() {
            private final Button acceptButton = new Button("Terima");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                AppointmentTableData data = getTableView().getItems().get(getIndex());
                if (acceptToggle.isSelected()) {
                    setGraphic(acceptButton);

                    acceptButton.setOnAction(event -> {
                        try {
                            Appointment appointment = appointmentDAO.getAppointmentByDetails(
                                data.getDoctorName(), data.getDate(), data.getTime());
                            appointment.setAppointmentStatus(AppointmentStatus.ACCEPTED);
                            appointmentDAO.updateAppointmentStatus(appointment);
                            loadAppointments();
                        } catch (Exception e) {
                            showError("Gagal memperbarui status: " + e.getMessage());
                        }
                    });
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
        requestToggle.setSelected(true);
    }

    public void setUser(User user) {
        this.currentUser = user;
        loadAppointments();
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
            ObservableList<AppointmentTableData> tableData = FXCollections.observableArrayList();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

            for (Appointment appointment : filterAppointments(appointments)) {
                Doctor doctor = doctorDAO.getDoctorById(appointment.getDoctorId());
                AppointmentTableData data = new AppointmentTableData();
                data.setDoctorName(doctor != null ? doctorDAO.getDoctorNameById(doctor.getDoctorId()) : "Unknown");
                data.setSpecialization(doctor != null ? doctor.getSpecialization() : "Unknown");
                data.setDate(appointment.getAppointmentDate().format(dateFormat));
                data.setTime(appointment.getAppointmentDate().format(timeFormat));
                data.setJumlahPasien("N/A");
                data.setAksi(acceptToggle.isSelected() ? "Terima" : "-");

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
        // No action needed as this is the current view
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
        System.out.println("Logging out");
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
        private final SimpleStringProperty jumlahPasien = new SimpleStringProperty();
        private final SimpleStringProperty aksi = new SimpleStringProperty();

        public String getDoctorName() { return doctorName.get(); }
        public void setDoctorName(String name) { doctorName.set(name); }
        public SimpleStringProperty doctorNameProperty() { return doctorName; }

        public String getSpecialization() { return specialization.get(); }
        public void setSpecialization(String specialization) { this.specialization.set(specialization); }
        public SimpleStringProperty specializationProperty() { return specialization; }

        public String getDate() { return date.get(); }
        public void setDate(String date) { this.date.set(date); }
        public SimpleStringProperty dateProperty() { return date; }

        public String getTime() { return time.get(); }
        public void setTime(String time) { this.time.set(time); }
        public SimpleStringProperty timeProperty() { return time; }

        public String getJumlahPasien() { return jumlahPasien.get(); }
        public void setJumlahPasien(String jumlah) { jumlahPasien.set(jumlah); }
        public SimpleStringProperty jumlahPasienProperty() { return jumlahPasien; }

        public String getAksi() { return aksi.get(); }
        public void setAksi(String aksiStr) { aksi.set(aksiStr); }
        public SimpleStringProperty aksiProperty() { return aksi; }
    }
}

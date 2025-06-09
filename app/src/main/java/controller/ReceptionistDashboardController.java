package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.dao.AppointmentDAO;
import model.dao.PatientDAO;
import model.dao.UserDAO;
import model.entity.Appointment;
import model.entity.AppointmentStatus;
import model.entity.User;
import view.LoginView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ReceptionistDashboardController {
    
    @FXML private Button profilSidebarButton;
    @FXML private Button janjiTemuSidebarButton;
    @FXML private Button daftarPasienBaruSidebarButton;
    @FXML private Button jadwalDokterSidebarButton;
    @FXML private Button notifikasiSidebarButton;
    @FXML private Button keluarSidebarButton;
    @FXML private Button mengaturJanjiTemuButton;
    @FXML private Button mendaftarkanPasienBaruButton;
    @FXML private Button mengaturJadwalDokterButton;

    @FXML private Label namePlaceHolder;
    @FXML private Label janjiTemuHariIniPlaceholder;
    @FXML private Label pasienBaruTerdaftarPlaceholder;
    @FXML private Label pasienMenungguPlaceholder;

    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private UserDAO userDAO = new UserDAO();
    private User currentUser;

    @FXML
    public void initialize() {
        loadDashboardData();
    }

    public void setUser(User user) {
        this.currentUser = user;
        if (namePlaceHolder != null) {
            namePlaceHolder.setText(user.getUsername());
        }
        loadDashboardData();
    }

    private void loadDashboardData() {
        try {
            // Get today’s appointments (using appointment date)
            LocalDate today = LocalDate.now();
            List<Appointment> todayAppointments = appointmentDAO.getAppointmentsByDate(today.atStartOfDay());
            if (janjiTemuHariIniPlaceholder != null) {
                janjiTemuHariIniPlaceholder.setText(String.valueOf(todayAppointments.size()));
            }
            // Waiting patients: appointments with SCHEDULED status today
            List<Appointment> waitingPatients = todayAppointments.stream()
                    .filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED)
                    .collect(Collectors.toList());
            if (pasienMenungguPlaceholder != null) {
                pasienMenungguPlaceholder.setText(String.valueOf(waitingPatients.size()));
            }
            // New patients registered in the past week
            long newPatientsCount = patientDAO.getAllPatients().stream()
                    .filter(p -> p.getRegistrationDate() != null &&
                        p.getRegistrationDate().isAfter(LocalDateTime.now().minusWeeks(1)))
                    .count();
            if (pasienBaruTerdaftarPlaceholder != null) {
                pasienBaruTerdaftarPlaceholder.setText(String.valueOf(newPatientsCount));
            }
        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProfilClick(ActionEvent event) {
        System.out.println("Profile clicked");
    }

    @FXML
    private void handleJanjiTemuClick(ActionEvent event) {
        System.out.println("Appointment management clicked");
    }

    @FXML
    private void handleDaftarPasienBaruClick(ActionEvent event) {
        System.out.println("Patient registration clicked");
    }

    @FXML
    private void handleJadwalDokterClick(ActionEvent event) {
        System.out.println("Doctor schedule management clicked");
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
        } catch (Exception e) {
            System.err.println("Error logging out: " + e.getMessage());
        }
    }

    @FXML
    private void handleMengaturJanjiTemuClick(ActionEvent event) {
        System.out.println("Manage appointments clicked");
    }

    @FXML
    private void handleMendaftarkanPasienBaruClick(ActionEvent event) {
        System.out.println("Register new patient clicked");
    }

    @FXML
    private void handleMengaturJadwalDokterClick(ActionEvent event) {
        System.out.println("Manage doctor schedule clicked");
    }
}
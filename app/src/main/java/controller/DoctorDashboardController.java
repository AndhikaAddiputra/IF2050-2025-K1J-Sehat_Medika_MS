package controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.dao.AppointmentDAO;
import model.dao.DoctorDAO;
import model.dao.MedicalRecordDAO;
import model.dao.PatientDAO;
import model.entity.Appointment;
import model.entity.AppointmentStatus;
import model.entity.Doctor;
import model.entity.User;
import view.LoginView;

public class DoctorDashboardController {
    
    @FXML private Label namePlaceHolder;
    @FXML private Label pasienHariIniPlaceholder;
    @FXML private Label totalDiagnosisPlaceholder;
    @FXML private Label toralPasienDitanganiPlaceholder; // Note: typo in FXML "toral" instead of "total"
    @FXML private Label datePlaceholder;
    @FXML private Label timePlacholder; // Note: typo in FXML "timePlacholder" instead of "timePlaceholder"
    @FXML private Label doctorNamePlaceholder;
    
    @FXML private Button profilSidebarButton;
    @FXML private Button janjiTemuSidebarButton;
    @FXML private Button diagnosisSidebarButton;
    @FXML private Button resepObatSidebarButton;
    @FXML private Button notifikasiSIdebarButton; // Note: typo in FXML "SIdebar" instead of "Sidebar"
    @FXML private Button keluarSidebarButton;
    
    @FXML private Button mengaturJanjiTemuButton;
    @FXML private Button mendaftarkanPasienBaruButton;
    @FXML private Button mengaturJadwalDokterButton;
    
    private User currentUser;
    private Doctor currentDoctor;
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private DoctorDAO doctorDAO = new DoctorDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
    
    @FXML
    public void initialize() {
        // Initialize any default values or UI components
        // This method is called automatically when the FXML is loaded
    }
    
    public void setUser(User user) {
        this.currentUser = user;
        
        // Set the doctor's name in the UI
        if (namePlaceHolder != null) {
            namePlaceHolder.setText(user.getUsername());
        }
        
        // Load the doctor's data
        try {
            currentDoctor = doctorDAO.getDoctorById(user.getUserId());
            loadDashboardData();
        } catch (Exception e) {
            showAlert("Error", "Failed to load doctor data: " + e.getMessage());
        }
    }
    
    private void loadDashboardData() {
        if (currentDoctor == null) return;
        
        try {
            // Get today's appointments
            LocalDate today = LocalDate.now();
            List<Appointment> todayAppointments = appointmentDAO.getAppointmentsByDoctorAndDate(
                currentDoctor.getDoctorId(), today.atStartOfDay());
            
            if (pasienHariIniPlaceholder != null) {
                pasienHariIniPlaceholder.setText(String.valueOf(todayAppointments.size()));
            }
            
            // Get total diagnoses made
            int diagnosisCount = medicalRecordDAO.getMedicalRecordsByDoctorId(currentDoctor.getDoctorId()).size();
            if (totalDiagnosisPlaceholder != null) {
                totalDiagnosisPlaceholder.setText(String.valueOf(diagnosisCount));
            }
            
            // Get total patients handled
            int totalPatientsHandled = appointmentDAO.getAppointmentsByDoctorId(currentDoctor.getDoctorId())
                .stream()
                .filter(a -> a.getAppointmentStatus() == AppointmentStatus.ACCEPTED)
                .collect(Collectors.toSet())
                .size();
            
            if (toralPasienDitanganiPlaceholder != null) {
                toralPasienDitanganiPlaceholder.setText(String.valueOf(totalPatientsHandled));
            }
            
            // Get next appointment
            List<Appointment> upcomingAppointments = appointmentDAO.getAppointmentsByDoctorId(currentDoctor.getDoctorId())
                .stream()
                .filter(a -> a.getAppointmentStatus() == AppointmentStatus.REQUESTED && 
                       a.getAppointmentDate().isAfter(LocalDate.now().atStartOfDay()))
                .sorted((a1, a2) -> a1.getAppointmentDate().compareTo(a2.getAppointmentDate()))
                .collect(Collectors.toList());
            
            if (!upcomingAppointments.isEmpty()) {
                Appointment nextAppointment = upcomingAppointments.get(0);
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy");
                DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
                
                if (datePlaceholder != null) {
                    datePlaceholder.setText(nextAppointment.getAppointmentDate().format(dateFormat));
                }
                
                if (timePlacholder != null) {
                    timePlacholder.setText(nextAppointment.getAppointmentDate().format(timeFormat));
                }
                
                if (doctorNamePlaceholder != null) {
                    String patientName = patientDAO.getPatientName(nextAppointment.getPatientId());
                    doctorNamePlaceholder.setText("| " + patientName);
                }
            }
            
        } catch (Exception e) {
            showAlert("Error", "Failed to load dashboard data: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleProfilClick(ActionEvent event) {
        System.out.println("Profile clicked");
        showAlert("Info", "Profile feature will be implemented soon.");
        /*try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DoctorProfile.fxml"));
            Parent root = loader.load();
            
            DoctorProfileController controller = loader.getController();
            controller.setUser(currentUser);
            
            Stage currentStage = (Stage) profilSidebarButton.getScene().getWindow();
            currentStage.close();
            
            Stage newStage = new Stage();
            newStage.setTitle("Doctor Profile - Klinik Sehat Medika");
            newStage.setScene(new Scene(root, 1200, 800));
            newStage.show();
            
        } catch (Exception e) {
            showAlert("Error", "Failed to open profile: " + e.getMessage());
        }*/
    }
    
    @FXML
    private void handleJanjiTemuClick(ActionEvent event) {
        System.out.println("Janj Temu Clicked");
        showAlert("Info", "Janji Temu feature will be implemented soon.");
        /*try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AppointmentDoctor.fxml"));
            Parent root = loader.load();
            
            AppointmentDoctorController controller = loader.getController();
            controller.setUser(currentUser);
            
            Stage currentStage = (Stage) janjiTemuSidebarButton.getScene().getWindow();
            currentStage.close();
            
            Stage newStage = new Stage();
            newStage.setTitle("Appointments - Klinik Sehat Medika");
            newStage.setScene(new Scene(root, 1200, 800));
            newStage.show();
            
        } catch (Exception e) {
            showAlert("Error", "Failed to open appointments: " + e.getMessage());
        }*/
    }
    
    @FXML
    private void handleDiagnosisClick(ActionEvent event) {
        System.out.println("Diagnosis clicked");
        showAlert("Info", "Diagnosis feature will be implemented soon.");
    }
    
    @FXML
    private void handleResepObatClick(ActionEvent event) {
        System.out.println("Resep Obat clicked");
        showAlert("Info", "Resep Obat feature will be implemented soon.");
    }
    
    @FXML
    private void handleNotifikasiClick(ActionEvent event) {
        System.out.println("Notifikasi clicked");
        showAlert("Info", "Notification feature will be implemented soon.");
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
            showAlert("Error", "Failed to logout: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleJadwalHariIniClick(ActionEvent event) {
        System.out.println("Jadwal Hari Ini clicked");
        showAlert("Info", "Jadwal Hari Ini feature will be implemented soon.");
    }
    
    @FXML
    private void handleLihatDiagnosisClick(ActionEvent event) {
        System.out.println("Lihat Diagnosis clicked");
        showAlert("Info", "Lihat Diagnosis feature will be implemented soon.");
    }
    
    @FXML
    private void handlePantauPembuatanResepObatClick(ActionEvent event) {
        System.out.println("Pantau Pembuatan Resep Obat clicked");
        showAlert("Info", "Pantau Pembuatan Resep Obat feature will be implemented soon.");
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
package controller;

import java.net.URL;

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
import model.dao.MedicalRecordDAO;
import model.dao.PatientDAO;
import model.dao.UserDAO;
import model.entity.User;
import view.LoginView;

public class PatientDashboardController {
    
    @FXML private Button dashboardButton;
    @FXML private Button profilSidebarButton;
    @FXML private Button janjiTemuSidebarButton;
    @FXML private Button riwayatMedisSidebarButton;
    @FXML private Button resepObatSidebarButton;
    @FXML private Button notifikasiSidebarButton;
    @FXML private Button keluarSidebarButton;
    @FXML private Button buatJanjiTemuButton;
    @FXML private Button lihatCatatanMedisButton;
    @FXML private Button cekPembuatanResepObatButton;
    
    @FXML private Label namePlaceHolder;
    @FXML private Label janjiTemuAktifPlaceholder;
    @FXML private Label resepDiprosesPlaceholder;
    @FXML private Label catatanMedisPlaceholder;
    @FXML private Label datePlaceholder;
    @FXML private Label timePlaceholder;
    @FXML private Label doctorNamePlaceholder;

    private User currentUser;
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        // Initialize placeholder values
        //if (namePlaceHolder != null) namePlaceHolder.setText("");
        //if (janjiTemuAktifPlaceholder != null) janjiTemuAktifPlaceholder.setText("0");
        //if (resepDiprosesPlaceholder != null) resepDiprosesPlaceholder.setText("0");
        //if (catatanMedisPlaceholder != null) catatanMedisPlaceholder.setText("0");
        //if (datePlaceholder != null) datePlaceholder.setText("");
        //if (timePlaceholder != null) timePlaceholder.setText("");
        //if (doctorNamePlaceholder != null) doctorNamePlaceholder.setText("");
    }

    public void setUser(User user) {
        this.currentUser = user;
        updateUserInterface();
    }

    private void updateUserInterface() {
        if (currentUser != null) {
            if (namePlaceHolder != null) {
                namePlaceHolder.setText(currentUser.getFullname());
            }
            loadDashboardData();
        }
    }

    private void loadDashboardData() {
        if (currentUser == null) return;

        try {
            System.out.println("Loading dashboard data for user: " + currentUser.getUsername());
            
            String patientId = patientDAO.getPatientByUserId(currentUser.getUserId()).getPatientId();
            if (patientId == null) {
                System.err.println("Could not find patientId for userId: " + currentUser.getUserId());
                return;
            }
            
            System.out.println("Found patientId: " + patientId);

            int activeAppointments = appointmentDAO.getActiveAppointments(patientId).size();
            System.out.println("Active appointments count: " + activeAppointments);
            if (janjiTemuAktifPlaceholder != null) {
                janjiTemuAktifPlaceholder.setText(String.valueOf(activeAppointments));
            }

            // Get medical records count
            int medicalRecordsCount = medicalRecordDAO.getMedicalRecordsByPatientId(patientId).size();
            System.out.println("Medical records count: " + medicalRecordsCount);
            if (catatanMedisPlaceholder != null) {
                catatanMedisPlaceholder.setText(String.valueOf(medicalRecordsCount));
            }

        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // @FXML private void handleDashboardClick(ActionEvent event) {
    //     System.out.println("Dashboard clicked");
    // }

    @FXML 
    private void handleProfilClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/patientProfile.fxml"));
            Parent root = loader.load();
            
            PatientProfileController controller = loader.getController();
            controller.setUser(currentUser);
            
            Stage currentStage = (Stage) profilSidebarButton.getScene().getWindow();
            currentStage.close();
            
            Stage newStage = new Stage();
            newStage.setTitle("Patient Profile - Klinik Sehat Medika");
            newStage.setScene(new Scene(root, 1200, 800));
            newStage.show();
            
        } catch (Exception e) {
            System.err.println("Error opening patient profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML 
    private void handleJanjiTemuClick(ActionEvent event) {
        try {
            URL fxmlUrl = getClass().getResource("/view/AppointmentPatient.fxml");
            System.out.println("FXML URL: " + fxmlUrl); // Tambahkan ini
            if (fxmlUrl == null) {
                throw new RuntimeException("FXML file not found! Make sure AppointmentPatient.fxml is in src/main/resources/view");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            
            AppointmentPatientController controller = loader.getController();
            controller.setUser(currentUser);
            
            Stage currentStage = (Stage) janjiTemuSidebarButton.getScene().getWindow();
            currentStage.close();

            Stage newStage = new Stage();
            newStage.setTitle("Janji Temu - Klinik Sehat Medika");
            newStage.setScene(new Scene(root, 1200, 800));
            newStage.show();

        } catch (Exception e) {
            System.err.println("Error opening appointment view: " + e.getMessage());
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load appointment view");
            alert.setContentText(e.getMessage() + "\n\n" + e.getClass().getName());
            alert.showAndWait();
        }
    }

    @FXML private void handleRiwayatMedisClick(ActionEvent event) {
        showAlert("Info", "Riwayat Medis feature will be implemented next.");
        System.out.println("Riwayat Medis clicked");
    }

    @FXML private void handleResepObatClick(ActionEvent event) {
        showAlert("Info", "Resep Obat feature will be implemented next.");
        System.out.println("Resep Obat clicked");
    }

    @FXML private void handleNotifikasiClick(ActionEvent event) {
        showAlert("Info", "Notifikasi feature will be implemented next.");
        System.out.println("Notifikasi clicked");
    }

    @FXML private void handleKeluarClick(ActionEvent event) {
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

    @FXML 
    private void handleBuatJanjiTemuClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MakeAppointmentPatient.fxml"));
            Parent root = loader.load();
            
            MakeAppointmentController controller = loader.getController();
            controller.setUser(currentUser);
            
            Stage stage = new Stage();
            stage.setTitle("Buat Janji Temu - Klinik Sehat Medika");
            stage.setScene(new Scene(root, 600, 600));
            stage.show();
            
        } catch (Exception e) {
            System.err.println("Error opening make appointment view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML private void handleLihatCatatanMedisClick(ActionEvent event) {
        System.out.println("Lihat Catatan Medis clicked");
    }

    @FXML private void handleCekPembuatanResepObatClick(ActionEvent event) {
        System.out.println("Cek Resep Obat clicked");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

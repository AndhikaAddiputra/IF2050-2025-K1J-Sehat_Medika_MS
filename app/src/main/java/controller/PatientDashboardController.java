package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.dao.AppointmentDAO;
import model.dao.MedicalRecordDAO;
import model.dao.PatientDAO;
import model.dao.UserDAO;
import model.entity.User;
import view.LoginView;
import view.AppointmentPatientView;
import view.MakeAppointmentView;

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
        if (namePlaceHolder != null) namePlaceHolder.setText("Jhon");
        if (janjiTemuAktifPlaceholder != null) janjiTemuAktifPlaceholder.setText("0");
        if (resepDiprosesPlaceholder != null) resepDiprosesPlaceholder.setText("0");
        if (catatanMedisPlaceholder != null) catatanMedisPlaceholder.setText("0");
        if (datePlaceholder != null) datePlaceholder.setText("Rabu, 12 Mei 2025");
        if (timePlaceholder != null) timePlaceholder.setText("08.00");
        if (doctorNamePlaceholder != null) doctorNamePlaceholder.setText("| Dr. Asep Spakbor");
    }

    public void setUser(User user) {
        this.currentUser = user;
        updateUserInterface();
    }

    private void updateUserInterface() {
        if (currentUser != null && namePlaceHolder != null) {
            namePlaceHolder.setText(currentUser.getUsername());
            loadDashboardData();
        }
    }

    private void loadDashboardData() {
        if (currentUser == null) return;

        try {
            String patientId = patientDAO.getPatientByUserId(currentUser.getUserId()).getPatientId();
            if (patientId == null) {
                System.err.println("Could not find patientId for userId: " + currentUser.getUserId());
                return;
            }

            int activeAppointments = appointmentDAO.getActiveAppointments(patientId).size();
            if (janjiTemuAktifPlaceholder != null) {
                janjiTemuAktifPlaceholder.setText(String.valueOf(activeAppointments));
            }

            // Get processing prescriptions count
            // Note: Your PrescriptionDAO needs a method like getProcessingPrescriptionsForPatient
            // and your Prescription table needs a status column.
            // int processingPrescriptions = prescriptionDAO.getProcessingPrescriptionsForPatient(patientId);
            // if (resepDiprosesPlaceholder != null) {
            //     resepDiprosesPlaceholder.setText(String.valueOf(processingPrescriptions));
            // }

            // Get medical records count
            int medicalRecordsCount = medicalRecordDAO.getMedicalRecordsByPatientId(patientId).size();
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
            controller.setUser(controller.currentUser);
            
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

    @FXML private void handleJanjiTemuClick(ActionEvent event) {
        try {
            AppointmentPatientView appointmentView = new AppointmentPatientView();
            appointmentView.start(new Stage());
        } 
        catch (Exception e) {
            System.err.println("Error opening appointment view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML private void handleRiwayatMedisClick(ActionEvent event) {
        System.out.println("Riwayat Medis clicked");
    }

    @FXML private void handleResepObatClick(ActionEvent event) {
        System.out.println("Resep Obat clicked");
    }

    @FXML private void handleNotifikasiClick(ActionEvent event) {
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

    @FXML private void handleBuatJanjiTemuClick(ActionEvent event) {
        try {
            MakeAppointmentView makeAppointmentView = new MakeAppointmentView();
            makeAppointmentView.start(new Stage());
        }
        catch (Exception e) {
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

}

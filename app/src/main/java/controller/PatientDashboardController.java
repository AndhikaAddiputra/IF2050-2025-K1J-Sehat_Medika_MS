package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

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

    // @FXML private void handleDashboardClick(ActionEvent event) {
    //     System.out.println("Dashboard clicked");
    // }

    @FXML private void handleProfilClick(ActionEvent event) {
        System.out.println("Profil clicked");
    }

    @FXML private void handleJanjiTemuClick(ActionEvent event) {
        System.out.println("Janji Temu clicked");
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
        // Get current stage from any component (using keluarSidebarButton)
        Stage currentStage = (Stage) keluarSidebarButton.getScene().getWindow();
        
        // Close current dashboard window
        currentStage.close();
        
        // Create and show login view
        LoginView loginView = new LoginView();
        Stage loginStage = new Stage();
        loginView.start(loginStage);
        
    } catch (Exception e) {
        System.err.println("Error switching to login view: " + e.getMessage());
        e.printStackTrace();
    }
    }

    @FXML private void handleBuatJanjiTemuClick(ActionEvent event) {
        System.out.println("Buat Janji Temu clicked");
    }

    @FXML private void handleLihatCatatanMedisClick(ActionEvent event) {
        System.out.println("Lihat Catatan Medis clicked");
    }

    @FXML private void handleCekPembuatanResepObatClick(ActionEvent event) {
        System.out.println("Cek Resep Obat clicked");
    }
}

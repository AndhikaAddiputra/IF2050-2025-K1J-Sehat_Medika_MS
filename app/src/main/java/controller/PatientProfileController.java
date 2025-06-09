package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entity.User;
import model.entity.BloodType;
import model.dao.UserDAO;
import model.dao.PatientDAO;
import model.dao.MedicalRecordDAO;
import view.LoginView;
import view.PatientDashboardView;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class PatientProfileController {
    
    @FXML private TextField namaLengkapField;
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private TextField nomorTeleponField;
    @FXML private PasswordField passwordField;
    @FXML private Button showPasswordButton;
    @FXML private TextField passwordVisibleField;
    
    @FXML private ComboBox<String> golonganDarahCombo;
    @FXML private TextField beratBadanField;
    @FXML private TextField tinggiBadanField;
    @FXML private TextField riwayatAlergiField;
    @FXML private ComboBox<String> penyediaAsuransiCombo;
    @FXML private TextField nomorAsuransiField;
    
    @FXML private Button editAccountButton;
    @FXML private Button saveAccountButton;
    @FXML private Button cancelAccountButton;
    @FXML private Button editMedicalButton;
    @FXML private Button saveMedicalButton;
    @FXML private Button cancelMedicalButton;
    
    @FXML private TableView<MedicalRecordData> medicalRecordsTable;
    @FXML private TableColumn<MedicalRecordData, String> recordDateColumn;
    @FXML private TableColumn<MedicalRecordData, String> doctorNameColumn;
    @FXML private TableColumn<MedicalRecordData, String> diagnosisColumn;
    @FXML private TableColumn<MedicalRecordData, String> symptomsColumn;
    @FXML private TableColumn<MedicalRecordData, String> notesColumn;
    
    public User currentUser;
    private UserDAO userDAO = new UserDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
    
    private boolean passwordVisible = false;
    private String originalPassword;
    
    public void setUser(User user) {
        this.currentUser = user;
        loadUserData();
        loadMedicalData();
        loadMedicalRecords();
    }
    
    @FXML
    public void initialize() {
        setupComboBoxes();
        setupTableColumns();
        setFieldsEditable(false, false);
    }
    
    private void setupComboBoxes() {
        golonganDarahCombo.setItems(FXCollections.observableArrayList("A", "B", "AB", "O"));
        penyediaAsuransiCombo.setItems(FXCollections.observableArrayList(
            "BPJS Kesehatan", "Prudential", "Allianz", "AXA Mandiri", "Cigna", "Lainnya"
        ));
    }
    
    private void setupTableColumns() {
        recordDateColumn.setCellValueFactory(new PropertyValueFactory<>("recordDate"));
        doctorNameColumn.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        diagnosisColumn.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
        symptomsColumn.setCellValueFactory(new PropertyValueFactory<>("symptoms"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
    }
    
    private void loadUserData() {
        if (currentUser != null) {
            namaLengkapField.setText(currentUser.getUsername());
            emailField.setText(currentUser.getEmail());
            usernameField.setText(currentUser.getUsername());
            nomorTeleponField.setText(currentUser.getPhoneNumber());
            passwordField.setText(currentUser.getPassword());
            originalPassword = currentUser.getPassword();
        }
    }
    
    private void loadMedicalData() {
        try {
            String patientId = patientDAO.getPatientById(currentUser.getUserId()).getPatientId();
            if (patientId != null) {
                Map<String, Object> patientData = patientDAO.getPatientMedicalInfo(patientId);
                if (patientData != null) {
                    golonganDarahCombo.setValue((String) patientData.get("bloodType"));
                    riwayatAlergiField.setText((String) patientData.get("allergies"));
                    nomorAsuransiField.setText((String) patientData.get("insuranceInfo"));
                    // Note: Weight and height would need to be added to your Patient table
                }
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to load medical data: " + e.getMessage());
        }
    }
    
    private void loadMedicalRecords() {
        try {
            String patientId = patientDAO.getPatientByUserId(currentUser.getUserId()).getPatientId();
            if (patientId != null) {
                List<Map<String, Object>> records = medicalRecordDAO.getMedicalRecordsForPatient(patientId);
                ObservableList<MedicalRecordData> recordData = FXCollections.observableArrayList();
                
                for (Map<String, Object> record : records) {
                    recordData.add(new MedicalRecordData(
                        record.get("recordDate").toString(),
                        (String) record.get("doctorName"),
                        (String) record.get("diagnosis"),
                        (String) record.get("symptoms"),
                        (String) record.get("notes")
                    ));
                }
                
                medicalRecordsTable.setItems(recordData);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to load medical records: " + e.getMessage());
        }
    }
    
    @FXML
    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            if (passwordVisibleField == null) {
                passwordVisibleField = new TextField();
                passwordVisibleField.setPrefWidth(passwordField.getPrefWidth());
            }
            passwordVisibleField.setText(passwordField.getText());
            passwordField.setVisible(false);
            passwordVisibleField.setVisible(true);
            showPasswordButton.setText("🙈");
        } else {
            passwordField.setText(passwordVisibleField.getText());
            passwordField.setVisible(true);
            passwordVisibleField.setVisible(false);
            showPasswordButton.setText("👁");
        }
    }
    
    @FXML
    private void enableAccountEdit() {
        setFieldsEditable(true, false);
        editAccountButton.setVisible(false);
        saveAccountButton.setVisible(true);
        cancelAccountButton.setVisible(true);
    }
    
    @FXML
    private void enableMedicalEdit() {
        setFieldsEditable(false, true);
        editMedicalButton.setVisible(false);
        saveMedicalButton.setVisible(true);
        cancelMedicalButton.setVisible(true);
    }
    
    @FXML
    private void saveAccountInfo() {
        try {
            currentUser.setUsername(usernameField.getText());
            currentUser.setEmail(emailField.getText());
            currentUser.setPhoneNumber(nomorTeleponField.getText());
            
            String newPassword = passwordVisible ? passwordVisibleField.getText() : passwordField.getText();
            currentUser.setPassword(newPassword);
            
            userDAO.updateUser(currentUser);
            
            setFieldsEditable(false, false);
            editAccountButton.setVisible(true);
            saveAccountButton.setVisible(false);
            cancelAccountButton.setVisible(false);
            
            showAlert("Success", "Account information updated successfully!");
            
        } catch (SQLException e) {
            showAlert("Error", "Failed to update account information: " + e.getMessage());
        }
    }
    
    @FXML
    private void saveMedicalInfo() {
        try {
            String patientId = patientDAO.getPatientByUserId(currentUser.getUserId()).getPatientId();
            if (patientId != null) {
                patientDAO.updatePatientMedicalInfo(patientId, 
                    golonganDarahCombo.getValue(),
                    riwayatAlergiField.getText(),
                    nomorAsuransiField.getText());
                
                setFieldsEditable(false, false);
                editMedicalButton.setVisible(true);
                saveMedicalButton.setVisible(false);
                cancelMedicalButton.setVisible(false);
                
                showAlert("Success", "Medical information updated successfully!");
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to update medical information: " + e.getMessage());
        }
    }
    
    @FXML
    private void cancelAccountEdit() {
        loadUserData();
        setFieldsEditable(false, false);
        editAccountButton.setVisible(true);
        saveAccountButton.setVisible(false);
        cancelAccountButton.setVisible(false);
    }
    
    @FXML
    private void cancelMedicalEdit() {
        loadMedicalData();
        setFieldsEditable(false, false);
        editMedicalButton.setVisible(true);
        saveMedicalButton.setVisible(false);
        cancelMedicalButton.setVisible(false);
    }
    
    private void setFieldsEditable(boolean accountEditable, boolean medicalEditable) {
        namaLengkapField.setEditable(accountEditable);
        emailField.setEditable(accountEditable);
        usernameField.setEditable(accountEditable);
        nomorTeleponField.setEditable(accountEditable);
        passwordField.setEditable(accountEditable);
        
        golonganDarahCombo.setDisable(!medicalEditable);
        beratBadanField.setEditable(medicalEditable);
        tinggiBadanField.setEditable(medicalEditable);
        riwayatAlergiField.setEditable(medicalEditable);
        penyediaAsuransiCombo.setDisable(!medicalEditable);
        nomorAsuransiField.setEditable(medicalEditable);
    }
    
    @FXML
    private void handleDashboardClick() {
        try {
            PatientDashboardView dashboardView = new PatientDashboardView();
            Stage currentStage = (Stage) namaLengkapField.getScene().getWindow();
            currentStage.close();
            
            Stage newStage = new Stage();
            dashboardView.start(newStage);
            
            // Pass user data to dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/patientDashboard.fxml"));
            Parent root = loader.load();
            PatientDashboardController controller = loader.getController();
            controller.setUser(currentUser);
            
        } catch (Exception e) {
            showAlert("Error", "Failed to open dashboard: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleJanjiTemuClick() {
        // Navigate to appointment page
        showAlert("Info", "Janji Temu feature will be implemented next.");
    }
    
    @FXML
    private void handleRiwayatMedisClick() {
        // Navigate to medical history page
        showAlert("Info", "Riwayat Medis feature will be implemented next.");
    }
    
    @FXML
    private void handleResepObatClick() {
        // Navigate to prescription page
        showAlert("Info", "Resep Obat feature will be implemented next.");
    }
    
    @FXML
    private void handleKeluarClick() {
        try {
            Stage currentStage = (Stage) namaLengkapField.getScene().getWindow();
            currentStage.close();
            
            LoginView loginView = new LoginView();
            Stage loginStage = new Stage();
            loginView.start(loginStage);
            
        } catch (Exception e) {
            showAlert("Error", "Failed to logout: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Inner class for TableView data
    public static class MedicalRecordData {
        private String recordDate;
        private String doctorName;
        private String diagnosis;
        private String symptoms;
        private String notes;
        
        public MedicalRecordData(String recordDate, String doctorName, String diagnosis, String symptoms, String notes) {
            this.recordDate = recordDate;
            this.doctorName = doctorName;
            this.diagnosis = diagnosis;
            this.symptoms = symptoms;
            this.notes = notes;
        }
        
        // Getters
        public String getRecordDate() { return recordDate; }
        public String getDoctorName() { return doctorName; }
        public String getDiagnosis() { return diagnosis; }
        public String getSymptoms() { return symptoms; }
        public String getNotes() { return notes; }
    }
}
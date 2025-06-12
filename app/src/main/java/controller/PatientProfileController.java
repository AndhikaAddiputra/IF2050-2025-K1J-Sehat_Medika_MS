package controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import controller.PatientProfileController.MedicalRecordData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.dao.MedicalRecordDAO;
import model.dao.PatientDAO;
import model.dao.UserDAO;
import model.entity.User;
import view.LoginView;

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
    @FXML private Button dashboardSidebarButton;
    @FXML private Button janjiTemuSidebarButton;
    @FXML private Button riwayatMedisSidebarButton;
    @FXML private Button resepObatSidebarButton;
    @FXML private Button notifikasiSidebarButton;
    @FXML private Button keluarSidebarButton;
    
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
        if (this.currentUser != null) {
            loadUserData();
            loadMedicalData();
            loadMedicalRecords();
        } 
        else {
            showAlert("Error", "User data is null");
        }
    }
    
    @FXML
    public void initialize() {
        // Initialize UI components
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
            namaLengkapField.setText(currentUser.getFullname());
            emailField.setText(currentUser.getEmail());
            usernameField.setText(currentUser.getUsername());
            nomorTeleponField.setText(currentUser.getPhoneNumber());
            passwordField.setText(currentUser.getPassword());
            originalPassword = currentUser.getPassword();
        }
    }
    
    private void loadMedicalData() {
        try {
            String patientId = patientDAO.getPatientByUserId(currentUser.getUserId()).getPatientId();
            if (patientId != null) {
                Map<String, Object> patientData = patientDAO.getPatientMedicalInfo(patientId);
                if (patientData != null) {
                    golonganDarahCombo.setValue((String) patientData.get("bloodType"));
                    beratBadanField.setText(String.valueOf(patientData.get("weight")));
                    tinggiBadanField.setText(String.valueOf(patientData.get("height")));
                    riwayatAlergiField.setText((String) patientData.get("allergies"));
                    nomorAsuransiField.setText((String) patientData.get("insuranceNumber"));
                    penyediaAsuransiCombo.setValue((String) patientData.get("insuranceInfo"));
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

    public void validateAccountInfo(String username, String fullname, String email, String phone, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username tidak boleh kosong.");
        }
        if (fullname == null || fullname.isBlank()) {
            throw new IllegalArgumentException("Nama lengkap tidak boleh kosong.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email tidak boleh kosong.");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Nomor telepon tidak boleh kosong.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password tidak boleh kosong.");
        }
        // Bisa ditambahkan validasi lain seperti format email, dll.
    }

    /**
     * Metode publik untuk validasi data medis.
     * @throws IllegalArgumentException jika ada input yang tidak valid.
     */
    public void validateMedicalInfo(String golonganDarah, String riwayatAlergi, String beratBadan, String tinggiBadan) {
        if (golonganDarah == null || golonganDarah.isBlank()) {
            throw new IllegalArgumentException("Golongan darah harus dipilih.");
        }
        if (riwayatAlergi == null || riwayatAlergi.isBlank()) {
            throw new IllegalArgumentException("Riwayat alergi tidak boleh kosong.");
        }
        if (beratBadan == null || beratBadan.isBlank()) {
            throw new IllegalArgumentException("Berat badan tidak boleh kosong.");
        }
        if (tinggiBadan == null || tinggiBadan.isBlank()) {
            throw new IllegalArgumentException("Tinggi badan tidak boleh kosong.");
        }
    }

    @FXML
    private void saveAccountInfo() {
        try {
            // Ambil semua data dari field
            String username = usernameField.getText();
            String fullname = namaLengkapField.getText();
            String email = emailField.getText();
            String phone = nomorTeleponField.getText();
            String newPassword = passwordVisible ? passwordVisibleField.getText() : passwordField.getText();

            // 1. Lakukan validasi terlebih dahulu
            validateAccountInfo(username, fullname, email, phone, newPassword);

            // 2. Jika validasi berhasil, lanjutkan proses penyimpanan
            currentUser.setUsername(username);
            currentUser.setFullname(fullname);
            currentUser.setEmail(email);
            currentUser.setPhoneNumber(phone);
            currentUser.setPassword(newPassword);

            userDAO.updateUser(currentUser);

            setFieldsEditable(false, false);
            // ... (sisa logika UI)
            showAlert("Success", "Account information updated successfully!");

        } catch (IllegalArgumentException | SQLException e) {
            // Tangkap error validasi (IllegalArgumentException) dan error database (SQLException)
            showAlert("Error", "Failed to update account information: " + e.getMessage());
        }
    }
    
    @FXML
    private void saveMedicalInfo() {
        try {
            String golonganDarah = golonganDarahCombo.getValue();
            String riwayatAlergi = riwayatAlergiField.getText();
            String beratBadan = beratBadanField.getText();
            String tinggiBadan = tinggiBadanField.getText();
            String nomorAsuransi = nomorAsuransiField.getText();
            String penyediaAsuransi = penyediaAsuransiCombo.getValue();

            // 1. Lakukan validasi
            validateMedicalInfo(golonganDarah, riwayatAlergi, beratBadan, tinggiBadan);
            
            // 2. Lanjutkan proses penyimpanan
            String patientId = patientDAO.getPatientByUserId(currentUser.getUserId()).getPatientId();
            if (patientId != null) {
                patientDAO.updatePatientMedicalInfo(patientId,
                        golonganDarah,
                        riwayatAlergi,
                        beratBadan,
                        tinggiBadan,
                        nomorAsuransi, // Asumsi field ini opsional, jadi tidak divalidasi
                        penyediaAsuransi); // Asumsi field ini opsional

                setFieldsEditable(false, false);
                // ... (sisa logika UI)
                showAlert("Success", "Medical information updated successfully!");
            }
        } catch (IllegalArgumentException | SQLException e) {
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/patientDashboard.fxml"));
            Parent root = loader.load();
            
            PatientDashboardController controller = loader.getController();
            controller.setUser(currentUser);
            
            Stage currentStage = (Stage) namaLengkapField.getScene().getWindow();
            currentStage.close();
            
            Stage newStage = new Stage();
            newStage.setTitle("Dashboard Pasien - Klinik Sehat Medika");
            newStage.setScene(new Scene(root, 1200, 800));
            newStage.show();
            
        } catch (Exception e) {
            showAlert("Error", "Failed to return to dashboard: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleJanjiTemuClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AppointmentPatient.fxml"));
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
        }
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

    @FXML private void handleNotifikasiClick(ActionEvent event) {
        showAlert("Info", "Notifikasi feature will be implemented next.");
        System.out.println("Notifikasi clicked");
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
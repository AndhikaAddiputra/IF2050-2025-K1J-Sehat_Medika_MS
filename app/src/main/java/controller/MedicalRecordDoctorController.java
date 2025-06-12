package controller;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.dao.DoctorDAO;
import model.dao.MedicalRecordDAO;
import model.dao.PatientDAO;
import model.entity.Doctor;
import model.entity.MedicalRecord;
import model.entity.User;
import view.LoginView;

public class MedicalRecordDoctorController {
    
    // Sidebar buttons
    @FXML private Button dashboardSidebarButton;
    @FXML private Button profilSidebarButton;
    @FXML private Button janjiTemuSidebarButton;
    @FXML private Button diagnosisSidebarButton;
    @FXML private Button resepObatSidebarButton;
    @FXML private Button notifikasiSidebarButton;
    @FXML private Button keluarSidebarButton;
    
    // Table and columns
    @FXML private TableView<MedicalRecordTableData> medicalRecordsTable;
    @FXML private TableColumn<MedicalRecordTableData, String> colRecordId;
    @FXML private TableColumn<MedicalRecordTableData, String> colPatientName;
    @FXML private TableColumn<MedicalRecordTableData, String> colDate;
    @FXML private TableColumn<MedicalRecordTableData, String> colDiagnosis;
    @FXML private TableColumn<MedicalRecordTableData, String> colSymptoms;
    @FXML private TableColumn<MedicalRecordTableData, String> colTreatment;
    
    // Form fields
    @FXML private TextField patientNameField;
    @FXML private TextField diagnosisField;
    @FXML private TextArea symptomsArea;
    @FXML private TextArea treatmentArea;
    @FXML private TextArea notesArea;
    
    // Action buttons
    @FXML private Button addRecordButton;
    @FXML private Button updateRecordButton;
    @FXML private Button deleteRecordButton;
    @FXML private Button clearFormButton;
    @FXML private Button searchButton;
    @FXML private TextField searchField;
    
    private User currentUser;
    private Doctor currentDoctor;
    private MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
    private DoctorDAO doctorDAO = new DoctorDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private ObservableList<MedicalRecordTableData> medicalRecords = FXCollections.observableArrayList();
    private MedicalRecord selectedRecord = null;
    
    @FXML
    public void initialize() {
        setupTableColumns();
        setupTableSelection();
        clearForm();
        
        updateRecordButton.setDisable(true);
        deleteRecordButton.setDisable(true);
    }
    
    private void setupTableColumns() {
        colRecordId.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        colPatientName.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("recordDate"));
        colDiagnosis.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
        colSymptoms.setCellValueFactory(new PropertyValueFactory<>("symptoms"));
        colTreatment.setCellValueFactory(new PropertyValueFactory<>("treatment"));
        
        medicalRecordsTable.setItems(medicalRecords);
    }
    
    private void setupTableSelection() {
        medicalRecordsTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    loadRecordToForm(newSelection);
                    updateRecordButton.setDisable(false);
                    deleteRecordButton.setDisable(false);
                } else {
                    clearForm();
                    updateRecordButton.setDisable(true);
                    deleteRecordButton.setDisable(true);
                }
            }
        );
    }
    
    public void setUser(User user) {
        this.currentUser = user;
        try {
            int userId = Integer.parseInt(user.getUserId());
            currentDoctor = doctorDAO.getDoctorByUserId(userId);
            loadMedicalRecords();
        } catch (Exception e) {
            showError("Error loading doctor data: " + e.getMessage());
        }
    }
    
    private void loadMedicalRecords() {
        try {
            medicalRecords.clear();
            List<MedicalRecord> records = medicalRecordDAO.getMedicalRecordsByDoctorId(currentDoctor.getDoctorId());
            
            for (MedicalRecord record : records) {
                String patientName = patientDAO.getPatientName(record.getPatientId());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                
                MedicalRecordTableData tableData = new MedicalRecordTableData(
                    record.getRecordID(),
                    patientName,
                    record.getRecordDate().format(formatter),
                    record.getDiagnosis(),
                    record.getSymptoms(),
                    record.getTreatment()
                );
                medicalRecords.add(tableData);
            }
        } catch (Exception e) {
            showError("Error loading medical records: " + e.getMessage());
        }
    }
    
    private void loadRecordToForm(MedicalRecordTableData tableData) {
        try {
            List<MedicalRecord> records = medicalRecordDAO.getMedicalRecordsByDoctorId(currentDoctor.getDoctorId());
            selectedRecord = records.stream()
                .filter(r -> r.getRecordID() == tableData.getRecordId()) 
                .findFirst()
                .orElse(null);
            
            if (selectedRecord != null) {
                patientNameField.setText(tableData.getPatientName());
                diagnosisField.setText(selectedRecord.getDiagnosis());
                symptomsArea.setText(selectedRecord.getSymptoms());
                treatmentArea.setText(selectedRecord.getTreatment());
                notesArea.setText(selectedRecord.getNotes() != null ? selectedRecord.getNotes() : "");
                
                patientNameField.setDisable(true);
                
                updateRecordButton.setDisable(false);
                deleteRecordButton.setDisable(false);
                addRecordButton.setDisable(true); 
            }
        } 
        catch (Exception e) {
            showError("Error loading record details: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleAddRecord(ActionEvent event) {
        if (validateFormForAdd()) {
            try {
                PatientSelectionResult result = showPatientSelectionDialog();
                if (result != null && result.getPatientId() != null) {
                    MedicalRecord newRecord = new MedicalRecord();
                    newRecord.setPatientId(result.getPatientId());
                    newRecord.setDoctorId(currentDoctor.getDoctorId());
                    newRecord.setRecordDate(LocalDateTime.now());
                    newRecord.setDiagnosis(diagnosisField.getText().trim());
                    newRecord.setSymptoms(symptomsArea.getText().trim());
                    newRecord.setTreatment(treatmentArea.getText().trim());
                    newRecord.setNotes(notesArea.getText().trim());
                    newRecord.setAttachments(""); // Set empty string for attachments
                    
                    medicalRecordDAO.addMedicalRecord(newRecord);
                    showSuccess("Rekam medis berhasil ditambahkan untuk " + result.getPatientName() + "!");
                    loadMedicalRecords();
                    clearForm();
                }
            } catch (Exception e) {
                showError("Error adding medical record: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @FXML // Added missing annotation
    private void handleUpdateRecord(ActionEvent event) {
        if (selectedRecord != null && validateFormForUpdate()) {
            try {
                selectedRecord.setDiagnosis(diagnosisField.getText().trim());
                selectedRecord.setSymptoms(symptomsArea.getText().trim());
                selectedRecord.setTreatment(treatmentArea.getText().trim());
                selectedRecord.setNotes(notesArea.getText().trim());
                selectedRecord.setRecordDate(LocalDateTime.now()); 
                
                medicalRecordDAO.updateMedicalRecord(selectedRecord);
                showSuccess("Rekam medis berhasil diperbarui!");
                
                // Refresh table and clear form
                loadMedicalRecords();
                clearForm();
                
            } catch (Exception e) {
                showError("Error updating medical record: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void handleDeleteRecord(ActionEvent event) {
        if (selectedRecord != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Konfirmasi Hapus");
            confirmAlert.setHeaderText("Hapus Rekam Medis");
            confirmAlert.setContentText("Apakah Anda yakin ingin menghapus rekam medis ini?");
            
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    medicalRecordDAO.deleteMedicalRecord(selectedRecord.getRecordID());
                    showSuccess("Rekam medis berhasil dihapus!");
                    loadMedicalRecords();
                    clearForm();
                } catch (Exception e) {
                    showError("Error deleting medical record: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    
    @FXML
    private void handleClearForm(ActionEvent event) {
        clearForm();
    }
    
    @FXML
    private void handleSearch(ActionEvent event) {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            loadMedicalRecords();
        } else {
            try {
                medicalRecords.clear();
                List<MedicalRecord> allRecords = medicalRecordDAO.getMedicalRecordsByDoctorId(currentDoctor.getDoctorId());
                
                for (MedicalRecord record : allRecords) {
                    String patientName = patientDAO.getPatientName(record.getPatientId()).toLowerCase();
                    if (patientName.contains(searchText) || 
                        record.getDiagnosis().toLowerCase().contains(searchText) ||
                        record.getSymptoms().toLowerCase().contains(searchText)) {
                        
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                        MedicalRecordTableData tableData = new MedicalRecordTableData(
                            record.getRecordID(),
                            patientDAO.getPatientName(record.getPatientId()),
                            record.getRecordDate().format(formatter),
                            record.getDiagnosis(),
                            record.getSymptoms(),
                            record.getTreatment()
                        );
                        medicalRecords.add(tableData);
                    }
                }
            } catch (Exception e) {
                showError("Error searching records: " + e.getMessage());
            }
        }
    }
    
    private PatientSelectionResult showPatientSelectionDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PatientSelectionDialog.fxml"));
            Parent root = loader.load();
            
            PatientSelectionDialogController controller = loader.getController();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Pilih Pasien");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(addRecordButton.getScene().getWindow());
            dialogStage.setScene(new Scene(root, 500, 400));
            
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
            
            if (controller.getSelectedPatientId() != null) {
                return new PatientSelectionResult(
                    controller.getSelectedPatientId(),
                    controller.getSelectedPatientName()
                );
            }
            return null;
        } 
        catch (Exception e) {
            showError("Error opening patient selection: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private boolean validateFormForAdd() {
        if (diagnosisField.getText().trim().isEmpty()) {
            showError("Diagnosis tidak boleh kosong!");
            return false;
        }
        if (symptomsArea.getText().trim().isEmpty()) {
            showError("Gejala tidak boleh kosong!");
            return false;
        }
        if (treatmentArea.getText().trim().isEmpty()) {
            showError("Pengobatan tidak boleh kosong!");
            return false;
        }
        return true;
    }

    private boolean validateFormForUpdate() {
        if (patientNameField.getText().trim().isEmpty()) {
            showError("Nama pasien tidak boleh kosong!");
            return false;
        }
        if (diagnosisField.getText().trim().isEmpty()) {
            showError("Diagnosis tidak boleh kosong!");
            return false;
        }
        if (symptomsArea.getText().trim().isEmpty()) {
            showError("Gejala tidak boleh kosong!");
            return false;
        }
        if (treatmentArea.getText().trim().isEmpty()) {
            showError("Pengobatan tidak boleh kosong!");
            return false;
        }
        return true;
    }
    
    private void clearForm() {
        patientNameField.clear();
        diagnosisField.clear();
        symptomsArea.clear();
        treatmentArea.clear();
        notesArea.clear();
        
        patientNameField.setDisable(false);
        
        selectedRecord = null;
        medicalRecordsTable.getSelectionModel().clearSelection();
        
        updateRecordButton.setDisable(true);
        deleteRecordButton.setDisable(true);
        addRecordButton.setDisable(false);
    }
    
    // Sidebar navigation methods
    @FXML
    private void handleDashboardClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DoctorDashboard.fxml"));
            Parent root = loader.load();
            
            DoctorDashboardController controller = loader.getController();
            controller.setUser(currentUser);
            
            Stage currentStage = (Stage) dashboardSidebarButton.getScene().getWindow();
            currentStage.close();
            
            Stage newStage = new Stage();
            newStage.setTitle("Dashboard Dokter - Klinik Sehat Medika");
            newStage.setScene(new Scene(root, 1200, 800));
            newStage.show();
            
        } catch (Exception e) {
            showError("Error returning to dashboard: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleProfilClick(ActionEvent event) {
        showInfo("Info", "Fitur Profil akan segera diimplementasikan.");
    }
    
    @FXML
    private void handleDiagnosisClick(ActionEvent event) {
        showInfo("Info", "Fitur Diagnosis akan segera diimplementasikan.");
    }
    
    @FXML
    private void handleResepObatClick(ActionEvent event) {
        showInfo("Info", "Fitur Resep Obat akan segera diimplementasikan.");
    }
    
    @FXML
    private void handleNotifikasiClick(ActionEvent event) {
        showInfo("Info", "Fitur Notifikasi akan segera diimplementasikan.");
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
            showError("Error logging out: " + e.getMessage());
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
        alert.setTitle("Sukses");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static class PatientSelectionResult {
        private String patientId;
        private String patientName;
        
        public PatientSelectionResult(String patientId, String patientName) {
            this.patientId = patientId;
            this.patientName = patientName;
        }
        
        public String getPatientId() { return patientId; }
        public String getPatientName() { return patientName; }
    }
}
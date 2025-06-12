package controller;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.dao.PatientDAO;
import model.entity.Patient;

public class PatientSelectionDialogController {
    
    @FXML private TableView<PatientSelectionData> patientsTable;
    @FXML private TableColumn<PatientSelectionData, String> colPatientId;
    @FXML private TableColumn<PatientSelectionData, String> colPatientName;
    @FXML private TableColumn<PatientSelectionData, String> colEmail;
    @FXML private TableColumn<PatientSelectionData, String> colPhone;
    
    @FXML private TextField searchPatientField;
    @FXML private Button selectButton;
    @FXML private Button cancelButton;
    
    private Stage dialogStage;
    private String selectedPatientId = null;
    private String selectedPatientName = null;
    private PatientDAO patientDAO = new PatientDAO();
    private ObservableList<PatientSelectionData> patients = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        setupTable();
        loadPatients();
        
        // Initially disable select button
        selectButton.setDisable(true);
        
        // Enable select button when a patient is selected
        patientsTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                selectButton.setDisable(newSelection == null);
                if (newSelection != null) {
                    selectedPatientName = newSelection.getFullName();
                }
            }
        );
    }
    
    private void setupTable() {
        colPatientId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        colPatientName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        
        patientsTable.setItems(patients);
    }
    
    private void loadPatients() {
        try {
            patients.clear();
            List<Patient> patientList = patientDAO.getAllPatients();
            
            for (Patient patient : patientList) {
                PatientSelectionData data = new PatientSelectionData(
                    patient.getPatientId(),
                    patient.getFullname(),
                    patient.getEmail(),
                    patient.getPhoneNumber()
                );
                patients.add(data);
            }
        } catch (Exception e) {
            showError("Error loading patients: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleSearch(ActionEvent event) {
        String searchText = searchPatientField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            loadPatients();
        } else {
            try {
                patients.clear();
                List<Patient> allPatients = patientDAO.getAllPatients();
                
                for (Patient patient : allPatients) {
                    if (patient.getFullname().toLowerCase().contains(searchText) ||
                        patient.getEmail().toLowerCase().contains(searchText) ||
                        patient.getPhoneNumber().contains(searchText)) {
                        
                        PatientSelectionData data = new PatientSelectionData(
                            patient.getPatientId(),
                            patient.getFullname(),
                            patient.getEmail(),
                            patient.getPhoneNumber()
                        );
                        patients.add(data);
                    }
                }
            } catch (Exception e) {
                showError("Error searching patients: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleSelect(ActionEvent event) {
        PatientSelectionData selected = patientsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selectedPatientId = selected.getPatientId();
            selectedPatientName = selected.getFullName();
            dialogStage.close();
        }
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        selectedPatientId = null;
        selectedPatientName = null;
        dialogStage.close();
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public String getSelectedPatientId() {
        return selectedPatientId;
    }
    
    public String getSelectedPatientName() {
        return selectedPatientName;
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static class PatientSelectionData {
        private String patientId;
        private String fullName;
        private String email;
        private String phoneNumber;
        
        public PatientSelectionData(String patientId, String fullName, String email, String phoneNumber) {
            this.patientId = patientId;
            this.fullName = fullName;
            this.email = email;
            this.phoneNumber = phoneNumber;
        }
        
        public String getPatientId() { return patientId; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getPhoneNumber() { return phoneNumber; } 
    }
}
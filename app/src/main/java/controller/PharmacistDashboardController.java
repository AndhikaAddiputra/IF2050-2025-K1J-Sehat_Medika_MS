package controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.dao.MedicationDAO;
import model.dao.PrescriptionDAO;
import model.dao.UserDAO;
import model.entity.Medication;
import model.entity.Prescription;
import model.entity.User;
import view.LoginView;
import javafx.event.ActionEvent;

public class PharmacistDashboardController {

    @FXML private Label namePlaceHolder;
    @FXML private Label resepDiprosesPlaceholder;
    @FXML private Label resepSelesaiPlaceholder;
    @FXML private Label obatTersediaPlaceholder;
    @FXML private Label doctorNamePlaceholder;
    @FXML private Label datePlaceholder;
    @FXML private Label timePlacholder;

    @FXML private Button dashboardSidebarButton;
    @FXML private Button resepObatSidebarButton;
    @FXML private Button persediaanObatSidebarButton;
    @FXML private Button notifikasiSidebarButton;
    @FXML private Button keluarSidebarButton;
    @FXML private Button prosesResepObatButton;
    @FXML private Button mengaturPersediaanObatButton;

    private User currentUser;
    private PrescriptionDAO prescriptionDAO = new PrescriptionDAO();
    private MedicationDAO medicationDAO = new MedicationDAO();
    private UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        // Initialize any default values if needed
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
        try {
            // Count prescriptions in process
            List<Prescription> inProcessPrescriptions = new ArrayList<>();
            try {
                inProcessPrescriptions = prescriptionDAO.getPrescriptionsByStatus("PROCESSING");
            } catch (SQLException e) {
                System.err.println("Error getting PROCESSING prescriptions: " + e.getMessage());
            }
            
            if (resepDiprosesPlaceholder != null) {
                resepDiprosesPlaceholder.setText(String.valueOf(inProcessPrescriptions.size()));
            }
            
            // Count completed prescriptions
            List<Prescription> completedPrescriptions = new ArrayList<>();
            try {
                completedPrescriptions = prescriptionDAO.getPrescriptionsByStatus("COMPLETED");
            } catch (SQLException e) {
                System.err.println("Error getting COMPLETED prescriptions: " + e.getMessage());
            }
            
            if (resepSelesaiPlaceholder != null) {
                resepSelesaiPlaceholder.setText(String.valueOf(completedPrescriptions.size()));
            }
            
            // Count available medications
            List<Medication> availableMedications = new ArrayList<>();
            availableMedications = medicationDAO.getAllMedications();
            
            if (obatTersediaPlaceholder != null) {
                obatTersediaPlaceholder.setText(String.valueOf(availableMedications.size()));
            }
            
            // Set upcoming prescription info if available
            if (!inProcessPrescriptions.isEmpty()) {
                Prescription nextPrescription = inProcessPrescriptions.get(0);
                
                if (datePlaceholder != null && nextPrescription.getCreatedAt() != null) {
                    LocalDateTime date = nextPrescription.getCreatedAt();
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy");
                    datePlaceholder.setText(date.format(dateFormatter));
                } else if (datePlaceholder != null) {
                    datePlaceholder.setText("-");
                }
                
                if (timePlacholder != null && nextPrescription.getCreatedAt() != null) {
                    LocalDateTime time = nextPrescription.getCreatedAt();
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                    timePlacholder.setText(time.format(timeFormatter));
                } else if (timePlacholder != null) {
                    timePlacholder.setText("-");
                }
                
                if (doctorNamePlaceholder != null) {
                    String doctorName = nextPrescription.getDoctorId() != null ? 
                        userDAO.getUsernameById(nextPrescription.getDoctorId()) : "Unknown";
                    doctorNamePlaceholder.setText("| " + doctorName);
                }
            } else {
                // No prescriptions in process
                if (datePlaceholder != null) datePlaceholder.setText("-");
                if (timePlacholder != null) timePlacholder.setText("-");
                if (doctorNamePlaceholder != null) doctorNamePlaceholder.setText("| -");
            }
            
        } catch (Exception e) {
            showError("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDashboardClick(ActionEvent event) {
        // Since we're already on the dashboard, we don't need to navigate
        loadDashboardData();
    }

    @FXML
    private void handleProsesResepObatClick(ActionEvent event) {
        try {
            navigateToPage("/view/PrescriptionPharmacist.fxml", "Proses Resep Obat - Klinik Sehat Medika");
        } catch (Exception e) {
            showError("Error opening prescription processing view: " + e.getMessage());
        }
    }

    @FXML
    private void handleMengaturPersediaanObatClick(ActionEvent event) {
        try {
            navigateToPage("/view/MedicationPharmacist.fxml", "Persediaan Obat - Klinik Sehat Medika");
        } catch (Exception e) {
            showError("Error opening medication inventory view: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleResepObatClick(ActionEvent event) {
        try {
            navigateToPage("/view/PrescriptionPharmacist.fxml", "Resep Obat - Klinik Sehat Medika");
        } catch (Exception e) {
            showError("Error opening prescription view: " + e.getMessage());
        }
    }
    
    @FXML
    private void handlePersediaanObatClick(ActionEvent event) {
        try {
            navigateToPage("/view/MedicationPharmacist.fxml", "Persediaan Obat - Klinik Sehat Medika");
        } catch (Exception e) {
            showError("Error opening medication inventory view: " + e.getMessage());
        }
    }

    @FXML
    private void handleNotifikasiClick(ActionEvent event) {
        showInfo("Fitur Notifikasi akan segera diimplementasikan.");
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
            e.printStackTrace();
        }
    }
    
    private void navigateToPage(String fxmlPath, String title) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        
        // Set user to the controller if it has a setUser method
        Object controller = loader.getController();
        if (controller instanceof MedicationPharmacistController) {
            ((MedicationPharmacistController) controller).setUser(currentUser);
        } /*else if (controller instanceof PrescriptionPharmacistController) {
            ((PrescriptionPharmacistController) controller).setUser(currentUser);
        }*/
        
        Stage currentStage = (Stage) namePlaceHolder.getScene().getWindow();
        currentStage.close();
        
        Stage newStage = new Stage();
        newStage.setTitle(title);
        newStage.setScene(new Scene(root, 1200, 800));
        newStage.show();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
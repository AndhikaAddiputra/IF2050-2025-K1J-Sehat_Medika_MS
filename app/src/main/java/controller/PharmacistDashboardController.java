package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.dao.MedicationDAO;
// import model.dao.PrescriptionDAO;
import model.entity.User;
import view.LoginView;

public class PharmacistDashboardController {

    // Sidebar Buttons from FXML
    @FXML private Button profilSidebarButton;
    @FXML private Button resepObatSidebarButton;
    @FXML private Button persediaanObatSidebarButton;
    @FXML private Button notifikasiSidebarButton;
    @FXML private Button keluarSidebarButton;

    // Aksi Cepat Buttons from FXML
    @FXML private Button prosesResepObatButton;
    @FXML private Button mengaturPersediaanObatButton;

    // Placeholders from FXML
    @FXML private Label namePlaceHolder;
    @FXML private Label resepDiprosesPlaceholder;
    @FXML private Label obatTersediaPlaceholder;
    @FXML private Label resepSelesaiPlaceholder;
    @FXML private Label datePlaceholder;
    @FXML private Label timePlaceholder;
    @FXML private Label doctorNamePlaceholder;

    private User currentUser;
    // DAOs relevant to the Pharmacist role
    // private PrescriptionDAO prescriptionDAO = new PrescriptionDAO();
    private MedicationDAO medicineDAO = new MedicationDAO();

    @FXML
    public void initialize() {
        // Initialization can be left minimal as data is loaded after setUser is called,
        // following the pattern in PatientDashboardController.
    }

    /**
     * Sets the current logged-in user and triggers a UI update.
     * This method should be called from the login controller.
     * @param user The logged-in user.
     */
    public void setUser(User user) {
        this.currentUser = user;
        updateUserInterface();
    }

    /**
     * Updates the user-specific parts of the interface.
     */
    private void updateUserInterface() {
        if (currentUser != null) {
            if (namePlaceHolder != null) {
                namePlaceHolder.setText(currentUser.getUsername());
            }
            loadDashboardData();
        }
    }

    /**
     * Fetches data from DAOs and populates the dashboard placeholders.
     */
    private void loadDashboardData() {
        if (currentUser == null) return;

        try {
            System.out.println("Loading dashboard data for pharmacist: " + currentUser.getUsername());

            // Fetch prescription counts by status
            // int resepDiprosesCount = prescriptionDAO.getPrescriptionsByStatus("Diproses").size();
            // int resepSelesaiCount = prescriptionDAO.getPrescriptionsByStatus("Selesai").size();
            int obatTersediaCount = medicineDAO.getAllMedications().size();

            // Update placeholders
            // if (resepDiprosesPlaceholder != null) resepDiprosesPlaceholder.setText(String.valueOf(resepDiprosesCount));
            // if (resepSelesaiPlaceholder != null) resepSelesaiPlaceholder.setText(String.valueOf(resepSelesaiCount));
            if (obatTersediaPlaceholder != null) obatTersediaPlaceholder.setText(String.valueOf(obatTersediaCount));
            
            // The "Agenda Mendatang" might not be relevant for a pharmacist.
            // Populating with placeholder text for now.
            if (datePlaceholder != null) datePlaceholder.setText("Tidak ada agenda");
            if (timePlaceholder != null) timePlaceholder.setText("");
            if (doctorNamePlaceholder != null) doctorNamePlaceholder.setText("");


        } catch (Exception e) {
            System.err.println("Error loading pharmacist dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

    @FXML
    private void handleProfilClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PharmacistDashboard.fxml"));
            Parent root = loader.load();

            // Assuming PharmacistProfileController has a setUser method
            PharmacistDashboardController controller = loader.getController();
            controller.setUser(currentUser);

            Stage currentStage = (Stage) profilSidebarButton.getScene().getWindow();
            currentStage.close();

            Stage newStage = new Stage();
            newStage.setTitle("Profil Apoteker - Klinik Sehat Medika");
            newStage.setScene(new Scene(root, 1200, 800));
            newStage.show();

        } catch (Exception e) {
            System.err.println("Error opening pharmacist profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleResepObatClick(ActionEvent event) {
        openView("/view/MedicationPharmacist.fxml", "Resep Obat", resepObatSidebarButton);
    }

    @FXML
    private void handlePersediaanObatClick(ActionEvent event) {
        openView("/view/MedicationPharmacist.fxml", "Persediaan Obat", persediaanObatSidebarButton);
    }
    
    @FXML
    private void handleNotifikasiClick(ActionEvent event) {
        showAlert("Info", "Fitur Notifikasi akan segera diimplementasikan.");
        System.out.println("Notifikasi clicked");
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
            System.err.println("Error switching to login view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- "Aksi Cepat" Handlers ---

    @FXML
    private void handleProsesResepObatClick(ActionEvent event) {
        // This button can navigate to the same page as the sidebar button
        openView("/view/PrescriptionPharmacist.fxml", "Resep Obat", prosesResepObatButton);
    }

    @FXML
    private void handleMengaturPersediaanObatClick(ActionEvent event) {
        // This button can navigate to the same page as the sidebar button
        openView("/view/MedicationPharmacist.fxml", "Persediaan Obat", mengaturPersediaanObatButton);
    }

    // --- Utility Methods ---

    /**
     * A helper method to reduce code duplication for view navigation.
     * @param fxmlPath The path to the FXML file.
     * @param title The title for the new window.
     * @param sourceButton The button that triggered the event.
     */
    private void openView(String fxmlPath, String title, Button sourceButton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // A generic way to pass the user object if the controller supports it.
            // This requires a common interface or checking with instanceof.
            Object loadedController = loader.getController();
            if (loadedController instanceof UserController) { // Assuming a common interface
                ((UserController) loadedController).setUser(currentUser);
            }
            
            Stage currentStage = (Stage) sourceButton.getScene().getWindow();
            currentStage.close();
            
            Stage newStage = new Stage();
            newStage.setTitle(title + " - Klinik Sehat Medika");
            newStage.setScene(new Scene(root, 1200, 800));
            newStage.show();

        } catch (Exception e) {
            System.err.println("Error opening view '" + title + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Optional: Create a common interface for controllers that need a User object
    public interface UserController {
        void setUser(User user);
    }
}
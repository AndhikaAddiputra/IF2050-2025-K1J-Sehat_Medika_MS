package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.dao.UserDAO;
import model.entity.User;

public class LoginController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button masukButton;
    
    @FXML
    private Label wrongLogin;
    
    @FXML
    public void initialize() {
        wrongLogin.setText("");
        
        // Debug prints
        System.out.println("Controller initialized");
        System.out.println("usernameField: " + (usernameField != null));
        System.out.println("passwordField: " + (passwordField != null));
        System.out.println("masukButton: " + (masukButton != null));
        System.out.println("wrongLogin: " + (wrongLogin != null));
    }
    
    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            wrongLogin.setText("Username dan password tidak boleh kosong");
            return;
        }

        UserDAO userDAO = new UserDAO();
        try {
            if (userDAO.authenticateUser(username, password)) {
                User user = userDAO.getUserByUsername(username);
                if (user != null) {
                    String role = user.getRole().name();

                    wrongLogin.setText("Login berhasil sebagai " + role);
                    System.out.println("Login successful - role: " + role);

                    switch (role) {
                        // case "ADMIN":
                        //     break;
                        case "PATIENT":
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/patientDashboard.fxml"));
                                Parent root = loader.load();
                                
                                PatientDashboardController controller = loader.getController();
                                controller.setUser(user);
                                
                                Stage stage = new Stage();
                                stage.setTitle("Dashboard Pasien - Klinik Sehat Medika");
                                stage.setScene(new Scene(root, 1200, 800));
                                stage.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                wrongLogin.setText("Error loading dashboard");
                            }
                            break;
                        case "DOCTOR":
                             try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DoctorDashboard.fxml"));
                                Parent root = loader.load();
                                
                                DoctorDashboardController controller = loader.getController();
                                controller.setUser(user);
                                
                                Stage currentStage = (Stage) ((Button)event.getSource()).getScene().getWindow();
                                currentStage.close();
                                
                                Stage stage = new Stage();
                                stage.setTitle("Dashboard Dokter - Klinik Sehat Medika");
                                stage.setScene(new Scene(root, 1200, 800));
                                stage.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                wrongLogin.setText("Error loading doctor dashboard: " + e.getMessage());
                            }
                            break;
                        case "RECEPTIONIST":
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/receptionistDashboard.fxml"));
                                Parent root = loader.load();
                                
                                ReceptionistDashboardController controller = loader.getController();
                                controller.setUser(user);
                                
                                Stage stage = new Stage();
                                stage.setTitle("Dashboard Receptionist - Klinik Sehat Medika");
                                stage.setScene(new Scene(root, 1200, 800));
                                stage.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                wrongLogin.setText("Error loading dashboard");
                            }
                            break;
                            case "PHARMACIST":
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PharmacistDashboard.fxml"));
                                    Parent root = loader.load();
                                    
                                    PharmacistDashboardController controller = loader.getController();
                                    controller.setUser(user);
                                    
                                    Stage currentStage = (Stage) ((Button)event.getSource()).getScene().getWindow();
                                    currentStage.close();
                                    
                                    Stage stage = new Stage();
                                    stage.setTitle("Dashboard Apoteker - Klinik Sehat Medika");
                                    stage.setScene(new Scene(root, 1200, 800));
                                    stage.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    wrongLogin.setText("Error loading pharmacist dashboard: " + e.getMessage());
                                }
                            break;
                    }
                    ((Stage) masukButton.getScene().getWindow()).close();

                    
                } else {
                    wrongLogin.setText("Terjadi kesalahan: Data pengguna tidak ditemukan setelah autentikasi.");
                    System.out.println("Login failed - user data not found after authentication");
                }
            } else {
                wrongLogin.setText("Username atau password salah");
                System.out.println("Login failed - incorrect credentials");
            }
        } catch (Exception e) {
            e.printStackTrace();
            wrongLogin.setText("Terjadi kesalahan saat login");
        }
    }
}
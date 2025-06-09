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
import view.PatientDashboardView;
import view.ReceptionistDashboardView;

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
                            PatientDashboardView patientView = new PatientDashboardView();
                            patientView.start(new Stage());

                            PatientDashboardController patientController = new PatientDashboardController();
                            patientController.setUser(user);
                            break;
                        case "DOCTOR":
                            break;
                        case "RECEPTIONIST":
                            ReceptionistDashboardView receptionistView = new ReceptionistDashboardView();
                            receptionistView.start(new Stage());

                            ReceptionistDashboardController receptionistController = new ReceptionistDashboardController();
                            receptionistController.setUser(user);
                            break;
                        case "PHARMACIST":
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
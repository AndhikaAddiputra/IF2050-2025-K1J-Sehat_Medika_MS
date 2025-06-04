package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import view.PatientDashboardView;

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
        System.out.println("Login button clicked");
        
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        
        if (username.equals("admin") && password.equals("password")) {
            // Navigate to main application screen here
            wrongLogin.setText("Masuk Pak eko"); // Clear any previous error message
            System.out.println("Login successful - navigating to admin dashboard");

        } else if (username.equals("patient") && password.equals("patient123")) {
            PatientDashboardView patientView = new PatientDashboardView();
            try {
                patientView.start(new javafx.stage.Stage());
                System.out.println("Login successful - navigating to patient dashboard");
            } catch (Exception e) {
                e.printStackTrace();
                // PegeNotFound realization
            }

        } else if (username.equals("doctor") && password.equals("doctor123")) {
            // Navigate to doctor dashboard here
        } else if (username.equals("receptionist") && password.equals("receptionist123")) {
            // Navigate to receptionist dashboard here
        } else if (username.equals("pharmacist") && password.equals("pharmacist123")) {
            // Navigate to pharmacist dashboard here
        } else if (username.isEmpty() || password.isEmpty()) {
            wrongLogin.setText("Username dan password tidak boleh kosong");
            System.out.println("Login failed - empty fields");
        }
        else {
            wrongLogin.setText("Username atau password salah");
            System.out.println("Login failed - showing error message");
        }
    }
}
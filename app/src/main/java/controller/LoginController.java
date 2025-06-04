package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.DatabaseConnection;
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
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            wrongLogin.setText("Username dan password tidak boleh kosong");
            return;
        }

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String verifyLogin = "SELECT role FROM users WHERE username = ? AND password = ?";

        try {
            PreparedStatement statement = connectDB.prepareStatement(verifyLogin);
            statement.setString(1, username);
            statement.setString(2, password);
            
            ResultSet queryResult = statement.executeQuery();

            if (queryResult.next()) {
                String role = queryResult.getString("role");

                wrongLogin.setText("Login berhasil sebagai " + role);
                System.out.println("Login successful - role: " + role);

                switch (role) {
                    case "admin":
                        break;
                    case "patient":
                        PatientDashboardView patientView = new PatientDashboardView();
                        patientView.start(new Stage());
                        break;
                    case "doctor":
                        break;
                    case "receptionist":
                        break;
                    case "pharmacist":
                        break;
                }
                ((Stage) masukButton.getScene().getWindow()).close();
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
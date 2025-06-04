package view;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class PatientDashboardView extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("patientDashboard.fxml"));
        Scene scene  = new Scene(root, 1200, 800);  
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());     
        primaryStage.setTitle("Patient Dashboard - Sistem Informasi dan Manajemen Klinik Sehat Medika");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ReceptionistDashboardView extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/ReceptionistDashboard.fxml"));
        Scene scene  = new Scene(root, 1200, 800);  
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());     
        primaryStage.setTitle("Receptionist Dashboard - Sistem Informasi dan Manajemen Klinik Sehat Medika");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
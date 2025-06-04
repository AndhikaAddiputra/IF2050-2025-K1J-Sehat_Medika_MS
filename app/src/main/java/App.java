import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize the application
        primaryStage.setTitle("Sistem Informasi dan Manajemen Klinik Sehat Medika");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.show();
    }
}

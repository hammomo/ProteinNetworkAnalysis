package home;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 
 * @author Hanyi.Mo
 *
 * Main.java
 * Main entrance of the project
 */

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
        Parent root = loader.load();
        HomeController controller = loader.getController();
        controller.setStage(primaryStage);
        primaryStage.setTitle("Network Analysis");
        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(Main.class.getResource("stylesheet.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
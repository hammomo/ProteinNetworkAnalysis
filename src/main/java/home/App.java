package home;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

//    @Override
//    public void start(Stage primaryStage) throws Exception{
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("main/resources/home/Home.fxml"));
//        Parent root = loader.load();
//        HomeController controller = loader.getController();
//        controller.setStage(primaryStage);
//        primaryStage.setTitle("Network Analysis");
//        Scene scene = new Scene(root, 750, 500);
//        scene.getStylesheets().add("main/resources/home/stylesheet.css");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = loadFXML("Home");
        scene = new Scene(loader.load(), 750, 500);
        HomeController controller = loader.getController();
        controller.setStage(stage);
        scene.getStylesheets().add(String.valueOf(App.class.getResource("stylesheet.css")));
        stage.setTitle("Network Analysis");
        stage.setScene(scene);
        stage.show();
    }

    private static FXMLLoader loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader;
    }

    public static void main(String[] args) {
        launch();
    }

}
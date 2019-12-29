package home;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private TextArea summary;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        summary.setText("Number of Nodes:\nNumber of Interactions:\nAverage Degree:");
    }
}

package home;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import logic.Network;

public class Controller implements Initializable {
    @FXML
    private TextArea summary, infoArea;
    @FXML
    private Button upload, add, save;
    @FXML
    private TextField filepath, node1, node2, output;

    private Stage stage;
    private Network network;
    private double averageDegree = 0.0;
    private int numOfNodes = 0;
    private int numOfInteractions = 0;
    private String hubStr = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        filepath.setText("PPInetwork.txt");
        setSummary();
        infoArea.setText("Information Area:\n");
        infoArea.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                infoArea.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
            }
        });
        upload.setOnAction(actionEvent -> {
            // openFileDialog(); // Cannot work on Mac OS 10.15.1 due to JavaFX internal bug
            openFile();
        });
        add.setOnAction(actionEvent -> {
            String msg = addInteraction();
            infoArea.appendText(msg);
        });
        save.setOnAction(actionEvent -> {
            String msg = saveDistribution();
            infoArea.appendText(msg);
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void setSummary() {
        summary.setText("Number of Nodes: " + numOfNodes
                + "\nNumber of Interactions: " + numOfInteractions
                + "\nAverage Degree: " + averageDegree
                + "\nHubs: " + hubStr);
    }

    private void updateSummary() {
        hubStr = network.generateHubsString();
        numOfNodes = network.countOfNodes();
        numOfInteractions = network.counOfEdges();
        averageDegree = network.averageDegree();
    }

    private void openFile() {
        network = new Network();
        try {
            network.createNetworkFromFile(filepath.getText());
            updateSummary();
            setSummary();
            infoArea.appendText("File " + filepath.getText() + " uploaded successfully!\n");
        } catch (IOException e) {
            infoArea.appendText("Cannot find file: " + e.getMessage()
                    + "\nPlease input valid filename!\n");
        }
    }

    private String addInteraction() {
        try {
            checkNetworkExistence();
        } catch (NullPointerException e) {
            return e.getMessage();
        }
        try {
            String msg = network.addInteraction(node1.getText(), node2.getText());
            updateSummary();
            setSummary();
            return msg;
        } catch (NullPointerException e) {
            return "Cannot add Null node! Please input both nodes!\n";
        }
    }

    private String saveDistribution() {
        try {
            checkNetworkExistence();
        } catch (NullPointerException e) {
            return e.getMessage();
        }
        String filename = output.getText();
        if (filename == null || filename.equals("")) return "Save failed! Please specify the output filename!\n";
        network.saveDegreeDistribution(filename);
        return String.format("Network distribution has been saved to file: %s\n", filename);
    }

    private void checkNetworkExistence() throws NullPointerException {
        if (network == null) throw new NullPointerException("Network has not been initialised!\n");
    }

    /**
     * Useless on Mac OS 10.15.1
     */
    private void openFileDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.showOpenDialog(stage);
    }
}

package home;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import logic.Network;
import logic.Node;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    @FXML
    private TextArea summary, infoArea;
    @FXML
    private Button upload, add, save, nodeSearch;
    @FXML
    private TextField filepath, node1, node2, output, nodeName;
    @FXML
    private LineChart<Number, Number> lineChart;

    private Stage stage;
    private Network network;
    private double averageDegree = 0.0;
    private int numOfNodes = 0;
    private int numOfInteractions = 0;
    private String hubStr = "";
    private int maxDegree = 0;
    private int numOfEdges = 0;
    private String theNode = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        filepath.setText("PPInetwork.txt");
        setSummary();
        infoArea.setText("Information Area:\n");
        infoArea.textProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> {
            infoArea.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
        });
        upload.setOnAction(actionEvent -> {
            // openFileDialog(); // Cannot work on Mac OS 10.15.1 due to JavaFX internal bug
            openFile();
        });
        nodeSearch.setOnAction(actionEvent -> {
            String msg = searchDegreeForNode();
            setSummary();
            infoArea.appendText(msg);
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
        if (numOfEdges == 0 && theNode.equals("")) {
            summary.setText(String.format("Number of Nodes: %d\nNumber of Interactions: %d\nAverage Degree: %.8f\n" +
                            "Hub(s): %s\nMax Degree: %d", numOfNodes, numOfInteractions
                    , averageDegree, hubStr, maxDegree));
        } else {
            summary.setText(String.format("Number of Nodes: %d\nNumber of Interactions: %d\nAverage Degree: %.8f\n" +
                            "Hub(s): %s\nMax Degree: %d\nFind %d edge(s) for the Node %s", numOfNodes, numOfInteractions
                    , averageDegree, hubStr, maxDegree, numOfEdges, theNode));
        }
    }

    private void updateSummary() {
        Pair<Integer, String> p  = network.generateHubsString();
        hubStr = p.getValue();
        maxDegree = p.getKey();
        numOfNodes = network.countOfNodes();
        numOfInteractions = network.countOfEdges();
        averageDegree = network.averageDegree();
    }

    private void openFile() {
        network = new Network();
        try {
            network.createNetworkFromFile(filepath.getText());
            updateSummary();
            setSummary();
            updateLineChart();
            infoArea.appendText("File " + filepath.getText() + " uploaded successfully!\n");
        } catch (IOException e) {
            infoArea.appendText("Cannot find file: " + e.getMessage()
                    + "\nPlease input valid filename!\n");
        }
    }

    private String searchDegreeForNode() {
        try {
            checkNetworkExistence();
        } catch (NullPointerException e) {
            return e.getMessage();
        }
        theNode = nodeName.getText().equals("")? theNode : nodeName.getText();
        if (theNode == null || theNode.equals("")) return "Please enter a node name!\n";
        int degree = network.degreeOfNode(new Node(theNode));
        if (degree == 0) return "Cannot find edges for the Node: " + theNode + "\n";
        numOfEdges = degree;
        return String.format("Find %d edge(s) for the Node %s\nSummary Updated!\n", numOfEdges, theNode);
    }

    private String addInteraction() {
        try {
            checkNetworkExistence();
        } catch (NullPointerException e) {
            return e.getMessage();
        }

        try {
            String msg = network.addInteraction(node1.getText(), node2.getText());
            if (theNode.equals(node1.getText()) || theNode.equals(node2.getText())) searchDegreeForNode();
            updateSummary();
            setSummary();
            updateLineChart();
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

    private void updateLineChart() {
        double xUpper = 0.0, yUpper = 0.0;
        Map<Integer, Integer> dist = network.getDegreeDistribution();
        for (int i: dist.keySet()) {
            if (i > xUpper) xUpper = i;
            if (dist.get(i) > yUpper) yUpper = dist.get(i);
        }
        if (lineChart.getData().size() > 0) {
            lineChart.getData().remove(0);
        }
        lineChart.setLayoutX(xUpper);
        lineChart.setLayoutY(yUpper);
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        dist.forEach((k,v) -> {
            series.getData().add(new Data<Number, Number>(k, v));
        });

        lineChart.getData().add(series);
    }

    /**
     * Useless on Mac OS 10.15.1
     */
    @SuppressWarnings("unused")
	private void openFileDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.showOpenDialog(stage);
    }
}

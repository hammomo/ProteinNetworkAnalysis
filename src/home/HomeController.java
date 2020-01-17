package home;

import home.helper.CanvasHelper;
import home.helper.TableHelper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import logic.Network;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author Hanyi.Mo
 * 
 * HomeController.java
 */

public class HomeController implements Initializable {

    // the following fields are bound with the view
    @FXML
    private TextArea summary, infoArea;
    @FXML
    private Button upload, add, save, nodeSearch;
    @FXML
    private TextField filepath, node1, node2, output, nodeName;
    @FXML
    private LineChart<Number, Number> lineChart;
    @FXML
    private TableView<TableHelper> table;
    @FXML
    private TableColumn<TableHelper, Integer> tDegree, tNum;
    @FXML
    private Canvas canvas;
    @FXML
    private GraphicsContext gc;

    // the following fields are only used in this controller
    private Stage stage;
    private Network network;
    private double averageDegree = 0.0;
    private int numOfNodes = 0;
    private int numOfInteractions = 0;
    private String hubStr = "";
    private int maxDegree = 0;
    private int numOfEdges = 0;
    private String theNode = "";

    /**
     * initial fields and events
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gc = canvas.getGraphicsContext2D();

        filepath.setText("PPInetwork.txt");
        setSummary();
        infoArea.setText("Information Area:\n");
        infoArea.textProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> {
            infoArea.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
        });
        upload.setOnAction(actionEvent -> {
            // openFileDialog(); // Cannot work on macOS 10.15.1
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

    /**
     * To set the summary string
     */
    private void setSummary() {
        if (numOfEdges == 0 || theNode.equals("")) {
            summary.setText(String.format("Number of Nodes: %d\nNumber of Interactions: %d\nAverage Degree: %.8f\n" +
                            "Hub(s): %s\nMax Degree: %d", numOfNodes, numOfInteractions
                    , averageDegree, hubStr, maxDegree));
        } else {
            summary.setText(String.format("Number of Nodes: %d\nNumber of Interactions: %d\nAverage Degree: %.8f\n" +
                            "Hub(s): %s\nMax Degree: %d\nFind %d edge(s) for the Node %s", numOfNodes, numOfInteractions
                    , averageDegree, hubStr, maxDegree, numOfEdges, theNode));
        }
    }

    /**
     * To update variables in the summary string
     */
    private void updateSummary() {
        hubStr = network.findHubs();
        maxDegree = network.getMaxDegree();
        numOfNodes = network.countOfNodes();
        numOfInteractions = network.countOfEdges();
        averageDegree = network.averageDegree();
    }

    /**
     * To create a network from the input filename
     */
    private void openFile() {
        network = new Network();
        try {
            network.createNetworkFromFile(filepath.getText());
            updateSummary();
            setSummary();
            updateLineChart();
            setTableView();
            drawCanvas();
            infoArea.appendText("File " + filepath.getText() + " uploaded successfully!\n");
        } catch (IOException e) {
            infoArea.appendText("Cannot find file: " + e.getMessage()
                    + "\nPlease input valid filename!\n");
        }
    }

    /**
     * To search the degree of one specific node
     *
     * @return
     */
    private String searchDegreeForNode() {
        try {
            checkNetworkExistence();
        } catch (NullPointerException e) {
            return e.getMessage();
        }
        theNode = nodeName.getText();
        if (theNode == null || theNode.equals("")) return "Please enter a node name!\n";
        int degree = network.degreeOfNode(theNode);
        if (degree == 0) {
            numOfEdges = 0;
            return "Cannot find edges for the Node: " + theNode + "\n";
        }
        numOfEdges = degree;
        return String.format("Find %d edge(s) for the Node %s\nSummary Updated!\n", numOfEdges, theNode);
    }

    /**
     * To add an edge to the current network
     *
     * @return
     */
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
            setTableView();
            drawCanvas();
            return msg;
        } catch (NullPointerException e) {
            return "Cannot add Null node! Please input both nodes!\n";
        }
    }

    /**
     * To save the degree distribution to a destination file
     *
     * @return
     */
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

    /**
     * To check if the network exists, otherwise throw an exception
     *
     * @throws NullPointerException
     */
    private void checkNetworkExistence() throws NullPointerException {
        if (network == null) throw new NullPointerException("Network has not been initialised!\n");
    }

    /**
     * To generate data in the line chart
     */
    private void updateLineChart() {
        double xUpper = 0.0, yUpper = 0.0;
        Map<Integer, Integer> dist = network.getDegreeDistribution();
        for (int i : dist.keySet()) {
            if (i > xUpper) xUpper = i;
            if (dist.get(i) > yUpper) yUpper = dist.get(i);
        }
        if (lineChart.getData().size() > 0) {
            lineChart.getData().remove(0);
        }
        lineChart.setLayoutX(xUpper);
        lineChart.setLayoutY(yUpper);
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        dist.forEach((k, v) -> {
            series.getData().add(new Data<>(k, v));
        });

        lineChart.getData().add(series);
    }

    private void setTableView() {
        List<TableHelper> list = new ArrayList<>();
        network.getDegreeDistribution().forEach((k, v) -> {
            list.add(new TableHelper(k, v));
        });
        final ObservableList<TableHelper> data =
                FXCollections.observableArrayList(list);
        tDegree.setCellValueFactory(new PropertyValueFactory<>("degree"));
        tNum.setCellValueFactory(new PropertyValueFactory<>("num"));
        table.setItems(data);
    }

    private void drawCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        List<String[]> names = network.getAllEdges();
        Map<String, CanvasHelper> nodeCoordinates = new HashMap<>();
        names.forEach(arr -> {
            if (!nodeCoordinates.containsKey(arr[0])) {
                nodeCoordinates.put(arr[0], new CanvasHelper(arr[0]));
            }
            if (!nodeCoordinates.containsKey(arr[1])) {
                nodeCoordinates.put(arr[1], new CanvasHelper(arr[1]));
            }
            CanvasHelper c1 = nodeCoordinates.get(arr[0]);
            CanvasHelper c2 = nodeCoordinates.get(arr[1]);
            gc.strokeLine(c1.xAxis + 7.5, c1.yAxis + 7.5, c2.xAxis + 7.5, c2.yAxis + 7.5);
        });
        gc.setFill(Color.BLUE);
        nodeCoordinates.values().forEach(c -> {
            gc.fillOval(c.xAxis, c.yAxis, 15, 15);
            gc.strokeText(c.name, c.xAxis, c.yAxis);
        });
    }

    /**
     * The method intended to allow user to locate an input file
     * from the whole file system via open a pop-up dialog/window.
     * However, due to the security setting on macOS, it cannot work properly.
     */
    @SuppressWarnings("unused")
    private void openFileDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.showOpenDialog(stage);
    }
}

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
        setInitialVariables();
        infoArea.textProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> {
            infoArea.setScrollTop(Double.MAX_VALUE); // this will scroll to the bottom
        });
        upload.setOnAction(actionEvent -> {
//             openFileDialog(); // Cannot work on macOS 10.15.1
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

    private void setInitialVariables() {
        gc = canvas.getGraphicsContext2D();
        filepath.setText("PPInetwork.txt");
        output.setText("output.txt");
        setSummary();
        infoArea.setText("Information Area:\n");
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
     * To clear all fields if uploading fails
     */
    private void clearAllFields() {
        // internal fields
        averageDegree = 0.0;
        numOfNodes = 0;
        numOfInteractions = 0;
        hubStr = "";
        maxDegree = 0;
        numOfEdges = 0;
        theNode = "";

        // GUI part
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        lineChart.getData().clear();
        table.getItems().clear();
    }

    /**
     * To create a network from the input filename
     */
    private void openFile() {
        network = new Network();
        String infoTxt = "";
        try {
            network.createNetworkFromFile(filepath.getText());
            numOfEdges = 0;
            theNode = "";
            updateSummary();
            updateLineChart();
            setTableView();
            drawCanvas();
            infoTxt = String.format("File %s uploaded successfully!\n", filepath.getText());
        } catch (IOException e) {
            clearAllFields();
            network = null;
            infoTxt = e.getMessage();
        } finally {
            setSummary();
            infoArea.appendText(infoTxt);
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
            return String.format("Cannot find edges for the Node: %s\n", theNode);
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
        } catch (IOException e) {
            return e.getMessage();
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
        if (network == null)
            throw new NullPointerException("Network has not been successfully initialised!\n");
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

    /**
     * To setup content in the degree distribution table
     */
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

    /**
     * To plot the visualised network
     * I won't explain these codes :(
     */
    private void drawCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // clear all content in the major plotting area
        List<String[]> names = network.getAllEdges();
        int minDegree = network.getMinDegree();
        int maxDegree = network.getMaxDegree();
        Map<String, CanvasHelper> nodeCoordinates = new HashMap<>();
        names.forEach(arr -> {
            if (!nodeCoordinates.containsKey(arr[0])) {
                nodeCoordinates.put(arr[0], getPlottingScope(arr[0], minDegree, maxDegree));
            }
            if (!nodeCoordinates.containsKey(arr[1])) {
                nodeCoordinates.put(arr[1], getPlottingScope(arr[1], minDegree, maxDegree));
            }
            CanvasHelper c1 = nodeCoordinates.get(arr[0]);
            CanvasHelper c2 = nodeCoordinates.get(arr[1]);

            if (arr[0].equals(arr[1])) {
                // for self-interaction
                gc.setStroke(Color.RED);
                gc.strokeOval(c1.xAxis + 3.75, c1.yAxis + 3.75, 30, 30);
            } else {
                // for non-self-interaction
                gc.setStroke(Color.GRAY);
                gc.strokeLine(c1.xAxis + 7.5, c1.yAxis + 7.5, c2.xAxis + 7.5, c2.yAxis + 7.5);
            }
        });

        gc.setStroke(Color.BLACK);
        nodeCoordinates.values().forEach(c -> {
            if (network.degreeOfNode(c.name) == maxDegree) gc.setFill(Color.RED);
            else gc.setFill(Color.BLUE);
            gc.fillOval(c.xAxis, c.yAxis, 15, 15);
            gc.strokeText(c.name, c.xAxis, c.yAxis);
        });
    }

    /**
     * To define the scope of the node which should be plotted in canvas
     *
     * @param nodeName
     * @param minDegree
     * @return
     */
    private CanvasHelper getPlottingScope(String nodeName, int minDegree, int maxDegree) {
        int degree = network.degreeOfNode(nodeName);
        return new CanvasHelper(nodeName, degree, minDegree, maxDegree);
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

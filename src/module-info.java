module ProteinNetworkAnalysis {
    requires javafx.controls;
    requires javafx.fxml;

    opens home to javafx.fxml;
    exports home;
}
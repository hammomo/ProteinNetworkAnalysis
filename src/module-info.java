module NetworkAnalysis {
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.graphics;

	opens home.helper to javafx.controls;
	exports home.helper;

	opens home to javafx.fxml;
	exports home;
}
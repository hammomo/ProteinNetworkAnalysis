module NetworkAnalysis {
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.graphics;
	
	opens home to javafx.fxml;
	exports home;
}
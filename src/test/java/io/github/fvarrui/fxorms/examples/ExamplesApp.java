package io.github.fvarrui.fxorms.examples;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/**
 * Shows every README example in its own tab. Run it from the IDE (test
 * classpath) to try the generated forms.
 */
public class ExamplesApp extends Application {

	@Override
	public void start(Stage stage) {
		TabPane tabs = new TabPane();
		tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		Examples.all().forEach((name, example) -> tabs.getTabs().add(new Tab(name, new ScrollPane(example.get()))));
		stage.setTitle("f(x)orms examples");
		stage.setScene(new Scene(tabs, 800, 520));
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}

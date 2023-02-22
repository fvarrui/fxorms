package io.github.fvarrui.fxorms.test;

import java.time.LocalDateTime;
import java.util.HashMap;

import io.github.fvarrui.fxorms.Form;
import io.github.fvarrui.fxorms.FormBuilder;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class EditUserApp extends Application {

	@SuppressWarnings("serial")
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		User user = new User(
				12345, 
				"Chuck Norris", 
				LocalDateTime.now(), 
				new HashMap<String, Object>() {{
					put("texto", "hola");
					put("numero", 123);
				}}, 
				true
		);
		
		Form<User> form = new FormBuilder<>(User.class)
				.registerFieldAdapter(null)
				.build();
		form.bind(user);
		
		BorderPane root = new BorderPane(form);

		Scene scene = new Scene(root, 600, 400);
		
		primaryStage.setTitle("f(x)orms test");
		primaryStage.setScene(scene);
		primaryStage.show();

	}

}

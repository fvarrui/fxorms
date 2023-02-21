package io.github.fvarrui.fxorms.test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import io.github.fvarrui.fxorms.Form;
import io.github.fvarrui.fxorms.FormBuilder;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class EditUserApp extends Application {

	@SuppressWarnings("serial")
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		User user = new User(
				"12345678Z", 
				1200.0, 
				"Chuck Norris", 
				LocalDateTime.now(), 
				new Image("/images/trash-alt-solid.png"), 
				new HashMap<String, Object>() {{
					put("texto", "hola");
					put("numero", 123);
				}}, 
				2.5, 
				Arrays.asList("Reserva1", "Reserva2"), 
				UUID.randomUUID(), 
				"chuck", 
				"cnorris@gmail.com", 
				true
		);
				
		
		Form<User> form = new FormBuilder<>(User.class).build();
		form.bind(user);
		
		BorderPane root = new BorderPane(form);

		Scene scene = new Scene(root, 640, 480);
		
		primaryStage.setTitle("FXorms test :: Edit user");
		primaryStage.setScene(scene);
		primaryStage.show();

	}

}

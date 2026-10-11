package com.example.app;

import com.example.model.Person;
import io.github.fvarrui.fxorms.Form;
import io.github.fvarrui.fxorms.FormBuilder;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Main {
	public static void main(String[] args) {
		Platform.startup(() -> {
			try {
				Person person = new Person();
				Form<Person> form = new FormBuilder<>(Person.class).build().bind(person);
				new Scene(form);
				form.applyCss();
				form.getChildren().stream().filter(n -> n instanceof Label).forEach(n -> System.out.println("row: " + ((Label) n).getText()));
				((TextField) form.getChildren().get(1)).setText("Grace");
				System.out.println("bound name: " + person.nameProperty().get());
				System.out.println("module: " + Form.class.getModule().getName());
			} catch (Throwable e) {
				e.printStackTrace();
			} finally {
				Platform.runLater(Platform::exit);
			}
		});
	}
}

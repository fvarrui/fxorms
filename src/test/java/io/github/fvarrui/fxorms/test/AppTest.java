package io.github.fvarrui.fxorms.test;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javafx.application.Application;

public class AppTest {

	@Test
	@Disabled("Manual demo: opens a window and blocks until it is closed")
	public void testApp() {
		Application.launch(EditUserApp.class);
	}

}

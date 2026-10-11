package io.github.fvarrui.fxorms.examples;

import static io.github.fvarrui.fxorms.test.FxThread.call;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import io.github.fvarrui.fxorms.Form;
import io.github.fvarrui.fxorms.adapters.TextBinding;
import javafx.scene.Parent;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Keeps the README examples working: each one must build, and the behaviour
 * the README describes must hold.
 */
public class ExamplesTest {

	@Test
	void everyExampleBuilds() {
		Examples.all().forEach(example -> assertInstanceOf(Parent.class, call(example.factory()::get), example.id()));
	}

	@Test
	void quickStartShowsTheBean() {
		Form<?> form = (Form<?>) call(Examples::quickStart);
		assertEquals(List.of("First name:", "Last name:", "Email:", "Birth date:", "Subscribed:"), labels(form));
		assertEquals("ada@example.com", ((TextField) form.getControl("email")).getText());
	}

	@Test
	void customizingFollowsTheAnnotations() {
		Locale previous = Locale.getDefault();
		Locale.setDefault(Locale.US);
		try {
			Form<?> form = (Form<?>) call(Examples::customizing);
			assertEquals(List.of("SKU:", "Name:", "Description:", "Category:", "Price:", "Units in stock:", "Available from:"), labels(form));
			assertEquals("1,234.50", ((TextField) form.getControl("price")).getText());
			assertEquals("2025-05-25 09:00", ((TextField) form.getControl("availableFrom")).getText());
		} finally {
			Locale.setDefault(previous);
		}
	}

	@Test
	void validationKeepsTheBeanValue() {
		Form<?> form = (Form<?>) call(Examples::validation);
		TextField stock = (TextField) form.getControl("stock");
		assertTrue(stock.getPseudoClassStates().contains(TextBinding.INVALID));
		assertEquals(25, ((Product) form.getBean()).stockProperty().get());
	}

	@Test
	void inheritanceShowsSuperclassRowsFirst() {
		Form<?> form = (Form<?>) call(Examples::inheritance);
		assertEquals(List.of("First name:", "Last name:", "Email:", "Birth date:", "Subscribed:", "Department:", "Salary:"), labels(form));
	}

	@Test
	void masterDetailBindsTheSelection() {
		Parent root = call(Examples::masterDetail);
		Form<?> form = (Form<?>) root.getChildrenUnmodifiable().get(1);
		assertEquals(LocalDate.of(1906, 12, 9), ((Person) form.getBean()).getBirthDate());
	}

	@Test
	void customAdaptersRenderTheirTypes() {
		Form<?> theme = (Form<?>) call(Examples::customAdapter);
		assertInstanceOf(ColorPicker.class, theme.getControl("background"));
		Form<?> audio = (Form<?>) call(Examples::customAnnotation);
		assertInstanceOf(Slider.class, audio.getControl("volume"));
		assertInstanceOf(TextField.class, audio.getControl("sampleRate"));
	}

	private static List<String> labels(Form<?> form) {
		return form.getChildren().stream()
				.filter(node -> GridPane.getColumnIndex(node) == 0)
				.map(node -> ((Label) node).getText())
				.collect(Collectors.toList());
	}

}

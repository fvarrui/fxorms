package io.github.fvarrui.fxorms.examples;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import io.github.fvarrui.fxorms.Form;
import io.github.fvarrui.fxorms.FormBuilder;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * The use cases shown in the README, one method each. They are run by
 * {@link ExamplesApp} (to try them), {@code ExamplesTest} (to keep them
 * working) and {@code ScreenshotsTest} (to regenerate the README images).
 */
public final class Examples {

	private Examples() {
	}

	/**
	 * A README example: its id is the screenshot file name, its title the window
	 * title in the screenshot and the tab name in {@link ExamplesApp}.
	 */
	public record Example(String id, String title, Supplier<Parent> factory) {
	}

	/** Every example, in README order. */
	public static List<Example> all() {
		return List.of(
				new Example("quick-start", "Quick start", Examples::quickStart),
				new Example("edit-dialog", "Edit in a dialog", Examples::editDialog),
				new Example("master-detail", "Master-detail", Examples::masterDetail),
				new Example("read-only", "Read-only and computed values", Examples::readOnly),
				new Example("customizing", "Labels, order and formats", Examples::customizing),
				new Example("validation", "Input validation", Examples::validation),
				new Example("collections", "Lists and maps", Examples::collections),
				new Example("inheritance", "Inheritance", Examples::inheritance),
				new Example("custom-adapter", "Custom adapter", Examples::customAdapter),
				new Example("custom-annotation", "Custom annotation", Examples::customAnnotation),
				new Example("styling", "Styling", Examples::styling));
	}

	public static Parent quickStart() {
		Person person = new Person("Ada", "Lovelace", "ada@example.com", LocalDate.of(1815, 12, 10), true);
		return new FormBuilder<>(Person.class).build().bind(person);
	}

	public static Parent editDialog() {
		Person person = new Person("Grace", "Hopper", "grace@example.com", LocalDate.of(1906, 12, 9), false);

		// edit a copy, so Cancel leaves the original untouched
		Person draft = new Person();
		draft.copyFrom(person);

		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Contacts");
		dialog.setHeaderText("Edit contact");
		dialog.getDialogPane().setContent(new FormBuilder<>(Person.class).build().bind(draft));
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// in an application:
		// if (dialog.showAndWait().filter(ButtonType.OK::equals).isPresent()) {
		//     person.copyFrom(draft);
		// }
		return dialog.getDialogPane();
	}

	public static Parent readOnly() {
		Account account = new Account(1042, "ada", LocalDateTime.of(2024, 3, 1, 9, 30, 0));
		Form<Account> form = new FormBuilder<>(Account.class).build().bind(account);
		account.login();
		account.login();
		account.login(); // the "Logins" row follows the bean
		return form;
	}

	public static Parent customizing() {
		return new FormBuilder<>(Product.class).build().bind(sampleProduct());
	}

	public static Parent validation() {
		Product product = sampleProduct();
		Form<Product> form = new FormBuilder<>(Product.class).build().bind(product);
		// what a user might type: the field turns red and product.getStock() keeps 25
		((TextField) form.getControl("stock")).setText("25 units");
		return form;
	}

	public static Parent collections() {
		Project project = new Project();
		project.nameProperty().set("fxorms");
		project.tagsProperty().addAll("javafx", "forms", "library");
		project.settingsProperty().put("java.version", 17);
		project.settingsProperty().put("javafx.version", "21");
		project.settingsProperty().put("headless.tests", true);
		return new FormBuilder<>(Project.class).build().bind(project);
	}

	public static Parent inheritance() {
		Employee employee = new Employee("Alan", "Turing", "alan@example.com", LocalDate.of(1912, 6, 23), "Research", 4250.5);
		return new FormBuilder<>(Employee.class).build().bind(employee);
	}

	public static Parent masterDetail() {
		ListView<Person> list = new ListView<>(FXCollections.observableArrayList(
				new Person("Ada", "Lovelace", "ada@example.com", LocalDate.of(1815, 12, 10), true),
				new Person("Grace", "Hopper", "grace@example.com", LocalDate.of(1906, 12, 9), false),
				new Person("Alan", "Turing", "alan@example.com", LocalDate.of(1912, 6, 23), true)));
		list.setPrefSize(160, 200);

		// one form, rebound to the selected person
		Form<Person> form = new FormBuilder<>(Person.class).build();
		list.getSelectionModel().selectedItemProperty().addListener((o, previous, selected) -> form.bind(selected));
		list.getSelectionModel().select(1);

		HBox root = new HBox(10, list, form);
		root.setPadding(new Insets(5));
		return root;
	}

	public static Parent customAdapter() {
		Theme theme = new Theme("Ocean", Color.web("#0b2545"), Color.web("#eef4ed"), Color.web("#13c4a3"), true);
		return new FormBuilder<>(Theme.class)
				.registerFieldAdapter(new ColorAdapter())
				.build()
				.bind(theme);
	}

	public static Parent customAnnotation() {
		AudioSettings settings = new AudioSettings("Speakers (USB Audio)", 65, -0.25, 48000);
		return new FormBuilder<>(AudioSettings.class)
				.registerFieldAdapter(new SliderAdapter())
				.build()
				.bind(settings);
	}

	public static Parent styling() {
		Form<Product> form = new FormBuilder<>(Product.class).build().bind(sampleProduct());
		form.getStylesheets().add(Examples.class.getResource("styling.css").toExternalForm());
		((TextField) form.getControl("price")).setText("free");
		return form;
	}

	private static Product sampleProduct() {
		Product product = new Product();
		product.codeProperty().set("BK-0042");
		product.nameProperty().set("The Hitchhiker's Guide to the Galaxy");
		product.descriptionProperty().set("Paperback edition.\nIncludes the original radio scripts.");
		product.categoryProperty().set(Product.Category.BOOKS);
		product.priceProperty().set(1234.5);
		product.stockProperty().set(25);
		product.availableFromProperty().set(LocalDateTime.of(2025, 5, 25, 9, 0));
		return product;
	}

}

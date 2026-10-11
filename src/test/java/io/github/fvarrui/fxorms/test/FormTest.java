package io.github.fvarrui.fxorms.test;

import static io.github.fvarrui.fxorms.test.FxThread.call;
import static io.github.fvarrui.fxorms.test.FxThread.run;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.fvarrui.fxorms.Form;
import io.github.fvarrui.fxorms.FormBuilder;
import io.github.fvarrui.fxorms.adapters.FieldAdapter;
import io.github.fvarrui.fxorms.adapters.ObjectAdapter;
import io.github.fvarrui.fxorms.adapters.TextBinding;
import io.github.fvarrui.fxorms.annotations.Multiline;
import io.github.fvarrui.fxorms.annotations.Format;
import io.github.fvarrui.fxorms.annotations.ReadOnly;
import io.github.fvarrui.fxorms.components.ListEditor;
import io.github.fvarrui.fxorms.components.MapEditor;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class FormTest {

	// sample beans

	public static class Person {
		StringProperty firstName = new SimpleStringProperty();
		@Multiline(rows = 3)
		StringProperty notes = new SimpleStringProperty();
		IntegerProperty age = new SimpleIntegerProperty();
		DoubleProperty height = new SimpleDoubleProperty();
		BooleanProperty active = new SimpleBooleanProperty();
		ObjectProperty<LocalDateTime> birth = new SimpleObjectProperty<>();
		ListProperty<Object> tags = new SimpleListProperty<>(FXCollections.observableArrayList());
		MapProperty<String, Object> extra = new SimpleMapProperty<>(FXCollections.observableHashMap());
		String notAProperty = "ignored";
	}

	public static class ReadOnlyBean {
		@ReadOnly
		StringProperty code = new SimpleStringProperty();
		ReadOnlyStringWrapper label = new ReadOnlyStringWrapper();
		ReadOnlyIntegerWrapper count = new ReadOnlyIntegerWrapper();
	}

	public static class ConcreteTypesBean {
		SimpleStringProperty name = new SimpleStringProperty();
		SimpleIntegerProperty size = new SimpleIntegerProperty();
	}

	public static class GenericsBean {
		ObjectProperty<List<String>> nested = new SimpleObjectProperty<>();
		ObjectProperty<Color> color = new SimpleObjectProperty<>();
	}

	public static class ColorAdapter extends ObjectAdapter<ColorPicker, Property<Color>> {
		public ColorAdapter() {
			super(Color.class);
		}
		@Override
		public ColorPicker build(Field field) {
			return new ColorPicker();
		}
		@Override
		public void doBind(ColorPicker control, Property<Color> property) {
			control.valueProperty().bindBidirectional(property);
		}
		@Override
		public void doUnbind(ColorPicker control, Property<Color> property) {
			control.valueProperty().unbindBidirectional(property);
		}
	}

	public static class UpperCaseLabelAdapter extends FieldAdapter<Label, StringProperty> {
		public UpperCaseLabelAdapter() {
			super(StringProperty.class);
		}
		@Override
		public Label build(Field field) {
			return new Label();
		}
		@Override
		public void doBind(Label control, StringProperty property) {
			control.textProperty().bind(property.map(String::toUpperCase));
		}
		@Override
		public void doUnbind(Label control, StringProperty property) {
			control.textProperty().unbind();
		}
	}

	// helpers

	private static <T extends Node> T control(Form<?> form, String label, Class<T> type) {
		for (Node node : form.getChildren()) {
			if (node instanceof Label && (label + ":").equals(((Label) node).getText())
					&& GridPane.getColumnIndex(node) == 0) {
				int row = GridPane.getRowIndex(node);
				for (Node other : form.getChildren()) {
					if (GridPane.getRowIndex(other) == row && GridPane.getColumnIndex(other) == 1) {
						return assertInstanceOf(type, other, "control for '" + label + "'");
					}
				}
			}
		}
		throw new AssertionError("No row labelled '" + label + "'. Labels: " + labels(form));
	}

	private static List<String> labels(Form<?> form) {
		return form.getChildren().stream()
				.filter(n -> GridPane.getColumnIndex(n) == 0)
				.map(n -> ((Label) n).getText())
				.collect(Collectors.toList());
	}

	@BeforeAll
	static void startFx() {
		FxThread.start();
	}

	// building

	@Test
	void buildsOneRowPerPropertyFieldWithReadableLabels() {
		Form<Person> form = call(() -> new FormBuilder<>(Person.class).build());
		assertEquals(
				List.of("First name:", "Notes:", "Age:", "Height:", "Active:", "Birth:", "Tags:", "Extra:"),
				labels(form));
	}

	@Test
	void picksControlByPropertyType() {
		Form<Person> form = call(() -> new FormBuilder<>(Person.class).build());
		control(form, "First name", TextField.class);
		TextArea notes = control(form, "Notes", TextArea.class);
		assertEquals(3, notes.getPrefRowCount());
		control(form, "Age", TextField.class);
		control(form, "Height", TextField.class);
		control(form, "Active", CheckBox.class);
		control(form, "Birth", TextField.class);
		control(form, "Tags", ListEditor.class);
		control(form, "Extra", MapEditor.class);
	}

	@Test
	void readOnlyAnnotationMakesControlNotEditable() {
		Form<ReadOnlyBean> form = call(() -> new FormBuilder<>(ReadOnlyBean.class).build());
		assertFalse(control(form, "Code", TextInputControl.class).isEditable());
	}

	@Test
	void readOnlyWrappersAreRenderedAsReadOnlyInputs() {
		Form<ReadOnlyBean> form = call(() -> new FormBuilder<>(ReadOnlyBean.class).build());
		assertFalse(control(form, "Label", TextInputControl.class).isEditable());
		assertFalse(control(form, "Count", TextInputControl.class).isEditable());
	}

	@Test
	void concretePropertyTypesAreSupported() {
		Form<ConcreteTypesBean> form = call(() -> new FormBuilder<>(ConcreteTypesBean.class).build());
		control(form, "Name", TextField.class);
		control(form, "Size", TextField.class);
	}

	@Test
	void nestedGenericObjectPropertyDoesNotBreakTheBuild() {
		assertDoesNotThrow(() -> call(() -> new FormBuilder<>(GenericsBean.class).build()));
	}

	// binding

	@Test
	void bindsTextBidirectionally() {
		Person person = new Person();
		person.firstName.set("Ada");
		Form<Person> form = call(() -> new FormBuilder<>(Person.class).build().bind(person));
		TextField field = control(form, "First name", TextField.class);
		assertEquals("Ada", call(field::getText));
		run(() -> field.setText("Grace"));
		assertEquals("Grace", person.firstName.get());
	}

	@Test
	void bindsNumbersAndBooleans() {
		Person person = new Person();
		person.age.set(36);
		person.active.set(true);
		Form<Person> form = call(() -> new FormBuilder<>(Person.class).build().bind(person));
		TextField age = control(form, "Age", TextField.class);
		CheckBox active = control(form, "Active", CheckBox.class);
		assertEquals("36", call(age::getText));
		assertTrue(call(active::isSelected));
		run(() -> {
			age.setText("40");
			active.setSelected(false);
		});
		assertEquals(40, person.age.get());
		assertFalse(person.active.get());
	}

	@Test
	void bindsLocalDateTime() {
		Person person = new Person();
		person.birth.set(LocalDateTime.of(2000, 1, 2, 3, 4));
		Form<Person> form = call(() -> new FormBuilder<>(Person.class).build().bind(person));
		assertFalse(call(control(form, "Birth", TextField.class)::getText).isEmpty());
	}

	@Test
	void unbindStopsSynchronisation() {
		Person person = new Person();
		Form<Person> form = call(() -> new FormBuilder<>(Person.class).build().bind(person));
		TextField field = control(form, "First name", TextField.class);
		run(() -> {
			form.unbind();
			field.setText("changed");
		});
		assertEquals(null, person.firstName.get());
	}

	@Test
	void rebindingToAnotherBeanDetachesThePreviousOne() {
		Person first = new Person();
		Person second = new Person();
		Form<Person> form = call(() -> new FormBuilder<>(Person.class).build());
		TextField field = control(form, "First name", TextField.class);
		run(() -> {
			form.bind(first);
			form.bind(second);
			field.setText("only second");
		});
		assertEquals("only second", second.firstName.get());
		assertEquals(null, first.firstName.get(), "first bean must not receive edits after rebinding");
	}

	@Test
	void formsBuiltFromTheSameBuilderAreIndependent() {
		FormBuilder<Person> builder = new FormBuilder<>(Person.class);
		Person person = new Person();
		Form<Person> first = call(builder::build);
		Form<Person> second = call(builder::build);
		assertNotSame(first, second);
		run(() -> first.bind(person));
		TextField field = control(first, "First name", TextField.class);
		run(() -> field.setText("from first form"));
		assertEquals("from first form", person.firstName.get());
	}

	// custom adapters

	@Test
	void customAdapterCanBeRegistered() {
		Form<GenericsBean> form = call(() -> new FormBuilder<>(GenericsBean.class)
				.registerFieldAdapter(new ColorAdapter())
				.build());
		control(form, "Color", ColorPicker.class);
	}

	@Test
	void customAdapterTakesPrecedenceOverBuiltIns() {
		Person person = new Person();
		person.firstName.set("ada");
		Form<Person> form = call(() -> new FormBuilder<>(Person.class)
				.registerFieldAdapter(new UpperCaseLabelAdapter())
				.build()
				.bind(person));
		assertEquals("ADA", call(control(form, "First name", Label.class)::getText));
	}

	// number and date-time formatting

	public static class FormattedBean {
		IntegerProperty id = new SimpleIntegerProperty();
		@Format("#,##0.00")
		DoubleProperty price = new SimpleDoubleProperty();
		ObjectProperty<LocalDateTime> when = new SimpleObjectProperty<>();
		@Format("yyyy-MM-dd HH:mm:ss")
		ObjectProperty<LocalDateTime> stamp = new SimpleObjectProperty<>();
	}

	private static <T> T withLocale(Locale locale, Callable<T> task) throws Exception {
		Locale previous = Locale.getDefault();
		Locale.setDefault(locale);
		try {
			return task.call();
		} finally {
			Locale.setDefault(previous);
		}
	}

	private static boolean isInvalid(Node control) {
		return call(() -> control.getPseudoClassStates().contains(TextBinding.INVALID));
	}

	@Test
	void integersAreShownWithoutGrouping() throws Exception {
		FormattedBean bean = new FormattedBean();
		bean.id.set(12345);
		Form<FormattedBean> form = withLocale(Locale.US, () -> call(() -> new FormBuilder<>(FormattedBean.class).build().bind(bean)));
		assertEquals("12345", call(control(form, "Id", TextField.class)::getText));
	}

	@Test
	void invalidNumberIsMarkedAndLeavesThePropertyUnchanged() {
		Person person = new Person();
		person.age.set(36);
		Form<Person> form = call(() -> new FormBuilder<>(Person.class).build().bind(person));
		TextField age = control(form, "Age", TextField.class);
		for (String text : List.of("abc", "12abc", "", "3.7")) {
			run(() -> age.setText(text));
			assertEquals(36, person.age.get(), "'" + text + "' must not change the property");
			assertTrue(isInvalid(age), "'" + text + "' must mark the field as invalid");
		}
		run(() -> age.setText("41"));
		assertEquals(41, person.age.get());
		assertFalse(isInvalid(age));
	}

	@Test
	void formatAnnotationAppliesToNumbers() throws Exception {
		FormattedBean bean = new FormattedBean();
		bean.price.set(1234.5);
		Form<FormattedBean> form = withLocale(Locale.US, () -> call(() -> new FormBuilder<>(FormattedBean.class).build().bind(bean)));
		TextField price = control(form, "Price", TextField.class);
		assertEquals("1,234.50", call(price::getText));
		run(() -> price.setText("99.9"));
		assertEquals(99.9, bean.price.get());
	}

	@Test
	void editingADateTimeKeepsItsSeconds() throws Exception {
		FormattedBean bean = new FormattedBean();
		bean.when.set(LocalDateTime.of(2024, 1, 2, 3, 4, 5));
		Form<FormattedBean> form = withLocale(Locale.US, () -> call(() -> new FormBuilder<>(FormattedBean.class).build().bind(bean)));
		TextField when = control(form, "When", TextField.class);
		assertEquals("Jan 2, 2024, 3:04:05 AM", call(when::getText));
		run(() -> when.setText("Jan 2, 2024, 3:05:05 am"));
		assertEquals(LocalDateTime.of(2024, 1, 2, 3, 5, 5), bean.when.get());
		assertFalse(isInvalid(when));
	}

	@Test
	void invalidDateTimeIsMarkedAndEmptyClearsIt() {
		FormattedBean bean = new FormattedBean();
		LocalDateTime value = LocalDateTime.of(2024, 1, 2, 3, 4, 5);
		bean.when.set(value);
		Form<FormattedBean> form = call(() -> new FormBuilder<>(FormattedBean.class).build().bind(bean));
		TextField when = control(form, "When", TextField.class);
		run(() -> when.setText("not a date"));
		assertEquals(value, bean.when.get());
		assertTrue(isInvalid(when));
		run(() -> when.setText(""));
		assertEquals(null, bean.when.get());
		assertFalse(isInvalid(when));
	}

	@Test
	void formatAnnotationAppliesToDateTimes() {
		FormattedBean bean = new FormattedBean();
		bean.stamp.set(LocalDateTime.of(2024, 1, 2, 3, 4, 5));
		Form<FormattedBean> form = call(() -> new FormBuilder<>(FormattedBean.class).build().bind(bean));
		TextField stamp = control(form, "Stamp", TextField.class);
		assertEquals("2024-01-02 03:04:05", call(stamp::getText));
		run(() -> stamp.setText("2025-06-07 08:09:10"));
		assertEquals(LocalDateTime.of(2025, 6, 7, 8, 9, 10), bean.stamp.get());
	}

	@Test
	void modelChangesClearTheInvalidMark() {
		Person person = new Person();
		Form<Person> form = call(() -> new FormBuilder<>(Person.class).build().bind(person));
		TextField age = control(form, "Age", TextField.class);
		run(() -> age.setText("abc"));
		run(() -> person.age.set(7));
		assertEquals("7", call(age::getText));
		assertFalse(isInvalid(age));
	}

	// collection editors

	@Test
	void mapEditorShowsEntriesAndRemovalUpdatesTheBean() {
		Person person = new Person();
		person.extra.put("a", 1);
		person.extra.put("b", 2);
		Form<Person> form = call(() -> new FormBuilder<>(Person.class).build().bind(person));
		MapEditor editor = control(form, "Extra", MapEditor.class);
		@SuppressWarnings("unchecked")
		TableView<Object> table = (TableView<Object>) editor.lookup("TableView");
		assertEquals(2, call(() -> table.getItems().size()));
		run(() -> {
			table.getSelectionModel().select(0);
			editor.lookup(".button").fireEvent(new javafx.event.ActionEvent());
		});
		assertEquals(1, person.extra.size(), "removing an entry in the editor must remove it from the bean");
	}

	@Test
	void mapEditorDoesNotDuplicateEntriesWhenAValueIsReplaced() {
		Person person = new Person();
		person.extra.put("a", 1);
		Form<Person> form = call(() -> new FormBuilder<>(Person.class).build().bind(person));
		MapEditor editor = control(form, "Extra", MapEditor.class);
		TableView<?> table = (TableView<?>) editor.lookup("TableView");
		run(() -> person.extra.put("a", 2));
		assertEquals(1, call(() -> table.getItems().size()));
	}

	@Test
	void listEditorRemovalUpdatesTheBean() {
		Person person = new Person();
		person.tags.addAll("x", "y");
		Form<Person> form = call(() -> new FormBuilder<>(Person.class).build().bind(person));
		ListEditor editor = control(form, "Tags", ListEditor.class);
		ListView<?> list = (ListView<?>) editor.lookup("ListView");
		run(() -> {
			list.getSelectionModel().select(0);
			editor.lookup(".button").fireEvent(new javafx.event.ActionEvent());
		});
		assertEquals(List.of("y"), person.tags);
	}

	@Test
	void demoUserFormBuildsAndBinds() {
		User user = new User(1, "Chuck", LocalDateTime.now(), Map.of("k", "v"), false);
		assertDoesNotThrow(() -> call(() -> new FormBuilder<>(User.class).build().bind(user)));
		assertFalse(user.isEnabled(), "User constructor must honour the 'enabled' argument");
	}

}

package io.github.fvarrui.fxorms.test;

import static io.github.fvarrui.fxorms.test.FxThread.call;
import static io.github.fvarrui.fxorms.test.FxThread.run;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.fvarrui.fxorms.BeanProperty;
import io.github.fvarrui.fxorms.Form;
import io.github.fvarrui.fxorms.FormBuilder;
import io.github.fvarrui.fxorms.adapters.FieldAdapter;
import io.github.fvarrui.fxorms.adapters.ObjectAdapter;
import io.github.fvarrui.fxorms.adapters.TextBinding;
import io.github.fvarrui.fxorms.annotations.Multiline;
import io.github.fvarrui.fxorms.annotations.Order;
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
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
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
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
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
		public StringProperty firstNameProperty() { return firstName; }
		public StringProperty notesProperty() { return notes; }
		public IntegerProperty ageProperty() { return age; }
		public DoubleProperty heightProperty() { return height; }
		public BooleanProperty activeProperty() { return active; }
		public ObjectProperty<LocalDateTime> birthProperty() { return birth; }
		public ListProperty<Object> tagsProperty() { return tags; }
		public MapProperty<String, Object> extraProperty() { return extra; }
		public String getNotAProperty() { return notAProperty; }
	}

	public static class ReadOnlyBean {
		@ReadOnly
		StringProperty code = new SimpleStringProperty();
		ReadOnlyStringWrapper label = new ReadOnlyStringWrapper();
		ReadOnlyIntegerWrapper count = new ReadOnlyIntegerWrapper();
		public StringProperty codeProperty() { return code; }
		public ReadOnlyStringProperty labelProperty() { return label.getReadOnlyProperty(); }
		public ReadOnlyIntegerProperty countProperty() { return count.getReadOnlyProperty(); }
	}

	public static class ConcreteTypesBean {
		SimpleStringProperty name = new SimpleStringProperty();
		SimpleIntegerProperty size = new SimpleIntegerProperty();
		public SimpleStringProperty nameProperty() { return name; }
		public SimpleIntegerProperty sizeProperty() { return size; }
	}

	public static class GenericsBean {
		ObjectProperty<List<String>> nested = new SimpleObjectProperty<>();
		ObjectProperty<Color> color = new SimpleObjectProperty<>();
		public ObjectProperty<List<String>> nestedProperty() { return nested; }
		public ObjectProperty<Color> colorProperty() { return color; }
	}

	public static class ColorAdapter extends ObjectAdapter<ColorPicker, Property<Color>> {
		public ColorAdapter() {
			super(Color.class);
		}
		@Override
		public ColorPicker build(BeanProperty property) {
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
		public Label build(BeanProperty property) {
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
		public IntegerProperty idProperty() { return id; }
		public DoubleProperty priceProperty() { return price; }
		public ObjectProperty<LocalDateTime> whenProperty() { return when; }
		public ObjectProperty<LocalDateTime> stampProperty() { return stamp; }
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

	// date and enum adapters

	public enum Size { SMALL, MEDIUM, LARGE }

	public static class DatesAndEnumsBean {
		ObjectProperty<LocalDate> day = new SimpleObjectProperty<>();
		ObjectProperty<Size> size = new SimpleObjectProperty<>();
		@ReadOnly
		ObjectProperty<LocalDate> fixedDay = new SimpleObjectProperty<>();
		@ReadOnly
		ObjectProperty<Size> fixedSize = new SimpleObjectProperty<>();
		@Format("dd/MM/yyyy")
		ObjectProperty<LocalDate> formattedDay = new SimpleObjectProperty<>();
		public ObjectProperty<LocalDate> dayProperty() { return day; }
		public ObjectProperty<Size> sizeProperty() { return size; }
		public ObjectProperty<LocalDate> fixedDayProperty() { return fixedDay; }
		public ObjectProperty<Size> fixedSizeProperty() { return fixedSize; }
		public ObjectProperty<LocalDate> formattedDayProperty() { return formattedDay; }
	}

	@Test
	void localDateIsEditedWithADatePicker() {
		DatesAndEnumsBean bean = new DatesAndEnumsBean();
		bean.day.set(LocalDate.of(2024, 1, 2));
		Form<DatesAndEnumsBean> form = call(() -> new FormBuilder<>(DatesAndEnumsBean.class).build().bind(bean));
		DatePicker day = control(form, "Day", DatePicker.class);
		assertEquals(LocalDate.of(2024, 1, 2), call(day::getValue));
		run(() -> day.setValue(LocalDate.of(2025, 3, 4)));
		assertEquals(LocalDate.of(2025, 3, 4), bean.day.get());
		run(() -> bean.day.set(LocalDate.of(2026, 5, 6)));
		assertEquals(LocalDate.of(2026, 5, 6), call(day::getValue));
	}

	@Test
	void enumIsEditedWithAComboBoxOfItsConstants() {
		DatesAndEnumsBean bean = new DatesAndEnumsBean();
		bean.size.set(Size.MEDIUM);
		Form<DatesAndEnumsBean> form = call(() -> new FormBuilder<>(DatesAndEnumsBean.class).build().bind(bean));
		@SuppressWarnings("unchecked")
		ComboBox<Object> size = control(form, "Size", ComboBox.class);
		assertEquals(List.of(Size.SMALL, Size.MEDIUM, Size.LARGE), call(() -> List.copyOf(size.getItems())));
		assertEquals(Size.MEDIUM, call(size::getValue));
		run(() -> size.getSelectionModel().select(Size.LARGE));
		assertEquals(Size.LARGE, bean.size.get());
		run(() -> bean.size.set(Size.SMALL));
		assertEquals(Size.SMALL, call(size::getValue));
	}

	@Test
	void readOnlyDatesAndEnumsCannotBeEdited() {
		Form<DatesAndEnumsBean> form = call(() -> new FormBuilder<>(DatesAndEnumsBean.class).build());
		assertTrue(control(form, "Fixed day", DatePicker.class).isDisabled());
		assertTrue(control(form, "Fixed size", ComboBox.class).isDisabled());
		assertFalse(control(form, "Day", DatePicker.class).isDisabled());
		assertFalse(control(form, "Size", ComboBox.class).isDisabled());
	}

	@Test
	void formatAnnotationAppliesToDates() {
		DatesAndEnumsBean bean = new DatesAndEnumsBean();
		bean.formattedDay.set(LocalDate.of(2024, 1, 2));
		Form<DatesAndEnumsBean> form = call(() -> new FormBuilder<>(DatesAndEnumsBean.class).build().bind(bean));
		DatePicker day = control(form, "Formatted day", DatePicker.class);
		assertEquals("02/01/2024", call(() -> day.getConverter().toString(day.getValue())));
	}

	// property discovery through accessor methods

	public static class Employee extends Person {
		StringProperty company = new SimpleStringProperty();
		public StringProperty companyProperty() { return company; }
	}

	public static class AccessorBean {
		private final StringProperty title = new SimpleStringProperty();
		private final StringProperty computed = new SimpleStringProperty();
		private final ReadOnlyStringWrapper status = new ReadOnlyStringWrapper("draft");
		@Multiline(rows = 4)
		public StringProperty titleProperty() { return title; }
		public StringProperty summaryProperty() { return computed; } // no backing field named 'summary'
		@ReadOnly
		public StringProperty fixedProperty() { return computed; }
		public ReadOnlyStringProperty statusProperty() { return status.getReadOnlyProperty(); }
		public static StringProperty sharedProperty() { return new SimpleStringProperty(); }
	}

	public static class NodeBean extends Region {
		StringProperty caption = new SimpleStringProperty();
		public StringProperty captionProperty() { return caption; }
	}

	static class PackagePrivateBean {
		StringProperty name = new SimpleStringProperty("hidden");
		public StringProperty nameProperty() { return name; }
	}

	@Test
	void inheritedPropertiesAreIncludedAfterTheSuperclassOnes() {
		Form<Employee> form = call(() -> new FormBuilder<>(Employee.class).build());
		assertEquals(
				List.of("First name:", "Notes:", "Age:", "Height:", "Active:", "Birth:", "Tags:", "Extra:", "Company:"),
				labels(form));
	}

	@Test
	void propertiesWithoutBackingFieldComeLastAndStaticAccessorsAreIgnored() {
		Form<AccessorBean> form = call(() -> new FormBuilder<>(AccessorBean.class).build());
		assertEquals(List.of("Title:", "Status:", "Fixed:", "Summary:"), labels(form));
	}

	@Test
	void annotationsOnAccessorMethodsAreHonoured() {
		Form<AccessorBean> form = call(() -> new FormBuilder<>(AccessorBean.class).build());
		assertEquals(4, control(form, "Title", TextArea.class).getPrefRowCount());
		assertFalse(control(form, "Fixed", TextInputControl.class).isEditable());
	}

	@Test
	void readOnlyAccessorIsShownButNotEditable() {
		AccessorBean bean = new AccessorBean();
		Form<AccessorBean> form = call(() -> new FormBuilder<>(AccessorBean.class).build().bind(bean));
		TextField status = control(form, "Status", TextField.class);
		assertFalse(status.isEditable());
		assertEquals("draft", call(status::getText));
		run(() -> bean.status.set("published"));
		assertEquals("published", call(status::getText));
	}

	@Test
	void readOnlyNumberAccessorFollowsTheBean() {
		ReadOnlyBean bean = new ReadOnlyBean();
		Form<ReadOnlyBean> form = call(() -> new FormBuilder<>(ReadOnlyBean.class).build().bind(bean));
		TextField count = control(form, "Count", TextField.class);
		run(() -> bean.count.set(3));
		assertEquals("3", call(count::getText));
	}

	@Test
	void demoUserEnabledIsAReadOnlyCheckBox() {
		User user = new User(1, "Chuck", LocalDateTime.now(), Map.of(), true);
		Form<User> form = call(() -> new FormBuilder<>(User.class).build().bind(user));
		CheckBox enabled = control(form, "Enabled", CheckBox.class);
		assertTrue(enabled.isDisabled());
		assertTrue(call(enabled::isSelected));
	}

	@Test
	void javafxMethodsOfTheBeanSuperclassAreIgnored() {
		Form<NodeBean> form = call(() -> new FormBuilder<>(NodeBean.class).build());
		assertEquals(List.of("Caption:"), labels(form));
	}

	@Test
	void nonPublicBeanClassesWork() {
		PackagePrivateBean bean = new PackagePrivateBean();
		Form<PackagePrivateBean> form = call(() -> new FormBuilder<>(PackagePrivateBean.class).build().bind(bean));
		assertEquals("hidden", call(control(form, "Name", TextField.class)::getText));
	}

	// labels and order

	public static class LabelledBean {
		StringProperty first = new SimpleStringProperty();
		StringProperty userID = new SimpleStringProperty();
		@Order(1)
		StringProperty third = new SimpleStringProperty();
		@Order(0)
		@io.github.fvarrui.fxorms.annotations.Label("E-mail address")
		StringProperty email = new SimpleStringProperty();
		StringProperty URLPath = new SimpleStringProperty();
		public StringProperty firstProperty() { return first; }
		public StringProperty userIDProperty() { return userID; }
		public StringProperty thirdProperty() { return third; }
		public StringProperty emailProperty() { return email; }
		public StringProperty URLPathProperty() { return URLPath; }
	}

	@Test
	void labelAndOrderAnnotationsAndAcronymsInLabels() {
		Form<LabelledBean> form = call(() -> new FormBuilder<>(LabelledBean.class).build());
		assertEquals(List.of("E-mail address:", "Third:", "First:", "User ID:", "URL path:"), labels(form));
	}

	// default styles

	/** Background colour near the right edge of a text field, where there's no text. */
	private static Color backgroundOf(TextField field) {
		WritableImage image = field.snapshot(null, null);
		return image.getPixelReader().getColor((int) image.getWidth() - 4, (int) image.getHeight() / 2);
	}

	@Test
	void readOnlyTextInputsLookDifferentAndKeepTheirTextSelectable() {
		AccessorBean bean = new AccessorBean();
		Form<AccessorBean> form = call(() -> new FormBuilder<>(AccessorBean.class).build().bind(bean));
		TextField status = control(form, "Status", TextField.class);
		TextField plain = call(() -> {
			Form<Person> personForm = new FormBuilder<>(Person.class).build();
			new Scene(personForm);
			personForm.applyCss();
			personForm.layout();
			return control(personForm, "First name", TextField.class);
		});
		Color editableBackground = call(() -> backgroundOf(plain));
		Color readOnlyBackground = call(() -> {
			new Scene(form);
			form.applyCss();
			form.layout();
			return backgroundOf(status);
		});
		assertEquals(Color.WHITE, editableBackground, "editable fields keep the default look");
		assertNotEquals(editableBackground, readOnlyBackground, "read-only fields must look different");
		run(status::selectAll);
		assertEquals("draft", call(status::getSelectedText), "read-only text stays selectable");
	}

	// access to controls

	@Test
	void getControlReturnsThePropertysControlBeforeAndAfterBinding() {
		Form<Person> form = call(() -> new FormBuilder<>(Person.class).build());
		TextField firstName = control(form, "First name", TextField.class);
		assertSame(firstName, form.getControl("firstName"));
		assertSame(control(form, "Extra", MapEditor.class), form.getControl("extra"));
		run(() -> form.bind(new Person()));
		assertSame(firstName, form.getControl("firstName"));
	}

	@Test
	void getControlRejectsUnknownProperties() {
		Form<Person> form = call(() -> new FormBuilder<>(Person.class).build());
		IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> form.getControl("nickname"));
		assertTrue(error.getMessage().contains("nickname"));
		assertTrue(error.getMessage().contains(Person.class.getName()));
		assertThrows(IllegalArgumentException.class, () -> form.getControl("notAProperty"));
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

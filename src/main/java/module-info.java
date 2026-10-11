/**
 * f(x)orms: JavaFX form builder.
 * <p>
 * Forms call the bean's public {@code xxxProperty()} accessors reflectively, so
 * an application module must export its bean packages to this module, e.g.
 * {@code exports com.example.model to io.github.fvarrui.fxorms;}.
 */
module io.github.fvarrui.fxorms {

	requires transitive javafx.controls;
	requires javafx.fxml;

	exports io.github.fvarrui.fxorms;
	exports io.github.fvarrui.fxorms.adapters;
	exports io.github.fvarrui.fxorms.annotations;
	exports io.github.fvarrui.fxorms.components;

	// ListEditor and MapEditor are their own FXML controllers
	opens io.github.fvarrui.fxorms.components to javafx.fxml;

}

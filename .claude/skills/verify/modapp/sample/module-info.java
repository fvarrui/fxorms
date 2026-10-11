// Minimal application module using fxorms from the module path (see ../../module-smoke-test.sh).
module sample {
	requires io.github.fvarrui.fxorms;
	exports com.example.app to javafx.graphics;
	// forms call the beans' accessors reflectively
	exports com.example.model to io.github.fvarrui.fxorms;
}

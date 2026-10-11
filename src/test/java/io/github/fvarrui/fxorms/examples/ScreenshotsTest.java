package io.github.fvarrui.fxorms.examples;

import static io.github.fvarrui.fxorms.test.FxThread.call;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import io.github.fvarrui.fxorms.examples.Examples.Example;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * Regenerates the README screenshots in {@code assets/examples/} from
 * {@link Examples}, each one inside a macOS-style window titled with the
 * example's title. Disabled by default; run it with:
 * <pre>
 * mvn test -Dtest=ScreenshotsTest -Dscreenshots=true
 * </pre>
 * It renders headless (Monocle), in the US locale, so the images don't depend
 * on the machine. The PNGs have a transparent background around the window's
 * shadow, so they look right on light and dark pages.
 */
@EnabledIfSystemProperty(named = "screenshots", matches = "true")
public class ScreenshotsTest {

	private static final double RADIUS = 10;

	@Test
	void generateScreenshots() throws IOException {
		Locale previous = Locale.getDefault();
		Locale.setDefault(Locale.US);
		try {
			File folder = new File("assets/examples");
			folder.mkdirs();
			for (Example example : Examples.all()) {
				WritableImage image = call(() -> snapshot(window(example.title(), example.factory().get())));
				ImageIO.write(toBufferedImage(image), "png", new File(folder, example.id() + ".png"));
			}
		} finally {
			Locale.setDefault(previous);
		}
	}

	/** The example inside a window frame: title bar with traffic lights, rounded corners and a shadow. */
	private static Parent window(String title, Parent content) {
		HBox lights = new HBox(8, light("#ff5f57", "#e0443e"), light("#febc2e", "#dea123"), light("#28c840", "#1aab29"));
		lights.setAlignment(Pos.CENTER_LEFT);
		lights.setMaxWidth(HBox.USE_PREF_SIZE);

		Label caption = new Label(title);
		caption.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #4a4a4a;");

		StackPane titleBar = new StackPane(caption, lights);
		StackPane.setAlignment(lights, Pos.CENTER_LEFT);
		titleBar.setPadding(new Insets(0, 14, 0, 14));
		titleBar.setMinHeight(34);
		titleBar.setStyle("-fx-background-color: linear-gradient(#f7f7f7, #e6e6e6);"
				+ "-fx-border-color: transparent transparent #d0d0d0 transparent;");

		StackPane body = new StackPane(content);
		body.setPadding(content instanceof DialogPane ? Insets.EMPTY : new Insets(12)); // a dialog fills its window
		body.setStyle("-fx-background-color: white;");

		VBox frame = new VBox(titleBar, body);
		frame.setStyle("-fx-background-color: white;"
				+ "-fx-border-color: rgba(0, 0, 0, 0.22);"
				+ "-fx-border-radius: " + RADIUS + ";"
				+ "-fx-background-radius: " + RADIUS + ";");
		Rectangle clip = new Rectangle();
		clip.setArcWidth(RADIUS * 2);
		clip.setArcHeight(RADIUS * 2);
		clip.widthProperty().bind(frame.widthProperty());
		clip.heightProperty().bind(frame.heightProperty());
		frame.setClip(clip);

		// the shadow goes on a wrapper, so the clip doesn't cut it
		StackPane shadow = new StackPane(frame);
		shadow.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(0, 0, 0, 0.28), 26, 0, 0, 8));

		StackPane root = new StackPane(shadow);
		root.setPadding(new Insets(20, 28, 36, 28));
		root.setStyle("-fx-background-color: transparent;");
		return root;
	}

	private static Circle light(String fill, String stroke) {
		Circle circle = new Circle(6, Color.web(fill));
		circle.setStroke(Color.web(stroke));
		circle.setStrokeWidth(0.6);
		return circle;
	}

	private static WritableImage snapshot(Parent root) {
		Scene scene = new Scene(root, Color.TRANSPARENT);
		root.applyCss();
		root.layout();
		return scene.snapshot(null);
	}

	private static BufferedImage toBufferedImage(WritableImage image) {
		BufferedImage result = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		PixelReader reader = image.getPixelReader();
		for (int y = 0; y < result.getHeight(); y++) {
			for (int x = 0; x < result.getWidth(); x++) {
				result.setRGB(x, y, reader.getArgb(x, y));
			}
		}
		return result;
	}

}

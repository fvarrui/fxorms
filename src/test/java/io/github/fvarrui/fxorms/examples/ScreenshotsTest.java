package io.github.fvarrui.fxorms.examples;

import static io.github.fvarrui.fxorms.test.FxThread.call;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;

/**
 * Regenerates the README screenshots in {@code assets/examples/} from
 * {@link Examples}. Disabled by default; run it with:
 * <pre>
 * mvn test -Dtest=ScreenshotsTest -Dscreenshots=true
 * </pre>
 * It renders headless (Monocle), in the US locale, so the images don't depend
 * on the machine.
 */
@EnabledIfSystemProperty(named = "screenshots", matches = "true")
public class ScreenshotsTest {

	@Test
	void generateScreenshots() throws IOException {
		Locale previous = Locale.getDefault();
		Locale.setDefault(Locale.US);
		try {
			File folder = new File("assets/examples");
			folder.mkdirs();
			for (var example : Examples.all().entrySet()) {
				WritableImage image = call(() -> snapshot(example.getValue().get()));
				ImageIO.write(toBufferedImage(image), "png", new File(folder, example.getKey() + ".png"));
			}
		} finally {
			Locale.setDefault(previous);
		}
	}

	private static WritableImage snapshot(Parent example) {
		StackPane root = new StackPane(example);
		root.setPadding(new Insets(8));
		root.setStyle("-fx-background-color: white;");
		Scene scene = new Scene(root);
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

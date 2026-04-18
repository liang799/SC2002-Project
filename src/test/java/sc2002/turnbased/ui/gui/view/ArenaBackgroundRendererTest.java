package sc2002.turnbased.ui.gui.view;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

class ArenaBackgroundRendererTest {
    private static final byte[] PNG_SIGNATURE = new byte[] {
        (byte) 0x89,
        'P',
        'N',
        'G',
        '\r',
        '\n',
        0x1A,
        '\n'
    };

    @Test
    void packagesBackgroundAsPngResource() throws IOException {
        try (InputStream inputStream = ArenaBackgroundRenderer.class.getResourceAsStream(
            ArenaBackgroundRenderer.BACKGROUND_RESOURCE
        )) {
            assertNotNull(inputStream);
            assertArrayEquals(PNG_SIGNATURE, inputStream.readNBytes(PNG_SIGNATURE.length));
        }
    }

    @Test
    void decodesPackagedBackgroundResource() throws IOException {
        try (InputStream inputStream = ArenaBackgroundRenderer.class.getResourceAsStream(
            ArenaBackgroundRenderer.BACKGROUND_RESOURCE
        )) {
            assertNotNull(inputStream);
            BufferedImage image = ImageIO.read(inputStream);

            assertNotNull(image);
            assertTrue(image.getWidth() > 0);
            assertTrue(image.getHeight() > 0);
        }
    }
}

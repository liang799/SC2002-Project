package sc2002.turnbased.ui.gui.view;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Objects;

import javax.imageio.ImageIO;

final class ArenaBackgroundRenderer {
    static final String BACKGROUND_RESOURCE = "/sc2002/turnbased/ui/gui/assets/arena-background.png";

    private final BufferedImage source;
    private BufferedImage scaledBackground;
    private int scaledWidth;
    private int scaledHeight;

    ArenaBackgroundRenderer() {
        this(loadResource(BACKGROUND_RESOURCE));
    }

    ArenaBackgroundRenderer(BufferedImage source) {
        this.source = Objects.requireNonNull(source, "source");
        if (source.getWidth() <= 0 || source.getHeight() <= 0) {
            throw new IllegalArgumentException("Background image must have positive dimensions.");
        }
    }

    void render(Graphics2D g, int width, int height) {
        Objects.requireNonNull(g, "g");
        if (width <= 0 || height <= 0) {
            return;
        }
        g.drawImage(scaledBackground(width, height), 0, 0, null);
    }

    private BufferedImage scaledBackground(int width, int height) {
        if (scaledBackground == null || width != scaledWidth || height != scaledHeight) {
            scaledBackground = createScaledBackground(width, height);
            scaledWidth = width;
            scaledHeight = height;
        }
        return scaledBackground;
    }

    private BufferedImage createScaledBackground(int width, int height) {
        BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D backgroundGraphics = target.createGraphics();
        try {
            backgroundGraphics.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR
            );
            backgroundGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            SourceCrop crop = coverCrop(width, height);
            backgroundGraphics.drawImage(
                source,
                0,
                0,
                width,
                height,
                crop.x(),
                crop.y(),
                crop.x() + crop.width(),
                crop.y() + crop.height(),
                null
            );
        } finally {
            backgroundGraphics.dispose();
        }
        return target;
    }

    private SourceCrop coverCrop(int width, int height) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        double sourceRatio = sourceWidth / (double) sourceHeight;
        double targetRatio = width / (double) height;
        if (sourceRatio > targetRatio) {
            int cropWidth = Math.max(1, (int) Math.round(sourceHeight * targetRatio));
            int cropX = (sourceWidth - cropWidth) / 2;
            return new SourceCrop(cropX, 0, cropWidth, sourceHeight);
        }
        int cropHeight = Math.max(1, (int) Math.round(sourceWidth / targetRatio));
        int cropY = (sourceHeight - cropHeight) / 2;
        return new SourceCrop(0, cropY, sourceWidth, cropHeight);
    }

    private static BufferedImage loadResource(String resourceName) {
        try (InputStream inputStream = ArenaBackgroundRenderer.class.getResourceAsStream(resourceName)) {
            if (inputStream == null) {
                throw new IllegalStateException("Missing arena background resource: " + resourceName);
            }
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                throw new IllegalStateException("Unreadable arena background resource: " + resourceName);
            }
            return image;
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to load arena background resource: " + resourceName, exception);
        }
    }

    private record SourceCrop(int x, int y, int width, int height) {
    }
}

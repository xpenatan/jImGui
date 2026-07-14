package imgui.example.renderer;

import io.github.libfdx.Fdx;
import io.github.libfdx.core.FdxException;
import io.github.libfdx.graphics.GraphicsFrame;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

public final class FdxExampleCapture {
    private final String screenshotPath;
    private final long screenshotAfterFrames;
    private final long exitAfterFrames;
    private long renderedFrames;
    private boolean screenshotWritten;

    public FdxExampleCapture() {
        screenshotPath = System.getProperty("jimgui.example.screenshot", "").trim();
        screenshotAfterFrames = parsePositiveLong(
                System.getProperty("jimgui.example.screenshotAfterFrames"), 3L);
        exitAfterFrames = parsePositiveLong(System.getProperty("jimgui.example.exitAfterFrames"), 0L);
    }

    public void onFrameEnd(Fdx fdx) {
        renderedFrames++;
        if (!screenshotWritten && screenshotPath.length() > 0
                && renderedFrames >= screenshotAfterFrames) {
            writeScreenshot(fdx);
        }
        if (exitAfterFrames > 0L && renderedFrames >= exitAfterFrames) {
            fdx.app().requestExit();
        }
    }

    private void writeScreenshot(Fdx fdx) {
        try {
            GraphicsFrame frame = fdx.graphics().main().currentFrame();
            writePpm(screenshotPath, frame.width(), frame.height(),
                    frame.frameBuffer().readPixelsRgba8());
            screenshotWritten = true;
            fdx.logger().info("jImGui wrote screenshot: " + screenshotPath);
        }
        catch (Exception error) {
            throw new FdxException("Could not write jImGui framebuffer screenshot", error);
        }
    }

    private static void writePpm(String path, int width, int height, ByteBuffer pixels)
            throws Exception {
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(width * height * 3 + 32);
        bytes.write(("P6\n" + width + " " + height + "\n255\n").getBytes("US-ASCII"));
        byte[] row = new byte[width * 3];
        for (int y = height - 1; y >= 0; y--) {
            int sourceOffset = y * width * 4;
            int targetOffset = 0;
            for (int x = 0; x < width; x++) {
                row[targetOffset++] = pixels.get(sourceOffset++);
                row[targetOffset++] = pixels.get(sourceOffset++);
                row[targetOffset++] = pixels.get(sourceOffset++);
                sourceOffset++;
            }
            bytes.write(row);
        }
        try (FileOutputStream output = new FileOutputStream(file)) {
            bytes.writeTo(output);
        }
    }

    private static long parsePositiveLong(String value, long fallback) {
        if (value == null || value.length() == 0) {
            return fallback;
        }
        long parsed = Long.parseLong(value);
        return parsed >= 0L ? parsed : fallback;
    }
}

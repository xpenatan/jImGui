package imgui.example.renderer.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.GdxRuntimeException;
import imgui.ImGui;
import imgui.ImGuiLoader;
import imgui.example.renderer.ImGuiRenderer;
import imgui.example.renderer.ImGuiShared;
import imgui.gdx.ImGuiGdxImpl;
import imgui.gdx.ImGuiGdxInputMultiplexer;

import java.util.function.Supplier;

public abstract class GdxImGuiExampleApplication extends ApplicationAdapter {
    private final Supplier<? extends ImGuiGdxImpl> rendererFactory;
    private final GdxGraphicsAdapter graphics;
    private ImGuiRenderer screen;
    private boolean screenShown;
    private String screenshotPath;
    private long screenshotAfterFrames;
    private long exitAfterFrames;
    private long renderedFrames;
    private boolean screenshotWritten;

    protected GdxImGuiExampleApplication(Supplier<? extends ImGuiGdxImpl> rendererFactory,
            GdxGraphicsAdapter graphics) {
        this.rendererFactory = rendererFactory;
        this.graphics = graphics;
    }

    @Override
    public final void create() {
        screenshotPath = System.getProperty("jimgui.example.screenshot", "");
        screenshotAfterFrames = parsePositiveLong(
                System.getProperty("jimgui.example.screenshotAfterFrames"), 3L);
        exitAfterFrames = parsePositiveLong(System.getProperty("jimgui.example.exitAfterFrames"), 0L);
        ImGuiLoader.init((success, error) -> {
            if (error != null) {
                throw new GdxRuntimeException("Could not load ImGui", error);
            }
            if (!success) {
                throw new GdxRuntimeException("Could not load ImGui");
            }
            loadExtensions(() -> {
                ImGui.CreateContext();
                ImGuiGdxImpl renderer = rendererFactory.get();
                ImGuiShared.initialize(new GdxImGuiExampleBackend(renderer, graphics));
                Gdx.input.setInputProcessor(new ImGuiGdxInputMultiplexer());
                screen = createScreen();
            });
        });
    }

    protected void loadExtensions(Runnable ready) {
        ready.run();
    }

    protected abstract ImGuiRenderer createScreen();

    @Override
    public final void render() {
        if (screen != null) {
            if (!screenShown) {
                screen.show();
                screenShown = true;
            }
            screen.render();
            renderedFrames++;
            writeScreenshotIfRequested();
            if (exitAfterFrames > 0L && renderedFrames >= exitAfterFrames) {
                Gdx.app.exit();
            }
        }
    }

    @Override
    public final void resize(int width, int height) {
        if (screen != null) {
            screen.resize(width, height);
        }
    }

    @Override
    public final void pause() {
        if (screen != null) {
            screen.pause();
        }
    }

    @Override
    public final void resume() {
        if (screen != null) {
            screen.resume();
        }
    }

    @Override
    public final void dispose() {
        if (screen != null) {
            screen.dispose();
            screen = null;
            screenShown = false;
        }
        ImGuiShared.dispose();
    }

    private void writeScreenshotIfRequested() {
        if (screenshotWritten || screenshotPath == null || screenshotPath.length() == 0
                || renderedFrames < screenshotAfterFrames) {
            return;
        }
        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getBackBufferWidth(),
                Gdx.graphics.getBackBufferHeight());
        Pixmap flipped = flipVertically(pixmap);
        PixmapIO.writePNG(Gdx.files.absolute(screenshotPath), flipped);
        flipped.dispose();
        pixmap.dispose();
        screenshotWritten = true;
        Gdx.app.log("jImGui", "Wrote screenshot: " + screenshotPath);
    }

    private static Pixmap flipVertically(Pixmap source) {
        Pixmap flipped = new Pixmap(source.getWidth(), source.getHeight(), source.getFormat());
        for (int y = 0; y < source.getHeight(); y++) {
            flipped.drawPixmap(source, 0, y, source.getWidth(), 1, 0, source.getHeight() - y - 1,
                    source.getWidth(), 1);
        }
        return flipped;
    }

    private static long parsePositiveLong(String value, long fallback) {
        if (value == null || value.length() == 0) {
            return fallback;
        }
        long parsed = Long.parseLong(value);
        return parsed >= 0L ? parsed : fallback;
    }
}

package imgui.example.renderer;

import imgui.ImGuiLoader;
import io.github.libfdx.Fdx;
import io.github.libfdx.application.ApplicationAdapter;
import io.github.libfdx.core.FdxException;
import io.github.libfdx.graphics.GraphicsAttachmentProvider;
import io.github.libfdx.imgui.FdxImGuiRenderer;

public abstract class ImGuiExampleApplication extends ApplicationAdapter {
    private final FdxImGuiRenderer renderer;
    private final GraphicsAttachmentProvider viewportGraphicsProvider;
    private Fdx fdx;
    private ImGuiRenderer screen;

    protected ImGuiExampleApplication(FdxImGuiRenderer renderer) {
        this(renderer, null);
    }

    protected ImGuiExampleApplication(FdxImGuiRenderer renderer,
            GraphicsAttachmentProvider viewportGraphicsProvider) {
        if (renderer == null) {
            throw new FdxException("FdxImGuiRenderer cannot be null");
        }
        this.renderer = renderer;
        this.viewportGraphicsProvider = viewportGraphicsProvider;
    }

    @Override
    public final void create(Fdx fdx) {
        this.fdx = fdx;
        ImGuiLoader.init((success, error) -> {
            if (error != null) {
                throw new FdxException("Could not load ImGui", error);
            }
            if (!success) {
                throw new FdxException("Could not load ImGui");
            }
            loadExtensions(() -> {
                ImGuiShared.initialize(new FdxImGuiExampleBackend(fdx, renderer, viewportGraphicsProvider));
                screen = createScreen();
                screen.show();
            });
        });
    }

    protected void loadExtensions(Runnable ready) {
        ready.run();
    }

    protected abstract ImGuiRenderer createScreen();

    protected final Fdx fdx() {
        return fdx;
    }

    @Override
    public final void render() {
        if (screen != null) {
            screen.render();
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
        }
        ImGuiShared.dispose();
        fdx = null;
    }
}

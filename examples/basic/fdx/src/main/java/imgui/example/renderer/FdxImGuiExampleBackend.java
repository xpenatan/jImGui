package imgui.example.renderer;

import io.github.libfdx.Fdx;
import io.github.libfdx.graphics.GraphicsAttachmentProvider;
import io.github.libfdx.graphics.GraphicsContext;
import io.github.libfdx.graphics.Texture;
import io.github.libfdx.graphics.TextureDescriptor;
import io.github.libfdx.imgui.FdxImGui;
import io.github.libfdx.imgui.FdxImGuiRenderer;

import java.nio.ByteBuffer;

public final class FdxImGuiExampleBackend implements ImGuiExampleBackend {
    private final Fdx fdx;
    private final FdxImGui imgui;

    public FdxImGuiExampleBackend(Fdx fdx, FdxImGuiRenderer renderer,
            GraphicsAttachmentProvider viewportGraphicsProvider) {
        if (fdx == null) {
            throw new IllegalArgumentException("Fdx cannot be null");
        }
        this.fdx = fdx;
        this.imgui = FdxImGui.create(fdx, renderer, viewportGraphicsProvider);
    }

    @Override
    public void beginFrame() {
        imgui.beginFrame();
    }

    @Override
    public void render() {
        imgui.render();
    }

    @Override
    public void clearScreen(float red, float green, float blue, float alpha) {
        fdx.graphics().main().clear(red, green, blue, alpha);
    }

    @Override
    public ExampleTexture createTexture(String label, int width, int height, ByteBuffer pixels) {
        GraphicsContext graphics = fdx.graphics().main();
        Texture texture = graphics.device().createTexture(TextureDescriptor.rgba8(label, width, height));
        graphics.device().writeTexture(texture, pixels);
        long id = imgui.textures().register(texture);
        return new ExampleTexture(id, width, height, () -> {
            if (!imgui.textures().isDisposed()) {
                imgui.textures().remove(id);
            }
            if (!texture.isDisposed()) {
                texture.dispose();
            }
        });
    }

    @Override
    public int framesPerSecond() {
        float delta = fdx.app().deltaTime();
        if (delta <= 0.0f || !Float.isFinite(delta)) {
            return 0;
        }
        return Math.round(1.0f / delta);
    }

    @Override
    public void dispose() {
        if (!imgui.isDisposed()) {
            imgui.dispose();
        }
    }
}

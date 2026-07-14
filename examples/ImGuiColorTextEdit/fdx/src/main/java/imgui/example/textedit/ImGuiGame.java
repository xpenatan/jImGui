package imgui.example.textedit;

import imgui.example.renderer.ImGuiExampleApplication;
import imgui.example.renderer.ImGuiRenderer;
import imgui.extension.textedit.TextEditLoader;
import io.github.libfdx.core.FdxException;
import io.github.libfdx.graphics.GraphicsAttachmentProvider;
import io.github.libfdx.imgui.FdxImGuiRenderer;

public class ImGuiGame extends ImGuiExampleApplication {
    public ImGuiGame(FdxImGuiRenderer renderer) {
        super(renderer);
    }

    public ImGuiGame(FdxImGuiRenderer renderer, GraphicsAttachmentProvider viewportGraphicsProvider) {
        super(renderer, viewportGraphicsProvider);
    }

    @Override
    protected void loadExtensions(Runnable ready) {
        TextEditLoader.init((success, error) -> {
            if (error != null) {
                throw new FdxException("Could not load ImGuiColorTextEdit", error);
            }
            if (!success) {
                throw new FdxException("Could not load ImGuiColorTextEdit");
            }
            ready.run();
        });
    }

    @Override
    protected ImGuiRenderer createScreen() {
        return new TextEditExample();
    }
}

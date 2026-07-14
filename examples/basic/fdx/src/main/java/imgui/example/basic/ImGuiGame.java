package imgui.example.basic;

import imgui.example.renderer.ImGuiExampleApplication;
import imgui.example.renderer.ImGuiRenderer;
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
    protected ImGuiRenderer createScreen() {
        return new BasicExample();
    }
}

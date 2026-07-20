package imgui.example.nodeeditor;

import imgui.example.renderer.ImGuiExampleApplication;
import imgui.example.renderer.ImGuiRenderer;
import imgui.extension.nodeeditor.NodeEditorLoader;
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
        NodeEditorLoader.init((success, error) -> {
            if (error != null) {
                throw new FdxException("Could not load imgui-node-editor", error);
            }
            if (!success) {
                throw new FdxException("Could not load imgui-node-editor");
            }
            ready.run();
        });
    }

    @Override
    protected ImGuiRenderer createScreen() {
        return new NodeEditorExample();
    }
}

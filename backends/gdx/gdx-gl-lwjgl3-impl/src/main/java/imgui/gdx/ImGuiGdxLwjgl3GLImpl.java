package imgui.gdx;

import com.badlogic.gdx.files.FileHandle;
import imgui.ImGuiViewportPlatformCallbacks;

public final class ImGuiGdxLwjgl3GLImpl extends ImGuiGdxGLImpl {
    public ImGuiGdxLwjgl3GLImpl() {
        super();
    }

    public ImGuiGdxLwjgl3GLImpl(FileHandle imgui) {
        super(imgui);
    }

    @Override
    ImGuiViewportPlatformCallbacks createViewportPlatform() {
        return new ImGuiGdxLwjgl3ViewportPlatform(this);
    }
}

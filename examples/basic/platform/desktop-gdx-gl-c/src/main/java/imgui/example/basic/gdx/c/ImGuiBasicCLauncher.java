package imgui.example.basic.gdx.c;

import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWApplication;
import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWApplicationConfiguration;
import imgui.example.basic.BasicExample;
import imgui.example.renderer.ImGuiRenderer;
import imgui.example.renderer.gdx.GdxExampleOptions;
import imgui.example.renderer.gdx.GdxGlGraphicsAdapter;
import imgui.example.renderer.gdx.GdxImGuiExampleApplication;
import imgui.gdx.ImGuiGdxGLImpl;

public final class ImGuiBasicCLauncher {
    private ImGuiBasicCLauncher() {
    }

    public static void main(String[] args) {
        GdxExampleOptions.apply(args);
        GLFWApplicationConfiguration config = new GLFWApplicationConfiguration();
        config.setTitle("jImGui Basic - GDX OpenGL TeaVM C");
        config.setWindowedMode(1200, 760);
        config.useVsync(true);
        config.setForegroundFPS(60);

        new GLFWApplication(new GdxImGuiExampleApplication(ImGuiGdxGLImpl::new,
                new GdxGlGraphicsAdapter()) {
            @Override
            protected ImGuiRenderer createScreen() {
                return new BasicExample();
            }
        }, config);
    }
}

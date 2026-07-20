package imgui.example.imlayout.gdx.c;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWApplication;
import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWApplicationConfiguration;
import imgui.example.imlayout.ImLayoutExample;
import imgui.example.renderer.ImGuiRenderer;
import imgui.example.renderer.gdx.GdxExampleOptions;
import imgui.example.renderer.gdx.GdxGlGraphicsAdapter;
import imgui.example.renderer.gdx.GdxImGuiExampleApplication;
import imgui.extension.imlayout.ImLayoutLoader;
import imgui.gdx.ImGuiGdxGLImpl;

public final class ImLayoutCLauncher {
    private ImLayoutCLauncher() {
    }

    public static void main(String[] args) {
        GdxExampleOptions.apply(args);
        GLFWApplicationConfiguration config = new GLFWApplicationConfiguration();
        config.setTitle("jImGui ImLayout - GDX OpenGL TeaVM C");
        config.setWindowedMode(1200, 760);
        config.useVsync(true);
        config.setForegroundFPS(60);

        new GLFWApplication(new GdxImGuiExampleApplication(ImGuiGdxGLImpl::new,
                new GdxGlGraphicsAdapter()) {
            @Override
            protected void loadExtensions(Runnable ready) {
                ImLayoutLoader.init((success, error) -> {
                    if(error != null) {
                        throw new GdxRuntimeException("Could not load ImLayout", error);
                    }
                    if(!success) {
                        throw new GdxRuntimeException("Could not load ImLayout");
                    }
                    ready.run();
                });
            }

            @Override
            protected ImGuiRenderer createScreen() {
                return new ImLayoutExample();
            }
        }, config);
    }
}

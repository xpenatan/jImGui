package imgui.example.basic;

import io.github.libfdx.backend.web.WebApplicationBackend;
import io.github.libfdx.backend.web.WebApplicationConfig;
import io.github.libfdx.graphics.gl.web.WebGLProvider;
import io.github.libfdx.imgui.FdxImGuiRenderers;

public final class WebGlLauncher {

    private WebGlLauncher() {
    }

    public static void main(String[] args) {
        WebGLProvider graphicsProvider = new WebGLProvider();
        WebApplicationConfig config = new WebApplicationConfig()
                .canvasId("canvas")
                .title("ImGui FDX WebGL Basic Example")
                .size(0, 0)
                .graphics(graphicsProvider);
        new WebApplicationBackend().start(config, new ImGuiGame(FdxImGuiRenderers.auto(), graphicsProvider));
    }
}

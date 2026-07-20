package imgui.example.basic;

import io.github.libfdx.backend.web.WebApplicationBackend;
import io.github.libfdx.backend.web.WebApplicationConfig;
import io.github.libfdx.graphics.wgpu.WebWGPUProvider;
import io.github.libfdx.imgui.FdxImGuiRenderers;

public final class WebWgpuLauncher {

    private WebWgpuLauncher() {
    }

    public static void main(String[] args) {
        WebWGPUProvider graphicsProvider = new WebWGPUProvider();
        WebApplicationConfig config = new WebApplicationConfig()
                .canvasId("canvas")
                .title("ImGui FDX WebGPU Basic Example")
                .size(0, 0)
                .graphics(graphicsProvider);
        new WebApplicationBackend().start(config, new ImGuiGame(FdxImGuiRenderers.auto(), graphicsProvider));
    }
}

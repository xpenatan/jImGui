package imgui.example.imlayout;

import io.github.libfdx.backend.desktop.DesktopApplicationBackend;
import io.github.libfdx.backend.desktop.DesktopApplicationConfig;
import io.github.libfdx.backend.desktop.DesktopOpenGLProvider;
import io.github.libfdx.imgui.FdxImGuiRenderers;

public class Main {
    public static void main(String[] args) {
        DesktopOpenGLProvider graphicsProvider = new DesktopOpenGLProvider();
        DesktopApplicationConfig config = new DesktopApplicationConfig()
                .size(1444, 800)
                .title("ImLayout FDX GL Example")
                .vSync(true)
                .graphics(graphicsProvider);
        new DesktopApplicationBackend().start(config, new ImGuiGame(FdxImGuiRenderers.auto(), graphicsProvider));
    }
}

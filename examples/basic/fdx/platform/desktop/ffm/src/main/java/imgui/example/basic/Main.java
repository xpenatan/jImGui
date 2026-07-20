package imgui.example.basic;

import io.github.libfdx.backend.desktop.DesktopApplicationBackend;
import io.github.libfdx.backend.desktop.DesktopApplicationConfig;
import io.github.libfdx.backend.desktop.DesktopOpenGLProvider;
import io.github.libfdx.backend.desktop.DesktopVulkanProvider;
import io.github.libfdx.graphics.GraphicsAttachmentProvider;
import io.github.libfdx.graphics.wgpu.WGPUProvider;
import io.github.libfdx.imgui.FdxImGuiRenderers;
import imgui.example.renderer.FdxExampleCapture;
import imgui.example.renderer.ImGuiExampleApplication;
import imgui.example.renderer.ImGuiRenderer;

public class Main {
    public static void main(String[] args) {
        String graphics = graphicsName(args);
        GraphicsAttachmentProvider graphicsProvider = graphicsProvider(graphics);
        FdxExampleCapture capture = new FdxExampleCapture();
        DesktopApplicationConfig config = new DesktopApplicationConfig()
                .size(1444, 800)
                .title("ImGui FDX " + graphicsDisplayName(graphics) + " FFM Basic Example")
                .vSync(false)
                .graphics(graphicsProvider);
        new DesktopApplicationBackend().start(config,
                new ImGuiExampleApplication(FdxImGuiRenderers.auto(), graphicsProvider) {
                    @Override
                    protected ImGuiRenderer createScreen() {
                        return new BasicExample();
                    }

                    @Override
                    public void onFrameEnd() {
                        capture.onFrameEnd(fdx());
                    }
                });
    }

    private static GraphicsAttachmentProvider graphicsProvider(String graphics) {
        if ("wgpu".equals(graphics)) {
            return new WGPUProvider().vSync(false);
        }
        if ("gl".equals(graphics) || "opengl".equals(graphics)) {
            return new DesktopOpenGLProvider();
        }
        if ("vulkan".equals(graphics) || "vk".equals(graphics)) {
            return new DesktopVulkanProvider().vSync(false);
        }
        throw new IllegalArgumentException(
                "Unsupported graphics API '" + graphics + "'. Expected wgpu, gl, or vulkan.");
    }

    private static String graphicsName(String[] args) {
        if (args != null) {
            for (String arg : args) {
                if (arg != null && arg.startsWith("--graphics=")) {
                    return arg.substring("--graphics=".length()).trim().toLowerCase();
                }
            }
        }
        return "wgpu";
    }

    private static String graphicsDisplayName(String graphics) {
        if ("gl".equals(graphics) || "opengl".equals(graphics)) {
            return "GL";
        }
        if ("vulkan".equals(graphics) || "vk".equals(graphics)) {
            return "Vulkan";
        }
        return "WGPU";
    }
}

package imgui.example.basic;

import io.github.libfdx.backend.web.WebBuilder;
import org.teavm.tooling.ConsoleTeaVMToolLog;

import java.nio.file.Path;

public final class Build {

    private Build() {
    }

    public static void main(String[] args) {
        boolean useWebGl = "gl".equalsIgnoreCase(System.getProperty("imgui.graphics", "wgpu"));
        Class<?> launcherClass = useWebGl ? WebGlLauncher.class : WebWgpuLauncher.class;
        String graphicsName = useWebGl ? "WebGL" : "WebGPU";
        WebBuilder.wasm()
                .classpathFromCurrentJvm()
                .webappDirectory(Path.of("build/dist"))
                .cacheDirectory(Path.of("build/teavm-cache"))
                .asset(Path.of("../../../../assets"))
                .mainClass(launcherClass.getName())
                .title("ImGui FDX " + graphicsName + " Basic Example")
                .canvasId("canvas")
                .fillWindow()
                .obfuscated(true)
                .log(new ConsoleTeaVMToolLog(false))
                .build();
    }
}

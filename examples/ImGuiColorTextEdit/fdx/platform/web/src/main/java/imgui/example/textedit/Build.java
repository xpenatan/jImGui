package imgui.example.textedit;

import io.github.libfdx.backend.web.WebBuilder;
import org.teavm.tooling.ConsoleTeaVMToolLog;

import java.nio.file.Path;

public class Build {

    public static void main(String[] args) {
        WebBuilder.wasm()
                .classpathFromCurrentJvm()
                .webappDirectory(Path.of("build/dist"))
                .cacheDirectory(Path.of("build/teavm-cache"))
                .asset(Path.of("../../../../assets"))
                .mainClass(Launcher.class.getName())
                .title("ImGuiColorTextEdit WebGL Example")
                .canvasId("canvas")
                .fillWindow()
                .obfuscated(true)
                .log(new ConsoleTeaVMToolLog(false))
                .build();
    }
}

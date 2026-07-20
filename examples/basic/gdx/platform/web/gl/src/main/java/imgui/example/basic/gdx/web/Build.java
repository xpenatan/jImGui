package imgui.example.basic.gdx.web;

import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.builder.TeaBuilder;
import com.github.xpenatan.gdx.teavm.backends.web.config.backend.WebBackend;
import java.io.File;
import org.teavm.vm.TeaVMOptimizationLevel;

public final class Build {
    private Build() {
    }

    public static void main(String[] args) {
        new TeaBuilder(new WebBackend()
                .setStartJettyAfterBuild(false)
                .setWebAssembly(true))
                .addAssets(new AssetFileHandle("../../../../../assets"))
                .setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE)
                .setMainClass(Launcher.class.getName())
                .setObfuscated(true)
                .build(new File("build/dist"));
    }
}

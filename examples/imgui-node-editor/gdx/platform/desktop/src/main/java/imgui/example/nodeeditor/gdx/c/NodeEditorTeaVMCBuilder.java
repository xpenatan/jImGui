package imgui.example.nodeeditor.gdx.c;

import com.github.xpenatan.gdx.teavm.backends.glfw.config.backend.TeaGLFWBackend;
import com.github.xpenatan.gdx.teavm.backends.shared.config.builder.TeaBuilder;
import java.io.File;
import java.io.IOException;
import org.teavm.vm.TeaVMOptimizationLevel;

public final class NodeEditorTeaVMCBuilder {
    private static final int MIN_HEAP_SIZE = 64 * 1024 * 1024;
    private static final int MAX_HEAP_SIZE = 512 * 1024 * 1024;
    private static final int MIN_DIRECT_BUFFER_SIZE = 64 * 1024 * 1024;

    private NodeEditorTeaVMCBuilder() {
    }

    public static void main(String[] args) throws IOException {
        TeaGLFWBackend.NativeBuildType buildType = args.length > 0
                ? TeaGLFWBackend.NativeBuildType.fromString(args[0])
                : TeaGLFWBackend.NativeBuildType.DEBUG;
        String action = args.length > 1 ? args[1].trim().toLowerCase() : "generate";
        boolean buildExecutable = action.equals("build") || action.equals("run");
        boolean runExecutable = action.equals("run");
        if(!action.equals("generate") && !buildExecutable) {
            throw new IllegalArgumentException("Expected action: generate, build, or run");
        }

        TeaGLFWBackend backend = new TeaGLFWBackend()
                .setBuildType(buildType)
                .setBuildExecutableAfterBuild(buildExecutable)
                .setRunExecutableAfterBuild(runExecutable)
                .setRunExecutableWithConsoleLog(runExecutable);
        backend.cmakeDefinition("JPARSER_TEAVMC_LINKAGE", "STATIC");

        new TeaBuilder(backend)
                .setOutputName("jimgui-nodeeditor")
                .setObfuscated(false)
                .setOptimizationLevel(TeaVMOptimizationLevel.FULL)
                .setMinHeapSize(MIN_HEAP_SIZE)
                .setMaxHeapSize(MAX_HEAP_SIZE)
                .setMinDirectBuffersSize(MIN_DIRECT_BUFFER_SIZE)
                .setMainClass(NodeEditorCLauncher.class.getName())
                .build(new File("build/dist/glfw"));
    }
}

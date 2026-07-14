package imgui;

import com.github.xpenatan.jParser.builder.tool.BuildToolOptions;
import com.github.xpenatan.jParser.builder.tool.BuilderTool;
import com.github.xpenatan.jParser.builder.tool.DefaultBuildTargetConfig;
import com.github.xpenatan.jParser.builder.tool.DefaultBuildTargetFactory;
import com.github.xpenatan.jParser.idl.IDLReader;
import java.io.File;
import java.util.Locale;
import java.util.ArrayList;

public final class BuildImLayout {
    private static final String[] DESKTOP_TARGETS = {
            "windows64_jni", "linux64_jni", "mac64_jni", "macArm_jni",
            "windows64_ffm", "linux64_ffm", "mac64_ffm", "macArm_ffm",
            "windows64_teavm_c", "linux64_teavm_c", "mac64_teavm_c", "macArm_teavm_c"
    };

    private BuildImLayout() {
    }

    public static void main(String[] args) throws Exception {
        File projectDir = new File(".").getCanonicalFile();
        File moduleRoot = projectDir.getParentFile();
        File imguiRoot = new File(projectDir, "../../../imgui").getCanonicalFile();
        File imguiBuilder = new File(imguiRoot, "builder");
        File imguiNativeBuild = new File(imguiBuilder, "build/c++");
        File imguiSource = new File(imguiRoot, "download/build/imgui-source");
        File imguiCustom = new File(imguiBuilder, "src/main/cpp/custom");
        File sourceDir = new File(projectDir, "src/main/cpp/source");

        BuildToolOptions.BuildToolParams params = params(moduleRoot, sourceDir);
        BuildToolOptions options = new BuildToolOptions(params, args);
        options.addAdditionalIDLRefPath(IDLReader.parseFile(
                new File(imguiBuilder, "src/main/cpp/imgui.idl").getAbsolutePath()));
        options.addAdditionalIDLRefPath(IDLReader.getRuntimeHelperFile());

        DefaultBuildTargetConfig config = DefaultBuildTargetConfig.fromBuildToolOptions(options);
        config.webForcedInclude = path(new File(projectDir, "src/main/cpp/custom/ImLayoutWebIncludes.h"));
        config.globalHooks.headerDirs.add(path(imguiSource));
        config.globalHooks.headerDirs.add(path(imguiCustom));
        config.globalHooks.cppIncludes.add(path(sourceDir) + "/*.cpp");
        configureTargets(config, imguiNativeBuild);

        DefaultBuildTargetFactory factory = new DefaultBuildTargetFactory();
        BuilderTool.build(options, (op, idlReader, targets) ->
                factory.addTargets(op, idlReader, targets, config));
    }

    private static BuildToolOptions.BuildToolParams params(File moduleRoot, File sourceDir) {
        BuildToolOptions.BuildToolParams params = new BuildToolOptions.BuildToolParams();
        params.libName = "imlayout";
        params.idlName = "imlayout";
        params.webModuleName = "imlayout";
        params.packageName = "imgui.extension.imlayout";
        params.cppSourcePath = path(sourceDir);
        params.modulePath = path(moduleRoot);
        params.modulePrefix = "imlayout";
        params.moduleCSuffix = "c";
        params.jniCppStandard = "c++17";
        params.ffmCppStandard = "c++17";
        params.webCppStandard = "c++17";
        params.teaVMCCppStandard = "c++17";
        return params;
    }

    private static void configureTargets(DefaultBuildTargetConfig config, File imguiNativeBuild) {
        for(String target : DESKTOP_TARGETS) {
            DefaultBuildTargetConfig.TargetHooks hooks = config.target(target);
            hooks.includeDefaultSources = false;
            hooks.includeCustomSources = true;
            hooks.compileFlags.add(imguiConfigFlag(target));
            addImguiLibrary(hooks, target, imguiNativeBuild);
            addPlatformLinkFlags(hooks, target);
        }
        DefaultBuildTargetConfig.TargetHooks web = config.target("web_wasm");
        web.includeDefaultSources = false;
        web.includeCustomSources = true;
        web.compileFlags.add(imguiConfigFlag("web_wasm"));
        web.linkerFlags.add("-lc++abi");
        web.linkerFlags.add("-lc++");
        web.linkerFlags.add("-lc");
    }

    private static void addImguiLibrary(DefaultBuildTargetConfig.TargetHooks hooks,
            String target, File nativeBuild) {
        String api = api(target);
        if(target.startsWith("windows64")) {
            hooks.staticLinkerInputs.add(path(new File(nativeBuild, "libs/windows/vc/" + api + "/imgui64.lib")));
        }
        else if(target.startsWith("linux64")) {
            hooks.sharedLinkerInputs.add(path(new File(nativeBuild, "libs/linux/" + api + "/libimgui64.so")));
        }
        else if(target.startsWith("macArm")) {
            hooks.sharedLinkerInputs.add(path(new File(nativeBuild, "libs/mac/arm/" + api + "/libimguiarm64.dylib")));
        }
        else if(target.startsWith("mac64")) {
            hooks.sharedLinkerInputs.add(path(new File(nativeBuild, "libs/mac/" + api + "/libimgui64.dylib")));
        }
    }

    private static void addPlatformLinkFlags(DefaultBuildTargetConfig.TargetHooks hooks, String target) {
        if(target.startsWith("linux64")) {
            hooks.linkerFlags.add("-Wl,-rpath,$ORIGIN");
        }
        else if(target.startsWith("mac")) {
            hooks.linkerFlags.add("-undefined");
            hooks.linkerFlags.add("dynamic_lookup");
        }
    }

    private static String imguiConfigFlag(String target) {
        if(target.startsWith("windows64")) {
            return "/DIMGUI_USER_CONFIG=\"\\\"ImGuiCustomConfig.h\\\"\"";
        }
        if(target.equals("web_wasm") && isWindowsHost()) {
            return "-DIMGUI_USER_CONFIG=\"\\\"ImGuiCustomConfig.h\\\"\"";
        }
        return "-DIMGUI_USER_CONFIG=\"ImGuiCustomConfig.h\"";
    }

    private static boolean isWindowsHost() {
        return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("windows");
    }

    private static String api(String target) {
        if(target.endsWith("_teavm_c")) {
            return "teavm_c";
        }
        return target.endsWith("_ffm") ? "ffm" : "jni";
    }

    private static String path(File file) {
        return file.getAbsolutePath().replace('\\', '/');
    }
}

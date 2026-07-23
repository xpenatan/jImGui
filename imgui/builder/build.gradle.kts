import com.github.xpenatan.jParser.builder.targets.AndroidTarget
import com.github.xpenatan.jParser.gradle.JParserTargets
import com.github.xpenatan.jParser.gradle.JParserTargetHooks
import com.github.xpenatan.jParser.idl.IDLClassOrEnum
import com.github.xpenatan.jParser.idl.IDLRenaming
import org.gradle.api.provider.ListProperty
import java.io.File

plugins {
    id("java-library")
    alias(libs.plugins.jParserPlugin)
}

fun File.normalizedPath(): String {
    return absolutePath.replace('\\', '/')
}

val downloadBuildDir = file("../download/build")
val imguiSourceRoot = File(downloadBuildDir, "imgui-source")
val imguiSourcePattern = "${imguiSourceRoot.normalizedPath()}/*.cpp"
val imguiSourceExcludes = listOf(
    "${imguiSourceRoot.normalizedPath()}/backends/*.cpp",
    "${imguiSourceRoot.normalizedPath()}/examples/**/*.cpp",
    "${imguiSourceRoot.normalizedPath()}/misc/**/*.cpp"
)
val imguiCustomSourceDir = file("src/main/cpp/custom")
val imguiCustomHeader = file("src/main/cpp/custom/ImGuiCustom.h")
val isWindowsHost = System.getProperty("os.name").startsWith("Windows", ignoreCase = true)
val imguiNativeUserConfigFlag = if(isWindowsHost) {
    "-DIMGUI_USER_CONFIG=\"\\\"ImGuiCustomConfig.h\\\"\""
}
else {
    "-DIMGUI_USER_CONFIG=\"ImGuiCustomConfig.h\""
}
val imguiMsvcUserConfigFlag = "/DIMGUI_USER_CONFIG=\"\\\"ImGuiCustomConfig.h\\\"\""
val enableNativeTestHooks = gradle.startParameter.taskNames.any {
    it == "test" || it.endsWith(":imgui:shared:jni:test")
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
}

fun ListProperty<String>.imguiUserConfigFlag(msvc: Boolean) {
    add(if(msvc) imguiMsvcUserConfigFlag else imguiNativeUserConfigFlag)
}

fun JParserTargetHooks.configureDesktopTarget(targetName: String) {
    val windows = targetName.startsWith("windows64")
    compileFlags.imguiUserConfigFlag(windows)
    if(windows) {
        compileFlag("/DIMGUI_EXPORTS")
        includeCustomSources.set(true)
    }
    else if(targetName.startsWith("linux64")) {
        linkerFlag("-Wl,-soname,libimgui64.so")
    }
}

val desktopTargets = listOf(
    JParserTargets.WINDOWS64_JNI,
    JParserTargets.LINUX64_JNI,
    JParserTargets.MAC64_JNI,
    JParserTargets.MAC_ARM_JNI,
    JParserTargets.WINDOWS64_FFM,
    JParserTargets.LINUX64_FFM,
    JParserTargets.MAC64_FFM,
    JParserTargets.MAC_ARM_FFM,
    JParserTargets.WINDOWS64_TEAVM_C,
    JParserTargets.LINUX64_TEAVM_C,
    JParserTargets.MAC64_TEAVM_C,
    JParserTargets.MAC_ARM_TEAVM_C
)

jParser {
    libName.set("imgui")
    modulePrefix.set("")
    modulePath(file(".."))
    moduleBuildSuffix.set("builder")
    moduleBaseSuffix.set("base")
    moduleCoreSuffix.set("core")
    moduleJNISuffix.set("shared/jni")
    moduleFFMSuffix.set("desktop/ffm")
    moduleWebSuffix.set("web/wasm")
    moduleCSuffix.set("shared/c")
    packageName.set("imgui")
    cppSourcePath(imguiSourceRoot)
    jniCppStandard.set("c++17")
    ffmCppStandard.set("c++17")
    webCppStandard.set("c++17")
    teaVMCCppStandard.set("c++17")
    webSideModule.set(1)
    webForcedInclude(imguiCustomHeader)
    ffmLogMethod.set(true)

    native {
        dependsOn(":imgui:download:imgui_download_source")
        headerDir(imguiSourceRoot)
        headerDir(imguiCustomSourceDir)
        cppInclude(imguiSourcePattern)
        imguiSourceExcludes.forEach(::cppExclude)
        includeDefaultSources.set(false)
        includeCustomSources.set(false)

        desktopTargets.forEach { targetName ->
            target(targetName) {
                configureDesktopTarget(targetName.targetName)
                if(enableNativeTestHooks && targetName.targetName.endsWith("_jni")) {
                    compileFlag(if(targetName.targetName.startsWith("windows64"))
                        "/DJIMGUI_ENABLE_TEST_HOOKS"
                    else
                        "-DJIMGUI_ENABLE_TEST_HOOKS")
                }
            }
        }

        target(JParserTargets.WEB_WASM) {
            compileFlag(imguiNativeUserConfigFlag)
            compileFlag("-DIMGUI_DISABLE_FILE_FUNCTIONS")
            compileFlag("-DIMGUI_DEFINE_MATH_OPERATORS")
            linkerFlag("-lc++abi")
            linkerFlag("-lc++")
            linkerFlag("-lc")
        }

        target(JParserTargets.ANDROID_JNI) {
            compileFlag("-Wno-error=format-security")
            compileFlag("-DIMGUI_DISABLE_FILE_FUNCTIONS")
            compileFlag("-DIMGUI_DEFINE_MATH_OPERATORS")
            linkerFlag("-Wl,-z,max-page-size=16384")
            androidTarget(AndroidTarget.Target.x86) {}
            androidTarget(AndroidTarget.Target.x86_64) {}
            androidTarget(AndroidTarget.Target.armeabi_v7a) {}
            androidTarget(AndroidTarget.Target.arm64_v8a) {}
        }

        target(JParserTargets.ANDROID_TEAVM_C) {
            compileFlag("-Wno-error=format-security")
            compileFlag("-DIMGUI_DISABLE_FILE_FUNCTIONS")
            compileFlag("-DIMGUI_DEFINE_MATH_OPERATORS")
            linkerFlag("-Wl,-z,max-page-size=16384")
            androidTarget(AndroidTarget.Target.x86) {}
            androidTarget(AndroidTarget.Target.x86_64) {}
            androidTarget(AndroidTarget.Target.armeabi_v7a) {}
            androidTarget(AndroidTarget.Target.arm64_v8a) {}
        }
    }

    idlRenaming(object : IDLRenaming {
        override fun obtainNewPackage(idlClassOrEnum: IDLClassOrEnum, classPackage: String): String {
            if(idlClassOrEnum.isEnum) {
                return "enums"
            }
            return classPackage
        }

        override fun getIDLEnumName(enumName: String): String {
            var newName: String? = null
            if(enumName.contains("_")) {
                val values = enumName.split("_", limit = 2)
                newName = values[1]
                if(newName in setOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")) {
                    newName = "Num_$newName"
                }
                if(enumName.startsWith("ImGuiMod")) {
                    newName = null
                }
            }
            return newName ?: enumName
        }
    })
}

tasks.matching { it.name.startsWith("jParser_build_") }.configureEach {
    mustRunAfter("jParser_generate")
}

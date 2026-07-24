import com.github.xpenatan.jParser.idl.IDLClassOrEnum
import com.github.xpenatan.jParser.idl.IDLRenaming
import com.github.xpenatan.jParser.gradle.JParserTargets
import java.io.File

plugins {
    id("java-library")
    alias(libs.plugins.jParserPlugin)
}

val downloadBuildDir = file("../download/build")
val imguiSourceRoot = File(downloadBuildDir, "imgui-source")
val imguiSourcePath = imguiSourceRoot.invariantSeparatorsPath
val imguiSourcePattern = "$imguiSourcePath/*.cpp"
val imguiSourceExcludes = listOf(
    "$imguiSourcePath/backends/*.cpp",
    "$imguiSourcePath/examples/**/*.cpp",
    "$imguiSourcePath/misc/**/*.cpp"
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
val enableNativeTestHooks = gradle.startParameter.taskNames.any {
    it == "test" || it.endsWith(":imgui:shared:jni:test")
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
}

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
                val windows = name.startsWith("windows64")
                compileFlag(if(windows) imguiMsvcUserConfigFlag else imguiNativeUserConfigFlag)
                if(windows) {
                    compileFlag("/DIMGUI_EXPORTS")
                    includeCustomSources.set(true)
                }
                else if(name.startsWith("linux64")) {
                    linkerFlag("-Wl,-soname,libimgui64.so")
                }
                if(enableNativeTestHooks && name.endsWith("_jni")) {
                    compileFlag(if(windows) "/DJIMGUI_ENABLE_TEST_HOOKS" else "-DJIMGUI_ENABLE_TEST_HOOKS")
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
        }

        target(JParserTargets.ANDROID_TEAVM_C) {
            compileFlag("-Wno-error=format-security")
            compileFlag("-DIMGUI_DISABLE_FILE_FUNCTIONS")
            compileFlag("-DIMGUI_DEFINE_MATH_OPERATORS")
            linkerFlag("-Wl,-z,max-page-size=16384")
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

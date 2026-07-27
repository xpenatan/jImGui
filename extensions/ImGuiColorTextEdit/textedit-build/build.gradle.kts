import com.github.xpenatan.jParser.gradle.JParserTargetHooks
import com.github.xpenatan.jParser.gradle.JParserTargets
import de.undercouch.gradle.tasks.download.Download
import java.io.File

plugins {
    id("java-library")
    alias(libs.plugins.downloadPlugin)
    alias(libs.plugins.jParserPlugin)
}

val buildDir = layout.buildDirectory.get().asFile
val zippedPath = "${buildDir}/text-edit.zip"
val sourcePath = "${buildDir}/text-edit/"
val sourceDestination = "${buildDir}/ImGuiColorTextEdit/"
val textEditCommit = libs.versions.textEditSourceCommit.get()

tasks.register<Download>("download_textedit_source") {
    group = "textedit"
    description = "Download source"
    src("https://github.com/goossens/ImGuiColorTextEdit/archive/$textEditCommit.zip")
    dest(File(zippedPath))
    doLast {
        copy {
            from(zipTree(dest))
            into(sourcePath)
        }
        delete(sourceDestination)
        copy {
            from("$sourcePath/ImGuiColorTextEdit-$textEditCommit")
            into(sourceDestination)
        }
        delete(sourcePath)
        delete(zippedPath)
    }
}

tasks.register("download_source") {
    group = "textedit"
    description = "Download source"
    dependsOn("download_textedit_source")
}

val textEditSourceDir = file(sourceDestination)
val isWindowsHost = System.getProperty("os.name").startsWith("Windows", ignoreCase = true)
val imguiNativeUserConfigFlag = if(isWindowsHost) {
    "-DIMGUI_USER_CONFIG=\"\\\"ImGuiCustomConfig.h\\\"\""
}
else {
    "-DIMGUI_USER_CONFIG=\"ImGuiCustomConfig.h\""
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

fun JParserTargetHooks.configureDesktopTarget(targetName: String) {
    if(targetName.startsWith("windows64")) {
        compileFlag("/EHsc")
    }
    else if(targetName.startsWith("linux64")) {
        linkerFlag("-Wl,-rpath,\$ORIGIN")
    }
    else if(targetName.startsWith("mac")) {
        compileFlag(imguiNativeUserConfigFlag)
        linkerFlag("-undefined")
        linkerFlag("dynamic_lookup")
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
}

jParser {
    libName.set("textedit")
    idlName.set("ColorTextEdit")
    modulePrefix.set("textedit")
    modulePath(file(".."))
    moduleCSuffix.set("c")
    packageName.set("imgui.extension.textedit")
    cppSourcePath(textEditSourceDir)
    jniCppStandard.set("c++20")
    ffmCppStandard.set("c++20")
    webCppStandard.set("c++20")
    teaVMCCppStandard.set("c++20")
    webForcedInclude(file("src/main/cpp/custom/TextEditWebIncludes.h"))

    dependency("imgui") {
        referenceProject(":imgui:builder")
    }

    native {
        dependsOn("download_source")
        headerDir(textEditSourceDir)
        cppInclude("${textEditSourceDir.invariantSeparatorsPath}/TextEditor.cpp")
        cppInclude("${textEditSourceDir.invariantSeparatorsPath}/TextDiff.cpp")
        includeDefaultSources.set(false)
        includeCustomSources.set(true)

        desktopTargets.forEach { targetName ->
            target(targetName) {
                configureDesktopTarget(targetName.targetName)
            }
        }

        target(JParserTargets.WEB_WASM) {
            compileFlag(imguiNativeUserConfigFlag)
            linkerFlag("-lc++abi")
            linkerFlag("-lc++")
            linkerFlag("-lc")
        }
    }
}

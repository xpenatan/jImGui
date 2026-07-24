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
val zippedVendorPath = "${buildDir}/regex.zip"
val sourceVendorPath = "${buildDir}/regex/"
val sourceVendorDestination = "${buildDir}/ImGuiColorTextEdit/vendor/regex"
val textEditCommit = libs.versions.textEditSourceCommit.get()
val boostRegexCommit = libs.versions.boostRegexSourceCommit.get()

tasks.register<Download>("download_textedit_source") {
    group = "textedit"
    description = "Download source"
    src("https://github.com/santaclose/ImGuiColorTextEdit/archive/$textEditCommit.zip")
    dest(File(zippedPath))
    doLast {
        copy {
            from(zipTree(dest))
            into(sourcePath)
        }
        copy {
            from("$sourcePath/ImGuiColorTextEdit-$textEditCommit")
            into(sourceDestination)
        }
        delete(sourcePath)
        delete(zippedPath)
    }
}

tasks.register<Download>("download_vendor_source") {
    group = "textedit"
    description = "Download source"
    src("https://github.com/boostorg/regex/archive/$boostRegexCommit.zip")
    dest(File(zippedVendorPath))
    doLast {
        copy {
            from(zipTree(dest))
            into(sourceVendorPath)
        }
        copy {
            from("$sourceVendorPath/regex-$boostRegexCommit/")
            into(sourceVendorDestination)
        }
        delete(sourceVendorPath)
        delete(zippedVendorPath)
    }
}

tasks.register("download_source") {
    group = "textedit"
    description = "Download source"
    dependsOn("download_textedit_source", "download_vendor_source")
    tasks.findByName("download_vendor_source")?.mustRunAfter("download_textedit_source")
}

val textEditSourceDir = file(sourceDestination)
val regexIncludeDir = File(textEditSourceDir, "vendor/regex/include")
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
    if(targetName.startsWith("linux64")) {
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
    jniCppStandard.set("c++17")
    ffmCppStandard.set("c++17")
    webCppStandard.set("c++17")
    teaVMCCppStandard.set("c++17")
    webForcedInclude(file("src/main/cpp/custom/TextEditWebIncludes.h"))

    dependency("imgui") {
        referenceProject(":imgui:builder")
    }

    native {
        dependsOn("download_source")
        headerDir(textEditSourceDir)
        headerDir(regexIncludeDir)
        cppInclude("${textEditSourceDir.invariantSeparatorsPath}/ImGuiDebugPanel.cpp")
        cppInclude("${textEditSourceDir.invariantSeparatorsPath}/LanguageDefinitions.cpp")
        cppInclude("${textEditSourceDir.invariantSeparatorsPath}/TextEditor.cpp")
        cppInclude("${textEditSourceDir.invariantSeparatorsPath}/UnitTests.cpp")
        cppInclude("${textEditSourceDir.invariantSeparatorsPath}/vendor/regex/src/*.cpp")
        cppExclude("${textEditSourceDir.invariantSeparatorsPath}/vendor/regex/build/**/*.cpp")
        cppExclude("${textEditSourceDir.invariantSeparatorsPath}/vendor/regex/example/**/*.cpp")
        cppExclude("${textEditSourceDir.invariantSeparatorsPath}/vendor/regex/performance/**/*.cpp")
        cppExclude("${textEditSourceDir.invariantSeparatorsPath}/vendor/regex/test/**/*.cpp")
        cppExclude("${textEditSourceDir.invariantSeparatorsPath}/vendor/regex/tools/**/*.cpp")
        compileFlag("-includecmath")
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

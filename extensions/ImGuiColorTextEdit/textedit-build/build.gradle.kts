import com.github.xpenatan.jParser.gradle.JParserBuildTask
import com.github.xpenatan.jParser.gradle.JParserTargetHooks
import com.github.xpenatan.jParser.gradle.JParserTargets
import de.undercouch.gradle.tasks.download.Download
import java.io.File

plugins {
    id("java-library")
    alias(libs.plugins.downloadPlugin)
    alias(libs.plugins.jParserPlugin)
}

fun File.normalizedPath(): String {
    return absolutePath.replace('\\', '/')
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

val imguiRoot = file("../../../imgui")
val imguiBuilderDir = File(imguiRoot, "builder")
val imguiIDL = File(imguiBuilderDir, "src/main/cpp/imgui.idl")
val imguiCustomSourceDir = File(imguiBuilderDir, "src/main/cpp/custom")
val imguiNativeBuildDir = File(imguiBuilderDir, "build/c++")
val imguiSourceRoot = File(imguiRoot, "download/build/imgui-source")
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

fun JParserTargetHooks.linkImgui(targetName: String) {
    val api = if(targetName.endsWith("_teavm_c")) {
        "teavm_c"
    }
    else if(targetName.endsWith("_ffm")) {
        "ffm"
    }
    else {
        "jni"
    }

    if(targetName.startsWith("windows64")) {
        staticLinkerInput(File(imguiNativeBuildDir, "libs/windows/vc/$api/imgui64.lib"))
    }
    else if(targetName.startsWith("linux64")) {
        sharedLinkerInput(File(imguiNativeBuildDir, "libs/linux/$api/libimgui64.so"))
    }
    else if(targetName.startsWith("macArm")) {
        sharedLinkerInput(File(imguiNativeBuildDir, "libs/mac/arm/$api/libimguiarm64.dylib"))
    }
    else if(targetName.startsWith("mac64")) {
        sharedLinkerInput(File(imguiNativeBuildDir, "libs/mac/$api/libimgui64.dylib"))
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
}

// Let jParser discover the real packages of referenced ImGui types, including imgui.enums.
val jParserReferenceClasspath = configurations.create("jParserReferenceClasspath") {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    add(jParserReferenceClasspath.name, project(":imgui:core"))
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
        idlRefPath(imguiIDL)
        referenceProjectPath.set(":imgui:builder")
        native {
            headerDir(imguiSourceRoot)
            headerDir(imguiCustomSourceDir)
            desktopTargets.forEach { targetName ->
                target(targetName) {
                    linkImgui(targetName.targetName)
                }
            }
        }
    }

    native {
        dependsOn("download_source")
        headerDir(textEditSourceDir)
        headerDir(regexIncludeDir)
        cppInclude("${textEditSourceDir.normalizedPath()}/ImGuiDebugPanel.cpp")
        cppInclude("${textEditSourceDir.normalizedPath()}/LanguageDefinitions.cpp")
        cppInclude("${textEditSourceDir.normalizedPath()}/TextEditor.cpp")
        cppInclude("${textEditSourceDir.normalizedPath()}/UnitTests.cpp")
        cppInclude("${textEditSourceDir.normalizedPath()}/vendor/regex/src/*.cpp")
        cppExclude("${textEditSourceDir.normalizedPath()}/vendor/regex/build/**/*.cpp")
        cppExclude("${textEditSourceDir.normalizedPath()}/vendor/regex/example/**/*.cpp")
        cppExclude("${textEditSourceDir.normalizedPath()}/vendor/regex/performance/**/*.cpp")
        cppExclude("${textEditSourceDir.normalizedPath()}/vendor/regex/test/**/*.cpp")
        cppExclude("${textEditSourceDir.normalizedPath()}/vendor/regex/tools/**/*.cpp")
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

tasks.matching { it.name.startsWith("jParser_build_") }.configureEach {
    mustRunAfter("jParser_generate")
}

tasks.withType<JParserBuildTask>().configureEach {
    dependsOn(jParserReferenceClasspath)
    doFirst {
        val classPath = System.getProperty("java.class.path", "")
        val classPathEntries = classPath.split(File.pathSeparator).toMutableSet()
        val referenceEntries = jParserReferenceClasspath.files
            .map(File::getAbsolutePath)
            .filterNot(classPathEntries::contains)
        if(referenceEntries.isNotEmpty()) {
            System.setProperty(
                "java.class.path",
                listOf(classPath, referenceEntries.joinToString(File.pathSeparator))
                    .filter(String::isNotEmpty)
                    .joinToString(File.pathSeparator)
            )
        }
    }
}

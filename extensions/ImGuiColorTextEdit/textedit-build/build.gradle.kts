import de.undercouch.gradle.tasks.download.Download
import org.gradle.kotlin.dsl.support.unzipTo
import java.io.File

plugins {
    id("java-library")
    id("de.undercouch.download") version("5.5.0")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
}

dependencies {
    implementation(project(":imgui:core"))
    implementation("com.github.xpenatan.jParser:gen-build-tool:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:gen-build:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:gen-idl:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:runtime-core:${LibExt.jParserVersion}")
}

val buildDir = layout.buildDirectory.get().asFile
val zippedPath = "${buildDir}/text-edit.zip"
val sourcePath = "${buildDir}/text-edit/"
val sourceDestination = "${buildDir}/ImGuiColorTextEdit/"
val zippedVendorPath = "${buildDir}/regex.zip"
val sourceVendorPath = "${buildDir}/regex/"
val sourceVendorDestination = "${buildDir}/ImGuiColorTextEdit/vendor/regex"

tasks.register<Download>("download_textedit_source") {
    group = "textedit"
    description = "Download source"
    src("https://github.com/santaclose/ImGuiColorTextEdit/archive/264bee49ddc3c789b05d928d09c628649458da47.zip")
    dest(File(zippedPath))
    doLast {
        unzipTo(File(sourcePath), dest)
        copy {
            from("$sourcePath/ImGuiColorTextEdit-264bee49ddc3c789b05d928d09c628649458da47")
            into(sourceDestination)
        }
        delete(sourcePath)
        delete(zippedPath)
    }
}

tasks.register<Download>("download_vendor_source") {
    group = "textedit"
    description = "Download source"
    src("https://github.com/boostorg/regex/archive/4cbcd3078e6ae10d05124379623a1bf03fcb9350.zip")
    dest(File(zippedVendorPath))
    doLast {
        unzipTo(File(sourceVendorPath), dest)
        copy {
            from("$sourceVendorPath/regex-4cbcd3078e6ae10d05124379623a1bf03fcb9350/")
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

val builderMainClass = "imgui.BuildTextEdit"

fun registerBuilderTask(name: String, args: List<String>, vararg taskDependencies: Any) =
    tasks.register<JavaExec>(name) {
        group = "jParser"
        mainClass.set(builderMainClass)
        classpath = sourceSets["main"].runtimeClasspath
        workingDir = projectDir
        this.args(args)
        dependsOn("classes", "download_source", *taskDependencies)
    }

registerBuilderTask(
    "jParser_generate",
    listOf("gen_jni", "gen_ffm", "gen_web", "gen_teavm_c"),
    ":imgui:builder:jParser_generate",
    ":imgui:download:imgui_download_source"
)

val nativeTargets = listOf(
    "windows64_jni" to "gen_jni",
    "linux64_jni" to "gen_jni",
    "mac64_jni" to "gen_jni",
    "macArm_jni" to "gen_jni",
    "windows64_ffm" to "gen_ffm",
    "linux64_ffm" to "gen_ffm",
    "mac64_ffm" to "gen_ffm",
    "macArm_ffm" to "gen_ffm",
    "windows64_teavm_c" to "gen_teavm_c",
    "linux64_teavm_c" to "gen_teavm_c",
    "mac64_teavm_c" to "gen_teavm_c",
    "macArm_teavm_c" to "gen_teavm_c"
)

nativeTargets.forEach { (target, generation) ->
    registerBuilderTask(
        "jParser_build_$target",
        listOf(generation, target),
        ":imgui:builder:jParser_build_$target",
        ":imgui:download:imgui_download_source"
    )
}

registerBuilderTask(
    "jParser_build_web_wasm",
    listOf("gen_web", "web_wasm"),
    ":imgui:builder:jParser_build_web_wasm",
    ":imgui:download:imgui_download_source"
)

tasks.matching { it.name.startsWith("jParser_build_") }.configureEach {
    mustRunAfter("jParser_generate")
}

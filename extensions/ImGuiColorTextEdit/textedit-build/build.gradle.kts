import de.undercouch.gradle.tasks.download.Download
import java.io.File

plugins {
    id("java-library")
    alias(libs.plugins.downloadPlugin)
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
}

dependencies {
    implementation(project(":imgui:core"))
    implementation(libs.bundles.jParserGeneratorArtifacts)
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

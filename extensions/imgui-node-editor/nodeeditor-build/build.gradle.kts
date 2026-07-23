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
val zippedPath = "${buildDir}/nodeeditor.zip"
val sourcePath = "${buildDir}/nodeeditor/"
val sourceDestination = "${buildDir}/imgui-node-editor/"
val nodeEditorCommit = libs.versions.nodeEditorSourceCommit.get()

tasks.register<Download>("download_source") {
    group = "node-editor"
    description = "Download source"
    src("https://github.com/NogginBops/imgui-node-editor/archive/$nodeEditorCommit.zip")
    dest(File(zippedPath))
    doLast {
        copy {
            from(zipTree(dest))
            into(sourcePath)
        }
        delete(sourceDestination)
        copy {
            from("$sourcePath/imgui-node-editor-$nodeEditorCommit")
            into(sourceDestination)
        }
        delete(sourcePath)
        delete(zippedPath)
    }
}

val builderMainClass = "imgui.BuildNodeEditor"

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

plugins {
    id("java")
}

val wasmLibraries by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
}

val mainClassName = "imgui.example.textedit.Build"

dependencies {
    implementation(project(":examples:shared"))
    implementation(project(":examples:ImGuiColorTextEdit:fdx:core"))

    if(providers.gradleProperty("useRepoLibs").map(String::toBoolean).getOrElse(false)) {
        implementation(libs.jImGuiImguiWeb)
        wasmLibraries(libs.jImGuiImguiWebWasm)
        implementation(libs.jImGuiTextEditWeb)
        wasmLibraries(libs.jImGuiTextEditWebWasm)
    }
    else {
        implementation(project(":imgui:web:wasm"))
        wasmLibraries(project(path = ":imgui:web:wasm", configuration = "wasmRuntimeElements"))
        implementation(project(":extensions:ImGuiColorTextEdit:textedit-web"))
        wasmLibraries(project(path = ":extensions:ImGuiColorTextEdit:textedit-web", configuration = "wasmRuntimeElements"))
    }
    implementation(libs.libFdxImguiExt)

    implementation(libs.libFdxBackendWeb)
    implementation(libs.libFdxGlWeb)
}

tasks.register<JavaExec>("textedit_web_run") {
    group = "example-teavm"
    description = "Build teavm example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath + wasmLibraries
    inputs.files(wasmLibraries)
    doLast {
        copy {
            from(wasmLibraries.map { zipTree(it) }) {
                include("*.js", "*.wasm")
            }
            into(layout.buildDirectory.dir("dist/scripts"))
        }
    }
}

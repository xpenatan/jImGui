plugins {
    id("java")
}

val wasmLibraries by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
}

val mainClassName = "imgui.example.textedit.Build"

dependencies {
    implementation(project(":examples:basic:base"))
    implementation(project(":examples:ImGuiColorTextEdit:fdx"))

    if(LibExt.useRepoLibs) {
        implementation("com.github.xpenatan.jImGui:imgui-web:-SNAPSHOT")
        wasmLibraries("com.github.xpenatan.jImGui:imgui-web_wasm:-SNAPSHOT")
        implementation("com.github.xpenatan.jImGui:textedit-web:-SNAPSHOT")
        wasmLibraries("com.github.xpenatan.jImGui:textedit-web_wasm:-SNAPSHOT")
        implementation("com.github.xpenatan.jImGui:fdx-impl:-SNAPSHOT")
    }
    else {
        implementation(project(":imgui:web:wasm"))
        wasmLibraries(project(path = ":imgui:web:wasm", configuration = "wasmRuntimeElements"))
        implementation(project(":extensions:ImGuiColorTextEdit:textedit-web"))
        wasmLibraries(project(path = ":extensions:ImGuiColorTextEdit:textedit-web", configuration = "wasmRuntimeElements"))
        implementation(project(":backends:fdx:fdx-impl"))
    }

    implementation("io.github.libfdx:backend_web:${LibExt.libFdxVersion}")
    implementation("io.github.libfdx:gl_web:${LibExt.libFdxVersion}")
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

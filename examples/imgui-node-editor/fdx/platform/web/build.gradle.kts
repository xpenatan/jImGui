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

val mainClassName = "imgui.example.nodeeditor.Build"

dependencies {
    implementation(project(":examples:shared"))
    implementation(project(":examples:imgui-node-editor:fdx:core"))

    if(LibExt.useRepoLibs) {
        implementation("com.github.xpenatan.jImGui:imgui-web:-SNAPSHOT")
        wasmLibraries("com.github.xpenatan.jImGui:imgui-web_wasm:-SNAPSHOT")
        implementation("com.github.xpenatan.jImGui:nodeeditor-web:-SNAPSHOT")
        wasmLibraries("com.github.xpenatan.jImGui:nodeeditor-web_wasm:-SNAPSHOT")
        implementation("com.github.xpenatan.jImGui:fdx-impl:-SNAPSHOT")
    }
    else {
        implementation(project(":imgui:web:wasm"))
        wasmLibraries(project(path = ":imgui:web:wasm", configuration = "wasmRuntimeElements"))
        implementation(project(":extensions:imgui-node-editor:nodeeditor-web"))
        wasmLibraries(project(path = ":extensions:imgui-node-editor:nodeeditor-web", configuration = "wasmRuntimeElements"))
        implementation(project(":backends:fdx:fdx-impl"))
    }

    implementation("io.github.libfdx:backend_web:${LibExt.libFdxVersion}")
    implementation("io.github.libfdx:gl_web:${LibExt.libFdxVersion}")
}

tasks.register<JavaExec>("nodeeditor_web_run") {
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

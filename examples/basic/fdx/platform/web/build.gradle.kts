plugins {
    id("java")
}

val wasmLibraries by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    implementation(project(":examples:basic:fdx:core"))

    if(rootProject.extra["examplesUseRepoLibs"] as Boolean) {
        implementation(libs.jImGuiImguiWeb)
        wasmLibraries(libs.jImGuiImguiWebWasm)
    }
    else {
        implementation(project(":imgui:web:wasm"))
        wasmLibraries(project(path = ":imgui:web:wasm", configuration = "wasmRuntimeElements"))
    }
    implementation(libs.libFdxImguiExt)

    implementation(libs.libFdxBackendWeb)
    implementation(libs.libFdxGlWeb)
    implementation(libs.libFdxWgpuWeb)
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
}

fun registerFdxWebTask(name: String, graphics: String? = null) {
    tasks.register<JavaExec>(name) {
        group = "example-teavm"
        description = if(graphics == "gl") {
            "Build the libfdx WebGL WASM basic example"
        }
        else {
            "Build the libfdx WebGPU WASM basic example (default)"
        }
        mainClass.set("imgui.example.basic.Build")
        classpath = sourceSets["main"].runtimeClasspath + wasmLibraries
        inputs.files(wasmLibraries)
        if(graphics != null) {
            systemProperty("imgui.graphics", graphics)
        }
        doLast {
            copy {
                from(wasmLibraries.map { zipTree(it) }) {
                    include("*.js", "*.wasm")
                }
                into(layout.buildDirectory.dir("dist/scripts"))
            }
        }
    }
}

registerFdxWebTask("imgui_basic_wasm_fdx_web_run")
registerFdxWebTask("imgui_basic_wasm_fdx_web_gl_run", "gl")

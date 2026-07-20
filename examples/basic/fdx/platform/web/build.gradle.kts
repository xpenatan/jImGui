plugins {
    id("java")
}

val wasmLibraries by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    implementation(project(":examples:basic:fdx:core"))

    if(LibExt.useRepoLibs) {
        implementation("com.github.xpenatan.jImGui:imgui-web:-SNAPSHOT")
        wasmLibraries("com.github.xpenatan.jImGui:imgui-web_wasm:-SNAPSHOT")
        implementation("com.github.xpenatan.jImGui:fdx-impl:-SNAPSHOT")
    }
    else {
        implementation(project(":imgui:web:wasm"))
        wasmLibraries(project(path = ":imgui:web:wasm", configuration = "wasmRuntimeElements"))
        implementation(project(":backends:fdx:fdx-impl"))
    }

    implementation("io.github.libfdx:backend_web:${LibExt.libFdxVersion}")
    implementation("io.github.libfdx:gl_web:${LibExt.libFdxVersion}")
    implementation("io.github.libfdx:wgpu_web:${LibExt.libFdxVersion}")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
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

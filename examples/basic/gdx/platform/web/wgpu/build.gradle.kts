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

dependencies {
    implementation(project(":examples:basic:core"))
    implementation(project(":examples:basic:gdx:core"))
    implementation(project(":backends:gdx:gdx-wgpu-impl"))

    if(LibExt.useRepoLibs) {
        implementation("com.github.xpenatan.jImGui:imgui-web:-SNAPSHOT")
        wasmLibraries("com.github.xpenatan.jImGui:imgui-web_wasm:-SNAPSHOT")
    }
    else {
        implementation(project(":imgui:web:wasm"))
        wasmLibraries(project(path = ":imgui:web:wasm", configuration = "wasmRuntimeElements"))
    }

    implementation("com.github.xpenatan.gdx-teavm:backend-web:${LibExt.gdxTeaVMVersion}")
    implementation("${LibExt.gdxWebGPUGroup}:backend-teavm:${LibExt.gdxWebGPUVersion}")
    implementation("com.github.xpenatan.jWebGPU:webgpu-core:${LibExt.jWebGPUVersion}")
    implementation("com.github.xpenatan.jWebGPU:webgpu-web:${LibExt.jWebGPUVersion}")
    implementation("com.github.xpenatan.jWebGPU:webgpu-web_wasm:${LibExt.jWebGPUVersion}")
}

tasks.register<JavaExec>("imgui_basic_wasm_gdx_web_wgpu_run") {
    group = "example-teavm"
    description = "Build the libGDX WebGPU WASM basic example"
    mainClass.set("imgui.example.basic.gdx.web.Build")
    classpath = sourceSets["main"].runtimeClasspath + wasmLibraries
    inputs.files(wasmLibraries)
    doLast {
        copy {
            from(wasmLibraries.map { zipTree(it) }) {
                include("*.js", "*.wasm")
            }
            into(layout.buildDirectory.dir("dist/webapp/scripts"))
        }
    }
}

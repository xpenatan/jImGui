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

dependencies {
    implementation(project(":examples:basic:core"))
    implementation(project(":examples:basic:gdx:core"))
    implementation(project(":backends:gdx:gdx-wgpu-impl"))

    if(providers.gradleProperty("useRepoLibs").map(String::toBoolean).getOrElse(false)) {
        implementation(libs.jImGuiImguiWeb)
        wasmLibraries(libs.jImGuiImguiWebWasm)
    }
    else {
        implementation(project(":imgui:web:wasm"))
        wasmLibraries(project(path = ":imgui:web:wasm", configuration = "wasmRuntimeElements"))
    }

    implementation(libs.gdxTeaVMBackendWeb)
    implementation(libs.gdxWebGPUBackendTeaVM)
    implementation(libs.jWebGpuCore)
    implementation(libs.jWebGpuWeb)
    implementation(libs.jWebGpuWebWasm)
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

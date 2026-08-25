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
    implementation(project(":backends:gdx:gdx-gl-impl"))

    if(rootProject.extra["examplesUseRepoLibs"] as Boolean) {
        implementation(libs.jImGuiImguiWeb)
        wasmLibraries(libs.jImGuiImguiWebWasm)
    }
    else {
        implementation(project(":imgui:web:wasm"))
        wasmLibraries(project(path = ":imgui:web:wasm", configuration = "wasmRuntimeElements"))
    }

    implementation(libs.gdxTeaVMBackendWeb)
}

tasks.register<JavaExec>("imgui_basic_wasm_gdx_web_gl_run") {
    group = "example-teavm"
    description = "Build the libGDX WebGL WASM basic example"
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

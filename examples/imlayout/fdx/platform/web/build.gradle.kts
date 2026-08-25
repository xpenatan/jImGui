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

val mainClassName = "imgui.example.imlayout.Build"

dependencies {
    implementation(project(":examples:shared"))
    implementation(project(":examples:imlayout:fdx:core"))

    if(rootProject.extra["examplesUseRepoLibs"] as Boolean) {
        implementation(libs.jImGuiImguiWeb)
        wasmLibraries(libs.jImGuiImguiWebWasm)
        implementation(libs.jImGuiImlayoutWeb)
        wasmLibraries(libs.jImGuiImlayoutWebWasm)
    }
    else {
        implementation(project(":imgui:web:wasm"))
        wasmLibraries(project(path = ":imgui:web:wasm", configuration = "wasmRuntimeElements"))
        implementation(project(":extensions:imlayout:imlayout-web"))
        wasmLibraries(project(path = ":extensions:imlayout:imlayout-web", configuration = "wasmRuntimeElements"))
    }
    implementation(libs.libFdxImguiExt)

    implementation(libs.libFdxBackendWeb)
    implementation(libs.libFdxGlWeb)
}

tasks.register<JavaExec>("imlayout_web_run") {
    group = "example-teavm"
    description = "Build basic example"
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

plugins {
    id("java-library")
}

val moduleName = "imgui-web"

val emscriptenJS = "$projectDir/../../builder/build/c++/libs/emscripten/imgui.js"
val emscriptenWASM = "$projectDir/../../builder/build/c++/libs/emscripten/imgui.wasm"
val webBuilderTask = project(":imgui:builder").tasks.named("jParser_build_web_wasm")

val wasmJar = tasks.register<Jar>("wasmJar") {
    dependsOn(webBuilderTask)
    from(provider {
        listOf(emscriptenJS, emscriptenWASM).map(::file).filter { it.exists() }
    })
    doFirst {
        listOf(emscriptenJS, emscriptenWASM).forEach { output ->
            check(file(output).isFile) {
                "WebAssembly runtime was not produced: $output"
            }
        }
    }
    archiveBaseName.set("${moduleName}_wasm")
    archiveClassifier.set("")
}

tasks.named("assemble") {
    dependsOn(wasmJar)
}

val wasmRuntimeElements by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

artifacts {
    add(wasmRuntimeElements.name, wasmJar)
}

dependencies {
    api(libs.bundles.jParserWebArtifacts)
}

tasks.named("compileJava") {
    dependsOn(webBuilderTask)
}
tasks.matching { it.name == "sourcesJar" }.configureEach {
    dependsOn(webBuilderTask)
}

val taskNames = gradle.startParameter.taskNames
fun isTaskRequested(taskName: String): Boolean {
    return taskNames.any { it == taskName || it.endsWith(":$taskName") }
}
val isPrepareDeployTask = isTaskRequested("prepareRelease") || isTaskRequested("prepareSnapshot")
val isPublishTask = taskNames.any { it.contains("publish", ignoreCase = true) }
val includeWasmInMainJar = !(isPrepareDeployTask || isPublishTask)

tasks.jar {
    if(includeWasmInMainJar) {
        dependsOn(webBuilderTask)
        from(provider {
            listOf(emscriptenJS, emscriptenWASM).map(::file).filter(File::isFile)
        })
        doFirst {
            listOf(emscriptenJS, emscriptenWASM).forEach { output ->
                check(file(output).isFile) {
                    "WebAssembly runtime was not produced: $output"
                }
            }
        }
    }
}

tasks.named("clean") {
    doFirst {
        val srcPath = "$projectDir/src/main/java/gen"
        val jsPath = "$projectDir/src/main/resources/imgui.wasm.js"
        project.delete(files(srcPath, jsPath))
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }

        create<MavenPublication>("mavenWasm") {
            artifactId = "${moduleName}_wasm"
            artifact(wasmJar)
        }
    }
}

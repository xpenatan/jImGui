plugins {
    id("java")
}

val moduleName = "imlayout-web"

val emscriptenJS = "$projectDir/../imlayout-build/build/c++/libs/emscripten/imlayout.js"
val emscriptenWASM = "$projectDir/../imlayout-build/build/c++/libs/emscripten/imlayout.wasm"
val webBuilderTask = project(":extensions:imlayout:imlayout-build")
    .tasks.named("jParser_build_web_wasm")

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

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
}

dependencies {
    implementation(project(":imgui:web:wasm"))
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
        val srcPath = "$projectDir/src/main/java"
        project.delete(files(srcPath))
    }
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

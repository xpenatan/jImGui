plugins {
    id("java")
}

val moduleName = "nodeeditor-web"

val emscriptenJS = "$projectDir/../nodeeditor-build/build/c++/libs/emscripten/nodeeditor.js"
val emscriptenWASM = "$projectDir/../nodeeditor-build/build/c++/libs/emscripten/nodeeditor.wasm"

val wasmJar = tasks.register<Jar>("wasmJar") {
    from(provider {
        listOf(emscriptenJS, emscriptenWASM).map(::file).filter { it.exists() }
    })
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
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaWebTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaWebTarget)
}

dependencies {
    implementation(project(":imgui:web:wasm"))
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

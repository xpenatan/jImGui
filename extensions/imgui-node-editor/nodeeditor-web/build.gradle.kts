plugins {
    id("java")
}

val moduleName = "nodeeditor-web"

val emscriptenJS = "$projectDir/../nodeeditor-build/build/c++/libs/emscripten/nodeeditor.js"
val emscriptenWASM = "$projectDir/../nodeeditor-build/build/c++/libs/emscripten/nodeeditor.wasm"

val wasmJar = tasks.register<Jar>("wasmJar") {
    dependsOn(":extensions:imgui-node-editor:nodeeditor-build:jParser_build_web_wasm")
    from(emscriptenJS, emscriptenWASM)
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
            groupId = LibExt.groupId
            version = LibExt.libVersion
            from(components["java"])
        }

        create<MavenPublication>("mavenWasm") {
            artifactId = "${moduleName}_wasm"
            groupId = LibExt.groupId
            version = LibExt.libVersion
            artifact(wasmJar)
        }
    }
}

plugins {
    id("java")
}

val moduleName = "imlayout-web"

val emscriptenJS = "$projectDir/../imlayout-build/build/c++/libs/emscripten/imlayout.js"
val emscriptenWASM = "$projectDir/../imlayout-build/build/c++/libs/emscripten/imlayout.wasm"

val wasmJar = tasks.register<Jar>("wasmJar") {
    dependsOn(":extensions:imlayout:imlayout-build:jParser_build_web_wasm")
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

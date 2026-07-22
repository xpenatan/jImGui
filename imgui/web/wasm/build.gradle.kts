plugins {
    id("java-library")
}

val moduleName = "imgui-web"

val emscriptenJS = "$projectDir/../../builder/build/c++/libs/emscripten/imgui.js"
val emscriptenWASM = "$projectDir/../../builder/build/c++/libs/emscripten/imgui.wasm"

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

dependencies {
    api("com.github.xpenatan.jParser:runtime-core:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:runtime-web:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:runtime-web_wasm:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:loader-core:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:loader-web:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:api-core:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:api-web:${LibExt.jParserVersion}")
}

tasks.named("clean") {
    doFirst {
        val srcPath = "$projectDir/src/main/java/gen"
        val jsPath = "$projectDir/src/main/resources/imgui.wasm.js"
        project.delete(files(srcPath, jsPath))
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaWebTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaWebTarget)
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

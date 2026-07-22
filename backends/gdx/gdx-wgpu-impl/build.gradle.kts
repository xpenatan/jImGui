plugins {
    id("java-library")
    id("maven-publish")
}

val moduleName = "gdx-wgpu-impl"
val javaVersion = JavaVersion.toVersion(LibExt.javaMainTarget)

group = LibExt.groupId
version = LibExt.libVersion

dependencies {
    api(project(":backends:gdx:gdx-shared-impl"))
    compileOnly(project(":imgui:core"))
    api("${LibExt.gdxWebGPUGroup}:gdx-webgpu:${LibExt.gdxWebGPUVersion}")
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
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
    }
}

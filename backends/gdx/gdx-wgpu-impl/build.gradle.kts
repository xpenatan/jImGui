plugins {
    id("java-library")
    id("maven-publish")
}

val moduleName = "gdx-wgpu-impl"
val javaVersion = JavaVersion.toVersion(libs.versions.javaMain.get())

dependencies {
    api(project(":backends:gdx:gdx-shared-impl"))
    compileOnly(project(":imgui:core"))
    api(libs.gdxWebGPUCore)
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

plugins {
    id("java-library")
    id("maven-publish")
}

val moduleName = "gdx-gl-lwjgl3-impl"
val javaVersion = JavaVersion.toVersion(libs.versions.javaMain.get())

dependencies {
    api(project(":backends:gdx:gdx-gl-impl"))
    api(libs.gdxBackendLwjgl3)
    compileOnly(project(":imgui:core"))
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
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

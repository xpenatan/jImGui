plugins {
    id("java-library")
    id("maven-publish")
}

val moduleName = "gdx-gl-impl"
val javaVersion = JavaVersion.toVersion(libs.versions.javaMain.get())

dependencies {
    api(project(":backends:gdx:gdx-shared-impl"))
    compileOnly(project(":imgui:core"))
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

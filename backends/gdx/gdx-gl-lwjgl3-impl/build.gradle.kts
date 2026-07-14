plugins {
    id("java-library")
    id("maven-publish")
}

val moduleName = "gdx-gl-lwjgl3-impl"
val javaVersion = JavaVersion.toVersion(LibExt.javaMainTarget)

group = LibExt.groupId
version = LibExt.libVersion

dependencies {
    api(project(":backends:gdx:gdx-gl-impl"))
    api("com.badlogicgames.gdx:gdx-backend-lwjgl3:${LibExt.gdxVersion}")
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
            groupId = LibExt.groupId
            version = LibExt.libVersion
            from(components["java"])
        }
    }
}

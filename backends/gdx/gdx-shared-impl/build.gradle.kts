plugins {
    id("java-library")
    id("maven-publish")
}

val moduleName = "gdx-shared-impl"
val javaVersion = JavaVersion.toVersion(LibExt.javaMainTarget)

group = LibExt.groupId
version = LibExt.libVersion

dependencies {
    api("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    compileOnlyApi(project(":imgui:core"))

    testImplementation("junit:junit:${LibExt.jUnitVersion}")
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
            groupId = LibExt.groupId
            version = LibExt.libVersion
            from(components["java"])
        }
    }
}

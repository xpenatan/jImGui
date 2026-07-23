plugins {
    id("java-library")
    id("maven-publish")
}

val moduleName = "gdx-shared-impl"
val javaVersion = JavaVersion.toVersion(libs.versions.javaMain.get())

dependencies {
    api(libs.gdxCore)
    compileOnlyApi(project(":imgui:core"))

    testImplementation(libs.junit)
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

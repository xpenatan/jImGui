plugins {
    id("java-library")
    id("maven-publish")
}

val moduleName = "fdx-impl"
val javaVersion = JavaVersion.toVersion(libs.versions.javaFFM.get())

dependencies {
    compileOnlyApi(project(":imgui:core"))
    api(libs.bundles.libFdxCoreArtifacts)

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

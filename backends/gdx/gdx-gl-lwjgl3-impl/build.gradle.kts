plugins {
    id("java-library")
    id("maven-publish")
}

val moduleName = "gdx-gl-lwjgl3-impl"
val javaVersion = JavaVersion.toVersion(libs.versions.javaMain.get())
val examplesUseMavenArtifacts = rootProject.extra["examplesUseMavenArtifacts"] as Boolean

dependencies {
    if(examplesUseMavenArtifacts) {
        api(libs.jImGuiGdxGl)
        compileOnly(libs.jImGuiImguiCore)
    }
    else {
        api(project(":backends:gdx:gdx-gl-impl"))
        compileOnly(project(":imgui:core"))
    }
    api(libs.gdxBackendLwjgl3)
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

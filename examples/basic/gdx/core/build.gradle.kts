plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
}

dependencies {
    api(project(":examples:shared"))

    if(rootProject.extra["examplesUseMavenArtifacts"] as Boolean) {
        api(libs.jImGuiGdxShared)
        compileOnlyApi(libs.jImGuiImguiCore)
    }
    else {
        api(project(":backends:gdx:gdx-shared-impl"))
        compileOnlyApi(project(":imgui:core"))
    }
}

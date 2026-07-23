plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
}

dependencies {
    api(project(":examples:shared"))
    api(project(":backends:gdx:gdx-shared-impl"))

    if(providers.gradleProperty("useRepoLibs").map(String::toBoolean).getOrElse(false)) {
        compileOnlyApi(libs.jImGuiImguiCore)
    }
    else {
        compileOnlyApi(project(":imgui:core"))
    }
}

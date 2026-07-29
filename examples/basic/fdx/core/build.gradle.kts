plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
}

dependencies {
    api(project(":examples:shared"))
    api(project(":examples:basic:core"))

    if(providers.gradleProperty("useRepoLibs").map(String::toBoolean).getOrElse(false)) {
        compileOnlyApi(libs.jImGuiImguiCore)
    }
    else {
        compileOnlyApi(project(":imgui:core"))
    }
    api(libs.libFdxImguiExt)
}

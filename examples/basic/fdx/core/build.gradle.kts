plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
}

dependencies {
    api(project(":examples:shared"))
    api(project(":examples:basic:core"))

    if(LibExt.useRepoLibs) {
        compileOnlyApi("com.github.xpenatan.jImGui:imgui-core:-SNAPSHOT")
        api("com.github.xpenatan.jImGui:fdx-impl:-SNAPSHOT")
    }
    else {
        compileOnlyApi(project(":imgui:core"))
        api(project(":backends:fdx:fdx-impl"))
    }
}

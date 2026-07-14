plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaWebTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaWebTarget)
}

dependencies {
    api(project(":examples:basic:base"))
    api(project(":backends:gdx:gdx-shared-impl"))

    if(LibExt.useRepoLibs) {
        compileOnlyApi("com.github.xpenatan.jImGui:imgui-core:-SNAPSHOT")
    }
    else {
        compileOnlyApi(project(":imgui:core"))
    }
}

plugins {
    id("java")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaWebTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaWebTarget)
}

dependencies {
    implementation(project(":examples:basic:base"))

    if(LibExt.useRepoLibs) {
        compileOnly("com.github.xpenatan.jImGui:imgui-core:-SNAPSHOT")
    }
    else {
        compileOnly(project(":imgui:core"))
    }

    implementation("com.github.xpenatan.jParser:loader-core:${LibExt.jParserVersion}")
}

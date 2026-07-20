plugins {
    id("java")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaWebTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaWebTarget)
}

dependencies {
    implementation(project(":examples:shared"))

    if(LibExt.useRepoLibs) {
        compileOnly("com.github.xpenatan.jImGui:imgui-core:-SNAPSHOT")
        compileOnly("com.github.xpenatan.jImGui:textedit-core:-SNAPSHOT")
    }
    else {
        compileOnly(project(":imgui:core"))
        compileOnly(project(":extensions:ImGuiColorTextEdit:textedit-core"))
    }

    implementation("com.github.xpenatan.jParser:loader-core:${LibExt.jParserVersion}")
}

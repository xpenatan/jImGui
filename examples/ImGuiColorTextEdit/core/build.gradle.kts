plugins {
    id("java")
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
}

dependencies {
    implementation(project(":examples:shared"))

    if(rootProject.extra["examplesUseMavenArtifacts"] as Boolean) {
        compileOnly(libs.jImGuiImguiCore)
        compileOnly(libs.jImGuiTextEditCore)
    }
    else {
        compileOnly(project(":imgui:core"))
        compileOnly(project(":extensions:ImGuiColorTextEdit:textedit-core"))
    }

    implementation(libs.jParserLoaderCore)
}

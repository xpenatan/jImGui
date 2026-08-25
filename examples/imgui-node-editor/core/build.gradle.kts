plugins {
    id("java")
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
}

dependencies {
    implementation(project(":examples:shared"))

    if(rootProject.extra["examplesUseRepoLibs"] as Boolean) {
        compileOnly(libs.jImGuiImguiCore)
        compileOnly(libs.jImGuiNodeEditorCore)
    }
    else {
        compileOnly(project(":imgui:core"))
        compileOnly(project(":extensions:imgui-node-editor:nodeeditor-core"))
    }

    implementation(libs.jParserLoaderCore)
}

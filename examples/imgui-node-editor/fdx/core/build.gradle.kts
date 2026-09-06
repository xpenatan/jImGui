plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
}

val examplesUseMavenArtifacts = rootProject.extra["examplesUseMavenArtifacts"] as Boolean

dependencies {
    api(project(":examples:basic:fdx:core"))
    api(project(":examples:imgui-node-editor:core"))
    if(examplesUseMavenArtifacts) {
        compileOnlyApi(libs.jImGuiNodeEditorCore)
    }
    else {
        compileOnlyApi(project(":extensions:imgui-node-editor:nodeeditor-core"))
    }
}

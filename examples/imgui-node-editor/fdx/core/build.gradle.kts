plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
}

dependencies {
    api(project(":examples:basic:fdx:core"))
    api(project(":examples:imgui-node-editor:core"))
    compileOnlyApi(project(":extensions:imgui-node-editor:nodeeditor-core"))
}

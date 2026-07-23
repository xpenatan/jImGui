plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
}

dependencies {
    api(project(":examples:basic:fdx:core"))
    api(project(":examples:ImGuiColorTextEdit:core"))
    compileOnlyApi(project(":extensions:ImGuiColorTextEdit:textedit-core"))
}

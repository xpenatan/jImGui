plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
}

dependencies {
    api(project(":examples:basic:fdx"))
    api(project(":examples:ImGuiColorTextEdit:core"))
    compileOnlyApi(project(":extensions:ImGuiColorTextEdit:textedit-core"))
}

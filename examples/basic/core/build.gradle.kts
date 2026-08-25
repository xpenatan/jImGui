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
    }
    else {
        compileOnly(project(":imgui:core"))
    }

    implementation(libs.jParserLoaderCore)
}

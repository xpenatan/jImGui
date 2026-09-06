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
    api(project(":examples:imlayout:core"))
    if(examplesUseMavenArtifacts) {
        compileOnlyApi(libs.jImGuiImlayoutCore)
    }
    else {
        compileOnlyApi(project(":extensions:imlayout:imlayout-core"))
    }
}

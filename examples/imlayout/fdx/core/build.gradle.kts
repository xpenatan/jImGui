plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
}

dependencies {
    api(project(":examples:basic:fdx:core"))
    api(project(":examples:imlayout:core"))
    compileOnlyApi(project(":extensions:imlayout:imlayout-core"))
}

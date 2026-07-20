plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
}

dependencies {
    api(project(":examples:basic:fdx:core"))
    api(project(":examples:imlayout:core"))
    compileOnlyApi(project(":extensions:imlayout:imlayout-core"))
}

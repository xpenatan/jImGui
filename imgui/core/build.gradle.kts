plugins {
    id("java-library")
}

val moduleName = "imgui-core"

dependencies {
    api(libs.jParserLoaderCore)
    api(libs.jParserApiCore)
    api(libs.jParserRuntimeCore)

    testImplementation(libs.junit)
}

tasks.named("clean") {
    doFirst {
        val srcPath = "$projectDir/src/main/java"
        project.delete(files(srcPath))
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}

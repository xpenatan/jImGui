plugins {
    id("java-library")
}

val moduleName = "imgui-shared-c"
val generatedTeaVMCResourcesDir = layout.buildDirectory.dir("generated/jparser/resources/main")

base {
    archivesName.set(moduleName)
}

dependencies {
    api(project(":imgui:core"))
    api(libs.jParserApiCore)
    api(libs.jParserLoaderCore)
    api(libs.jParserRuntimeCore)
    api(libs.jParserRuntimeC)
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src/main/java"))
        java.include("gen/c/**/*.java")
        resources.setSrcDirs(listOf("src/main/resources", generatedTeaVMCResourcesDir))
    }
}

tasks.named("clean") {
    doFirst {
        project.delete(files("$projectDir/src/main/java"))
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
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

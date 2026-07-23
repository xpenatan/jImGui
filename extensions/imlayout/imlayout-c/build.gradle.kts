plugins {
    id("java-library")
}

val moduleName = "imlayout-c"
val nativeName = "imlayout"
val generatedTeaVMCResourcesDir = layout.buildDirectory.dir("generated/jparser/resources/main")
val nativeRoot = file("$projectDir/../imlayout-build/build/c++/libs")
val nativeResourceRoot = "external_cpp/jparser/$nativeName/native"

data class NativeResource(val sourcePath: String, val platform: String)

val nativeResources = listOf(
    NativeResource("$nativeRoot/windows/vc/teavm_c/${nativeName}64_.lib", "windows_x64"),
    NativeResource("$nativeRoot/windows/vc/teavm_c/${nativeName}64.lib", "windows_x64"),
    NativeResource("$nativeRoot/windows/vc/teavm_c/${nativeName}64.dll", "windows_x64"),
    NativeResource("$nativeRoot/linux/teavm_c/lib${nativeName}64_.a", "linux_x64"),
    NativeResource("$nativeRoot/linux/teavm_c/lib${nativeName}64.so", "linux_x64"),
    NativeResource("$nativeRoot/mac/teavm_c/lib${nativeName}64_.a", "mac_x64"),
    NativeResource("$nativeRoot/mac/teavm_c/lib${nativeName}64.dylib", "mac_x64"),
    NativeResource("$nativeRoot/mac/arm/teavm_c/lib${nativeName}64_.a", "mac_arm64"),
    NativeResource("$nativeRoot/mac/arm/teavm_c/lib${nativeName}arm64.dylib", "mac_arm64")
)

dependencies {
    api(project(":imgui:desktop:c"))
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src/main/java"))
        java.include("gen/c/**/*.java")
        resources.setSrcDirs(listOf("src/main/resources", generatedTeaVMCResourcesDir))
    }
}

tasks.named<Jar>("jar") {
    nativeResources.forEach { resource ->
        from(provider { listOf(file(resource.sourcePath)).filter { it.exists() } }) {
            into("$nativeResourceRoot/${resource.platform}")
        }
    }
}

tasks.named("clean") {
    doFirst { project.delete(files("$projectDir/src/main/java")) }
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

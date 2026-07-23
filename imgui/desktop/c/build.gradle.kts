plugins {
    id("java-library")
}

val moduleName = "imgui-c"
val nativeResourceRoot = "external_cpp/jparser/imgui/native"
val nativeRoot = file("$projectDir/../../builder/build/c++/libs")

data class NativeResource(val sourcePath: String, val platform: String)

val nativeResources = listOf(
    NativeResource("$nativeRoot/windows/vc/teavm_c/imgui64_.lib", "windows_x64"),
    NativeResource("$nativeRoot/windows/vc/teavm_c/imgui64.lib", "windows_x64"),
    NativeResource("$nativeRoot/windows/vc/teavm_c/imgui64.dll", "windows_x64"),
    NativeResource("$nativeRoot/linux/teavm_c/libimgui64_.a", "linux_x64"),
    NativeResource("$nativeRoot/linux/teavm_c/libimgui64.so", "linux_x64"),
    NativeResource("$nativeRoot/mac/teavm_c/libimgui64_.a", "mac_x64"),
    NativeResource("$nativeRoot/mac/teavm_c/libimgui64.dylib", "mac_x64"),
    NativeResource("$nativeRoot/mac/arm/teavm_c/libimgui64_.a", "mac_arm64"),
    NativeResource("$nativeRoot/mac/arm/teavm_c/libimguiarm64.dylib", "mac_arm64")
)

base {
    archivesName.set(moduleName)
}

tasks.named<Jar>("jar") {
    nativeResources.forEach { resource ->
        from(provider { listOf(file(resource.sourcePath)).filter { it.exists() } }) {
            into("$nativeResourceRoot/${resource.platform}")
        }
    }
}

dependencies {
    api(project(":imgui:shared:c"))
    implementation(libs.jParserRuntimeDesktopCWindowsX64)
    implementation(libs.jParserRuntimeDesktopCLinuxX64)
    implementation(libs.jParserRuntimeDesktopCMacX64)
    implementation(libs.jParserRuntimeDesktopCMacArm64)
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

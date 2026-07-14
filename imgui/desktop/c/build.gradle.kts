plugins {
    id("java-library")
}

val moduleName = "imgui-c"
group = "${LibExt.groupId}.desktop"
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
    implementation("com.github.xpenatan.jParser:runtime-desktop-c_windows_x64:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:runtime-desktop-c_linux_x64:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:runtime-desktop-c_mac_x64:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:runtime-desktop-c_mac_arm64:${LibExt.jParserVersion}")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaWebTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaWebTarget)
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            groupId = LibExt.groupId
            version = LibExt.libVersion
            from(components["java"])
        }
    }
}

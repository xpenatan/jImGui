import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    id("java")
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
}

fun currentDesktopPlatform(): String {
    val os = DefaultNativePlatform.getCurrentOperatingSystem()
    val arch = System.getProperty("os.arch").lowercase()
    return when {
        os.isWindows -> "windows_x64"
        os.isLinux -> "linux_x64"
        os.isMacOsX && (arch.contains("aarch64") || arch.contains("arm64")) -> "mac_arm64"
        os.isMacOsX -> "mac_x64"
        else -> throw GradleException("Unsupported desktop platform: ${os.name} $arch")
    }
}

dependencies {
    implementation(project(":examples:basic:core"))
    implementation(project(":examples:basic:gdx:core"))
    implementation(project(":backends:gdx:gdx-wgpu-impl"))

    if(providers.gradleProperty("useRepoLibs").map(String::toBoolean).getOrElse(false)) {
        implementation(libs.bundles.jImGuiFFMArtifacts)
    }
    else {
        implementation(project(":imgui:desktop:ffm"))
    }

    implementation(libs.gdxWebGPUBackendDesktopFFM)
    runtimeOnly(
        when(currentDesktopPlatform()) {
            "windows_x64" -> libs.jWebGpuDesktopFFMWgpuWindowsX64
            "linux_x64" -> libs.jWebGpuDesktopFFMWgpuLinuxX64
            "mac_x64" -> libs.jWebGpuDesktopFFMWgpuMacX64
            "mac_arm64" -> libs.jWebGpuDesktopFFMWgpuMacArm64
            else -> throw GradleException("Unsupported desktop platform")
        }
    )
    implementation(variantOf(libs.gdxPlatform) { classifier("natives-desktop") })
}

tasks.register<JavaExec>("imgui_basic_ffm_gdx_desktop_wgpu_run") {
    group = "example-desktop"
    description = "Run the libGDX desktop WGPU FFM example"
    mainClass.set("imgui.example.basic.Main")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = project.file("../../../../../assets")

    if(DefaultNativePlatform.getCurrentOperatingSystem().isMacOsX) {
        jvmArgs("-XstartOnFirstThread")
    }
}

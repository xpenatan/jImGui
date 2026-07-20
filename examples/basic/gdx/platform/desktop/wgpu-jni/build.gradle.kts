import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    id("java")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
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

    if(LibExt.useRepoLibs) {
        implementation("com.github.xpenatan.jImGui:imgui-jni:-SNAPSHOT")
        implementation("com.github.xpenatan.jImGui:imgui-jni_windows_x64:-SNAPSHOT")
        implementation("com.github.xpenatan.jImGui:imgui-jni_linux_x64:-SNAPSHOT")
        implementation("com.github.xpenatan.jImGui:imgui-jni_mac_x64:-SNAPSHOT")
        implementation("com.github.xpenatan.jImGui:imgui-jni_mac_arm64:-SNAPSHOT")
    }
    else {
        implementation(project(":imgui:desktop:jni"))
    }

    implementation("${LibExt.gdxWebGPUGroup}:backend-desktop-jni:${LibExt.gdxWebGPUVersion}")
    runtimeOnly("com.github.xpenatan.jWebGPU:webgpu-desktop-jni-wgpu_${currentDesktopPlatform()}:${LibExt.jWebGPUVersion}")
    implementation("com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:natives-desktop")
}

tasks.register<JavaExec>("imgui_basic_jni_gdx_desktop_wgpu_run") {
    group = "example-desktop"
    description = "Run the libGDX desktop WGPU JNI example"
    mainClass.set("imgui.example.basic.Main")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = project.file("../../../../../assets")

    if(DefaultNativePlatform.getCurrentOperatingSystem().isMacOsX) {
        jvmArgs("-XstartOnFirstThread")
    }
}

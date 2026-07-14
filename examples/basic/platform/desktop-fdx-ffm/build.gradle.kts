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
    implementation(project(":examples:basic:fdx"))

    if(LibExt.useRepoLibs) {
        implementation("com.github.xpenatan.jImGui:imgui-ffm:-SNAPSHOT")
        implementation("com.github.xpenatan.jImGui:imgui-ffm_windows_x64:-SNAPSHOT")
        implementation("com.github.xpenatan.jImGui:imgui-ffm_linux_x64:-SNAPSHOT")
        implementation("com.github.xpenatan.jImGui:imgui-ffm_mac_x64:-SNAPSHOT")
        implementation("com.github.xpenatan.jImGui:imgui-ffm_mac_arm64:-SNAPSHOT")
    }
    else {
        implementation(project(":imgui:desktop:ffm"))
    }

    implementation("io.github.libfdx:backend_desktop:${LibExt.libFdxVersion}")
    implementation("io.github.libfdx:wgpu_desktop_ffm:${LibExt.libFdxVersion}")
    runtimeOnly("com.github.xpenatan.jWebGPU:webgpu-desktop-ffm-wgpu_${currentDesktopPlatform()}:-SNAPSHOT")
    runtimeOnly("io.github.libfdx:fdx_desktop:${LibExt.libFdxVersion}")
    runtimeOnly("io.github.libfdx:gl_desktop:${LibExt.libFdxVersion}")
    runtimeOnly("io.github.libfdx:vulkan_desktop:${LibExt.libFdxVersion}")
}

val mainClassName = "imgui.example.basic.Main"
val assetsDir = project.file("../../../assets")

fun registerFdxRunTask(graphics: String) = tasks.register<JavaExec>(
    "imgui_basic_ffm_fdx_desktop_${graphics}_run"
) {
    group = "example-desktop"
    description = "Run the libfdx desktop FFM example with ${graphics.uppercase()}"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = assetsDir
    args("--graphics=$graphics")

    if(DefaultNativePlatform.getCurrentOperatingSystem().isMacOsX) {
        jvmArgs("-XstartOnFirstThread")
    }
}

registerFdxRunTask("wgpu")
registerFdxRunTask("gl")
registerFdxRunTask("vulkan")

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
    implementation(project(":examples:basic:fdx:core"))

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

    implementation("io.github.libfdx:backend_desktop:${LibExt.libFdxVersion}")
    implementation("io.github.libfdx:wgpu_desktop_jni:${LibExt.libFdxVersion}")
    runtimeOnly("com.github.xpenatan.jWebGPU:webgpu-desktop-jni-wgpu_${currentDesktopPlatform()}:-SNAPSHOT")
    runtimeOnly("io.github.libfdx:fdx_desktop:${LibExt.libFdxVersion}")
    runtimeOnly("io.github.libfdx:gl_desktop:${LibExt.libFdxVersion}")
    runtimeOnly("io.github.libfdx:vulkan_desktop:${LibExt.libFdxVersion}")
}

val mainClassName = "imgui.example.basic.Main"
val assetsDir = project.file("../../../../../assets")

fun registerFdxRunTask(graphics: String) = tasks.register<JavaExec>(
    "imgui_basic_jni_fdx_desktop_${graphics}_run"
) {
    group = "example-desktop"
    description = "Run the libfdx desktop JNI example with ${graphics.uppercase()}"
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

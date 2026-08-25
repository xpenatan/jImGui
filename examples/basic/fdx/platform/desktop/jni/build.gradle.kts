import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    id("java")
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
}

dependencies {
    implementation(project(":examples:basic:core"))
    implementation(project(":examples:basic:fdx:core"))

    if(rootProject.extra["examplesUseRepoLibs"] as Boolean) {
        implementation(libs.bundles.jImGuiJniArtifacts)
    }
    else {
        implementation(project(":imgui:desktop:jni"))
    }

    implementation(libs.libFdxBackendDesktop)
    implementation(libs.libFdxWgpuDesktopJni)
    runtimeOnly(libs.libFdxFdxDesktop)
    runtimeOnly(libs.libFdxGlDesktop)
    runtimeOnly(libs.libFdxVulkanDesktop)
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

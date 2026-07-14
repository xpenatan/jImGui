import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    id("java")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
}

dependencies {
    implementation(project(":examples:basic:base"))
    implementation(project(":examples:imlayout:fdx"))

    if(LibExt.useRepoLibs) {
        implementation("com.github.xpenatan.jImGui:imgui-jni:-SNAPSHOT")
        implementation("com.github.xpenatan.jImGui:imgui-jni_desktop:-SNAPSHOT")
        implementation("com.github.xpenatan.jImGui:imlayout-jni:-SNAPSHOT")
        implementation("com.github.xpenatan.jImGui:imlayout-jni_desktop:-SNAPSHOT")
    }
    else {
        implementation(project(":imgui:desktop:jni"))
        implementation(project(":extensions:imlayout:imlayout-jni"))
    }

    implementation(project(":backends:fdx:fdx-impl"))
    implementation("io.github.libfdx:backend_desktop:${LibExt.libFdxVersion}")
    runtimeOnly("io.github.libfdx:fdx_desktop:${LibExt.libFdxVersion}")
    runtimeOnly("io.github.libfdx:gl_desktop:${LibExt.libFdxVersion}")
    runtimeOnly("org.lwjgl:lwjgl-opengl:3.4.1")
    runtimeOnly("org.lwjgl:lwjgl-opengl:3.4.1:natives-linux")
    runtimeOnly("org.lwjgl:lwjgl-opengl:3.4.1:natives-macos")
    runtimeOnly("org.lwjgl:lwjgl-opengl:3.4.1:natives-macos-arm64")
    runtimeOnly("org.lwjgl:lwjgl-opengl:3.4.1:natives-windows")
}

val mainClassName = "imgui.example.imlayout.Main"
val assetsDir = project.file("../../../assets")

tasks.register<JavaExec>("imlayout_desktop_run") {
    group = "example-desktop"
    description = "Run desktop app"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = assetsDir

    if(DefaultNativePlatform.getCurrentOperatingSystem().isMacOsX) {
        jvmArgs("-XstartOnFirstThread")
    }
}

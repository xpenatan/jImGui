import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    id("java")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
}

dependencies {
    implementation(project(":examples:basic:core"))
    implementation(project(":examples:basic:gdx:core"))
    implementation(project(":backends:gdx:gdx-gl-lwjgl3-impl"))

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

    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:${LibExt.gdxVersion}")
    implementation("com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:natives-desktop")
}

tasks.register<JavaExec>("imgui_basic_ffm_gdx_desktop_gl_run") {
    group = "example-desktop"
    description = "Run the libGDX desktop GL FFM example"
    mainClass.set("imgui.example.basic.Main")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = project.file("../../../../../assets")

    if(DefaultNativePlatform.getCurrentOperatingSystem().isMacOsX) {
        jvmArgs("-XstartOnFirstThread")
    }
}

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
        implementation("com.github.xpenatan.jImGui:imgui-jni:-SNAPSHOT")
        implementation("com.github.xpenatan.jImGui:imgui-jni_windows_x64:-SNAPSHOT")
        implementation("com.github.xpenatan.jImGui:imgui-jni_linux_x64:-SNAPSHOT")
        implementation("com.github.xpenatan.jImGui:imgui-jni_mac_x64:-SNAPSHOT")
        implementation("com.github.xpenatan.jImGui:imgui-jni_mac_arm64:-SNAPSHOT")
    }
    else {
        implementation(project(":imgui:desktop:jni"))
    }

    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:${LibExt.gdxVersion}")
    implementation("com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:natives-desktop")
}

tasks.register<JavaExec>("imgui_basic_jni_gdx_desktop_gl_run") {
    group = "example-desktop"
    description = "Run the libGDX desktop GL JNI example"
    mainClass.set("imgui.example.basic.Main")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = project.file("../../../../../assets")

    if(DefaultNativePlatform.getCurrentOperatingSystem().isMacOsX) {
        jvmArgs("-XstartOnFirstThread")
    }
}

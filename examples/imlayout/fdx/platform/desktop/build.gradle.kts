import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    id("java")
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
}

dependencies {
    implementation(project(":examples:shared"))
    implementation(project(":examples:imlayout:fdx:core"))

    if(providers.gradleProperty("useRepoLibs").map(String::toBoolean).getOrElse(false)) {
        implementation(libs.jImGuiImguiJni)
        implementation(libs.jImGuiImguiJniDesktop)
        implementation(libs.jImGuiImlayoutJni)
        implementation(libs.jImGuiImlayoutJniDesktop)
    }
    else {
        implementation(project(":imgui:desktop:jni"))
        implementation(project(":extensions:imlayout:imlayout-jni"))
    }

    implementation(project(":backends:fdx:fdx-impl"))
    implementation(libs.libFdxBackendDesktop)
    runtimeOnly(libs.libFdxFdxDesktop)
    runtimeOnly(libs.libFdxGlDesktop)
    runtimeOnly(libs.lwjglOpenGl)
    runtimeOnly(variantOf(libs.lwjglOpenGl) { classifier("natives-linux") })
    runtimeOnly(variantOf(libs.lwjglOpenGl) { classifier("natives-macos") })
    runtimeOnly(variantOf(libs.lwjglOpenGl) { classifier("natives-macos-arm64") })
    runtimeOnly(variantOf(libs.lwjglOpenGl) { classifier("natives-windows") })
}

val mainClassName = "imgui.example.imlayout.Main"
val assetsDir = project.file("../../../../assets")

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

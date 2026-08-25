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
    implementation(project(":examples:imgui-node-editor:fdx:core"))

    if(rootProject.extra["examplesUseRepoLibs"] as Boolean) {
        implementation(libs.jImGuiImguiJni)
        implementation(libs.jImGuiImguiJniDesktop)
        implementation(libs.jImGuiNodeEditorJni)
        implementation(libs.jImGuiNodeEditorJniDesktop)
    }
    else {
        implementation(project(":imgui:desktop:jni"))
        implementation(project(":extensions:imgui-node-editor:nodeeditor-jni"))
    }

    implementation(libs.libFdxImguiExt)
    implementation(libs.libFdxBackendDesktop)
    runtimeOnly(libs.libFdxFdxDesktop)
    runtimeOnly(libs.libFdxGlDesktop)
}

val mainClassName = "imgui.example.nodeeditor.Main"
val assetsDir = project.file("../../../../assets")

tasks.register<JavaExec>("nodeeditor_desktop_run") {
    group = "example-desktop"
    description = "Run desktop app"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = assetsDir

    if(DefaultNativePlatform.getCurrentOperatingSystem().isMacOsX) {
        jvmArgs("-XstartOnFirstThread")
    }
}

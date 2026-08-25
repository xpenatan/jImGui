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
    implementation(project(":examples:basic:gdx:core"))
    implementation(project(":backends:gdx:gdx-gl-lwjgl3-impl"))

    if(rootProject.extra["examplesUseRepoLibs"] as Boolean) {
        implementation(libs.bundles.jImGuiFFMArtifacts)
    }
    else {
        implementation(project(":imgui:desktop:ffm"))
    }

    implementation(libs.gdxBackendLwjgl3)
    implementation(variantOf(libs.gdxPlatform) { classifier("natives-desktop") })
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

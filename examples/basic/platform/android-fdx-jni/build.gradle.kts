plugins {
    id("com.android.application")
}

group = "imgui.example.basic.fdx.android"

android {
    namespace = "imgui.example.basic.fdx.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "imgui.example.basic.fdx"
        minSdk = 29
        versionCode = 1
        versionName = "1.0"
    }

    sourceSets {
        named("main") {
            assets.srcDirs(project.file("../../../assets"))
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(LibExt.javaModernTarget)
        targetCompatibility = JavaVersion.toVersion(LibExt.javaModernTarget)
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")

    configurations.configureEach {
        exclude(module = "imgui-core")
        exclude(group = "com.github.xpenatan.jParser", module = "runtime-core")
    }

    if(LibExt.useRepoLibs) {
        implementation("com.github.xpenatan.jImGui:imgui-android:-SNAPSHOT")
    }
    else {
        implementation(project(":imgui:android:jni"))
    }

    implementation(project(":examples:basic:base")) {
        exclude(module = "core")
    }
    implementation(project(":examples:basic:core")) {
        exclude(module = "core")
    }
    implementation(project(":examples:basic:fdx")) {
        exclude(module = "core")
    }
    implementation(project(":backends:fdx:fdx-impl"))
    implementation("io.github.libfdx:backend_android:${LibExt.libFdxVersion}")
    implementation("io.github.libfdx:fdx_android:${LibExt.libFdxVersion}")
    implementation("io.github.libfdx:vulkan_android_jni:${LibExt.libFdxVersion}")
    implementation("io.github.libfdx:wgpu_android_jni:${LibExt.libFdxVersion}")
}

tasks.register("imgui_basic_jni_fdx_android_build") {
    group = "example-android"
    description = "Build the libfdx Android JNI basic example"
    dependsOn("assembleDebug")
}

fun registerAndroidRunTask(name: String, activity: String, graphics: String) {
    tasks.register<Exec>(name) {
        group = "example-android"
        description = "Install and run the libfdx Android $graphics JNI basic example"
        dependsOn("installDebug")
        commandLine(
            android.adbExecutable.absolutePath,
            "shell", "am", "start", "-n",
            "imgui.example.basic.fdx/$activity"
        )
    }
}

registerAndroidRunTask(
    "imgui_basic_jni_fdx_android_gl_run",
    "imgui.example.basic.fdx.android.FdxAndroidGlesActivity",
    "OpenGL ES"
)
registerAndroidRunTask(
    "imgui_basic_jni_fdx_android_vulkan_run",
    "imgui.example.basic.fdx.android.FdxAndroidVulkanActivity",
    "Vulkan"
)
registerAndroidRunTask(
    "imgui_basic_jni_fdx_android_wgpu_run",
    "imgui.example.basic.fdx.android.FdxAndroidWgpuActivity",
    "WGPU"
)

plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "imgui.example.basic.fdx.android"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        applicationId = "imgui.example.basic.fdx"
        minSdk = libs.versions.androidMinSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    sourceSets {
        named("main") {
            assets.directories.add(project.file("../../../../assets").absolutePath)
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.javaModern.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.javaModern.get())
    }
}

dependencies {
    coreLibraryDesugaring(libs.androidDesugar)

    configurations.configureEach {
        exclude(module = "imgui-core")
        exclude(group = "com.github.xpenatan.jParser", module = "runtime-core")
    }

    if(providers.gradleProperty("useRepoLibs").map(String::toBoolean).getOrElse(false)) {
        implementation(libs.jImGuiImguiAndroid)
    }
    else {
        implementation(project(":imgui:android:jni"))
    }

    implementation(project(":examples:shared")) {
        exclude(module = "core")
    }
    implementation(project(":examples:basic:core")) {
        exclude(module = "core")
    }
    implementation(project(":examples:basic:fdx:core")) {
        exclude(module = "core")
    }
    implementation(project(":backends:fdx:fdx-impl"))
    implementation(libs.libFdxBackendAndroid)
    implementation(libs.libFdxFdxAndroid)
    implementation(libs.libFdxVulkanAndroidJni)
    implementation(libs.libFdxWgpuAndroidJni)
}

tasks.register("imgui_basic_jni_fdx_android_build") {
    group = "example-android"
    description = "Build the libfdx Android JNI basic example"
    dependsOn("assembleDebug")
}

val adbExecutable = androidComponents.sdkComponents.adb

fun registerAndroidRunTask(name: String, activity: String, graphics: String) {
    tasks.register<Exec>(name) {
        group = "example-android"
        description = "Install and run the libfdx Android $graphics JNI basic example"
        dependsOn("installDebug")
        doFirst {
            commandLine(
                adbExecutable.get().asFile.absolutePath,
                "shell", "am", "start", "-n",
                "imgui.example.basic.fdx/$activity"
            )
        }
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

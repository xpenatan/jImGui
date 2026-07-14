plugins {
    id("com.android.application")
}

group = "imgui.example.basic.gdx.wgpu.android"

val gdxNativeClassifiers = linkedMapOf(
    "armeabi-v7a" to "natives-armeabi-v7a",
    "arm64-v8a" to "natives-arm64-v8a",
    "x86" to "natives-x86",
    "x86_64" to "natives-x86_64"
)
val gdxNativeConfigurations = gdxNativeClassifiers.keys.associateWith { abi ->
    configurations.create("gdxNatives${abi.replace("-", "").replace("_", "")}") {
        isCanBeConsumed = false
        isCanBeResolved = true
    }
}
val stagedGdxJniLibsDir = layout.buildDirectory.dir("generated/gdxJniLibs")

val stageGdxJniLibs by tasks.registering(Copy::class) {
    gdxNativeConfigurations.forEach { (abi, configuration) ->
        from(configuration.incoming.artifactView { }.files.elements.map { files ->
            files.map { zipTree(it.asFile) }
        }) {
            include("*.so")
            into(abi)
        }
    }
    into(stagedGdxJniLibsDir)
    doFirst {
        delete(stagedGdxJniLibsDir)
    }
}

android {
    namespace = "imgui.example.basic.gdx.wgpu.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "imgui.example.basic.gdx.wgpu"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    sourceSets {
        named("main") {
            assets.srcDirs(project.file("../../../assets"))
            jniLibs.srcDirs(stagedGdxJniLibsDir)
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
    implementation(project(":examples:basic:core"))
    implementation(project(":examples:basic:gdx"))
    implementation(project(":backends:gdx:gdx-wgpu-impl"))
    implementation(project(":imgui:android:jni"))
    implementation("${LibExt.gdxWebGPUGroup}:backend-android:${LibExt.gdxWebGPUVersion}")

    gdxNativeClassifiers.forEach { (abi, classifier) ->
        add(
            gdxNativeConfigurations.getValue(abi).name,
            "com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:$classifier"
        )
    }
}

tasks.matching { task ->
    task.name == "mergeDebugJniLibFolders" || task.name == "mergeReleaseJniLibFolders"
}.configureEach {
    dependsOn(stageGdxJniLibs)
}

tasks.register("imgui_basic_jni_gdx_android_wgpu_build") {
    group = "example-android"
    description = "Build the libGDX Android WGPU JNI basic example"
    dependsOn("assembleDebug")
}

tasks.register<Exec>("imgui_basic_jni_gdx_android_wgpu_run") {
    group = "example-android"
    description = "Install and run the libGDX Android WGPU JNI basic example"
    dependsOn("installDebug")
    commandLine(
        android.adbExecutable.absolutePath,
        "shell", "am", "start", "-n",
        "imgui.example.basic.gdx.wgpu/imgui.example.basic.gdx.wgpu.android.GdxAndroidWgpuActivity"
    )
}

plugins {
    alias(libs.plugins.androidApplication)
}

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
    namespace = "imgui.example.basic.gdx.gl.android"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        applicationId = "imgui.example.basic.gdx.gl"
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    sourceSets {
        named("main") {
            assets.srcDirs(project.file("../../../../../assets"))
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
        sourceCompatibility = JavaVersion.toVersion(libs.versions.javaModern.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.javaModern.get())
    }
}

dependencies {
    implementation(project(":examples:basic:core"))
    implementation(project(":examples:basic:gdx:core"))
    implementation(project(":backends:gdx:gdx-gl-impl"))
    implementation(project(":imgui:android:jni"))
    implementation(libs.gdxBackendAndroid)

    gdxNativeClassifiers.forEach { (abi, nativeClassifier) ->
        add(
            gdxNativeConfigurations.getValue(abi).name,
            variantOf(libs.gdxPlatform) { classifier(nativeClassifier) }
        )
    }
}

tasks.matching { task ->
    task.name == "mergeDebugJniLibFolders" || task.name == "mergeReleaseJniLibFolders"
}.configureEach {
    dependsOn(stageGdxJniLibs)
}

tasks.register("imgui_basic_jni_gdx_android_gl_build") {
    group = "example-android"
    description = "Build the libGDX Android OpenGL ES JNI basic example"
    dependsOn("assembleDebug")
}

tasks.register<Exec>("imgui_basic_jni_gdx_android_gl_run") {
    group = "example-android"
    description = "Install and run the libGDX Android OpenGL ES JNI basic example"
    dependsOn("installDebug")
    commandLine(
        android.adbExecutable.absolutePath,
        "shell", "am", "start", "-n",
        "imgui.example.basic.gdx.gl/imgui.example.basic.gdx.gl.android.GdxAndroidGlActivity"
    )
}

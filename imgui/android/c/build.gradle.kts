plugins {
    alias(libs.plugins.androidLibrary)
}

val moduleName = "imgui-android-c"
val cLibsDir = "$projectDir/../../builder/build/c++/libs/android"
val stagedJniLibsDir = layout.buildDirectory.dir("generated/cJniLibs")
val androidAbis = listOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a")

val stageCJniLibs by tasks.registering(Copy::class) {
    androidAbis.forEach { abi ->
        from("$cLibsDir/$abi/teavm_c") {
            include("*.so")
            into(abi)
        }
    }
    into(stagedJniLibsDir)
}

android {
    namespace = "imgui.android.c"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
    }

    sourceSets {
        named("main") {
            jniLibs.srcDirs(stagedJniLibsDir)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.javaModern.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.javaModern.get())
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    publishing {
        singleVariant("release")
    }
}

tasks.matching { task ->
    task.name == "mergeReleaseJniLibFolders" || task.name == "mergeDebugJniLibFolders"
}.configureEach {
    dependsOn(stageCJniLibs)
}

dependencies {
    api(project(":imgui:shared:c"))
    api(libs.jParserRuntimeAndroidC)
    runtimeOnly(libs.jParserRuntimeAndroidCX86)
    runtimeOnly(libs.jParserRuntimeAndroidCX8664)
    runtimeOnly(libs.jParserRuntimeAndroidCArmeabiV7a)
    runtimeOnly(libs.jParserRuntimeAndroidCArm64V8a)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
        }
    }
}

afterEvaluate {
    publishing {
        publications.named<MavenPublication>("maven") {
            from(components["release"])
        }
    }
}

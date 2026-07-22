plugins {
    id("com.android.library")
}

val moduleName = "imgui-android-c"
group = "${LibExt.groupId}.android"
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
    compileSdk = 36

    defaultConfig {
        minSdk = 29
    }

    sourceSets {
        named("main") {
            jniLibs.srcDirs(stagedJniLibsDir)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(LibExt.javaModernTarget)
        targetCompatibility = JavaVersion.toVersion(LibExt.javaModernTarget)
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
    api("com.github.xpenatan.jParser:runtime-android-c:${LibExt.jParserVersion}")
    runtimeOnly("com.github.xpenatan.jParser:runtime-android-c_x86:${LibExt.jParserVersion}")
    runtimeOnly("com.github.xpenatan.jParser:runtime-android-c_x86_64:${LibExt.jParserVersion}")
    runtimeOnly("com.github.xpenatan.jParser:runtime-android-c_armeabi_v7a:${LibExt.jParserVersion}")
    runtimeOnly("com.github.xpenatan.jParser:runtime-android-c_arm64_v8a:${LibExt.jParserVersion}")
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

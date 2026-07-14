plugins {
    id("com.android.library")
}

val moduleName = "imgui-android"
group = "${LibExt.groupId}.android"

android {
    namespace = "imgui"
    compileSdk = 36

    defaultConfig {
        minSdk = 29
    }

    sourceSets {
        named("main") {
            jniLibs.srcDirs("$projectDir/../../builder/build/c++/libs/android")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(LibExt.javaMainTarget)
        targetCompatibility = JavaVersion.toVersion(LibExt.javaMainTarget)
    }
}

dependencies {
    api(project(":imgui:shared:jni"))
    api("com.github.xpenatan.jParser:api-core:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:loader-core:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:runtime-jni:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:runtime-android:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:runtime-android_x86:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:runtime-android_x86_64:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:runtime-android_armeabi_v7a:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:runtime-android_arm64_v8a:${LibExt.jParserVersion}")
}

tasks.named("preBuild") {
    dependsOn(":imgui:builder:jParser_build_android_jni")
}

tasks.named("clean") {
    doFirst {
        val srcPath = "$projectDir/src/main/java"
        project.delete(files(srcPath))
    }
}

// TODO Uncomment when android is ready
//publishing {
//    publications {
//        create<MavenPublication>("maven") {
//            artifactId = moduleName
//            groupId = LibExt.groupId
//            version = LibExt.libVersion
//            afterEvaluate {
//                artifact(tasks.named("bundleReleaseAar"))
//            }
//        }
//    }
//}

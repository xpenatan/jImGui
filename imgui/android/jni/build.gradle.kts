plugins {
    alias(libs.plugins.androidLibrary)
}

val moduleName = "imgui-android"

android {
    namespace = "imgui"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
    }

    sourceSets {
        named("main") {
            jniLibs.directories.add("$projectDir/../../builder/build/c++/libs/android")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
    }
}

dependencies {
    api(project(":imgui:shared:jni"))
    api(libs.bundles.jParserAndroidJniArtifacts)
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
//            afterEvaluate {
//                artifact(tasks.named("bundleReleaseAar"))
//            }
//        }
//    }
//}

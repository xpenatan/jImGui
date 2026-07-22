plugins {
    id("java")
    id("org.jetbrains.kotlin.android") version "2.2.21" apply false
}

buildscript {
    repositories {
        mavenCentral()
        google()
    }

    val kotlinVersion = "2.1.10"

    dependencies {
        classpath("com.android.tools.build:gradle:8.12.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

allprojects  {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven { url = uri("https://central.sonatype.com/repository/maven-snapshots/") }
        maven { url = uri("https://jitpack.io") }

        maven {
            url = uri("http://teavm.org/maven/repository/")
            isAllowInsecureProtocol = true
        }
    }

    configurations.configureEach {
        // Check for updates every sync
        resolutionStrategy.cacheChangingModulesFor(0, "seconds")
        resolutionStrategy.eachDependency {
            if(requested.group == "com.github.xpenatan.jParser") {
                useVersion(LibExt.jParserVersion)
                because("jImGui builds against one jParser version across runtime, generator, and plugin artifacts")
            }
            else if(requested.group == "com.github.xpenatan.jWebGPU") {
                useVersion(LibExt.jWebGPUVersion)
                because("GDX and FDX WebGPU backends must use one generated jWebGPU API")
            }
        }
//        resolutionStrategy {
//            force("com.github.xpenatan.jWebGPU:webgpu-core:-SNAPSHOT")
//            force("com.github.xpenatan.jWebGPU:webgpu-jni:-SNAPSHOT")
//            force("com.github.xpenatan.jWebGPU:webgpu-ffm:-SNAPSHOT")
//            force("com.github.xpenatan.jWebGPU:webgpu-web:-SNAPSHOT")
//        }
    }
}

tasks.register("download_all_sources") {
    group = "imgui"
    description = "Download all sources"

    val source1 = ":imgui:download:imgui_download_source"
    val source2 = ":extensions:ImGuiColorTextEdit:textedit-build:download_source"
    val source3 = ":extensions:imgui-node-editor:nodeeditor-build:download_source"

    val list = listOf(source1, source2, source3)
    dependsOn(list)

    tasks.findByPath(source2)!!.mustRunAfter(source1)
    tasks.findByPath(source3)!!.mustRunAfter(source2)
}

apply(plugin = "publish")

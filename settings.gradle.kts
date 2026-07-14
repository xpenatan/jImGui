pluginManagement {
    val jParserPluginVersion = "-SNAPSHOT"

    resolutionStrategy {
        eachPlugin {
            if(requested.id.id == "com.github.xpenatan.jparser") {
                useModule("com.github.xpenatan.jParser:jparser-gradle-plugin:$jParserPluginVersion")
            }
        }
    }

    plugins {
        id("com.github.xpenatan.jparser") version jParserPluginVersion
    }

    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven {
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
        }
        gradlePluginPortal()
        maven {
            url = uri("http://teavm.org/maven/repository/")
            isAllowInsecureProtocol = true
        }
    }
}

rootProject.name = "jImGui"

// Core
include(":imgui:builder")
include(":imgui:download")
include(":imgui:base")
include(":imgui:core")
include(":imgui:shared:jni")
include(":imgui:shared:c")
include(":imgui:desktop:jni")
include(":imgui:desktop:ffm")
include(":imgui:desktop:c")
include(":imgui:web:wasm")
include(":imgui:android:jni")
include(":imgui:android:c")

// Backend implementations
include(":backends:gdx:gdx-shared-impl")
include(":backends:gdx:gdx-gl-impl")
include(":backends:gdx:gdx-gl-lwjgl3-impl")
include(":backends:gdx:gdx-wgpu-impl")
include(":backends:fdx:fdx-impl")

// Examples
include(":examples:basic:base")
include(":examples:basic:core")
include(":examples:basic:fdx")
include(":examples:basic:gdx")
include(":examples:basic:platform:desktop-fdx-jni")
include(":examples:basic:platform:desktop-fdx-ffm")
include(":examples:basic:platform:desktop-gdx-gl-jni")
include(":examples:basic:platform:desktop-gdx-gl-ffm")
include(":examples:basic:platform:desktop-gdx-gl-c")
include(":examples:basic:platform:desktop-gdx-wgpu-jni")
include(":examples:basic:platform:desktop-gdx-wgpu-ffm")
include(":examples:basic:platform:web-fdx")
include(":examples:basic:platform:web-gdx-gl")
include(":examples:basic:platform:web-gdx-wgpu")
include(":examples:basic:platform:android-fdx-jni")
include(":examples:basic:platform:android-gdx-gl-jni")
include(":examples:basic:platform:android-gdx-wgpu-jni")
include(":examples:imlayout:core")
include(":examples:imlayout:fdx")
include(":examples:imlayout:platform:desktop")
include(":examples:imlayout:platform:desktop-gdx-gl-c")
include(":examples:imlayout:platform:teavm")
include(":examples:ImGuiColorTextEdit:core")
include(":examples:ImGuiColorTextEdit:fdx")
include(":examples:ImGuiColorTextEdit:platform:desktop")
include(":examples:ImGuiColorTextEdit:platform:desktop-gdx-gl-c")
include(":examples:ImGuiColorTextEdit:platform:teavm")
include(":examples:imgui-node-editor:core")
include(":examples:imgui-node-editor:fdx")
include(":examples:imgui-node-editor:platform:desktop")
include(":examples:imgui-node-editor:platform:desktop-gdx-gl-c")
include(":examples:imgui-node-editor:platform:teavm")

// Extension ImLayout
include(":extensions:imlayout:imlayout-build")
include(":extensions:imlayout:imlayout-base")
include(":extensions:imlayout:imlayout-core")
include(":extensions:imlayout:imlayout-jni")
include(":extensions:imlayout:imlayout-ffm")
include(":extensions:imlayout:imlayout-web")
include(":extensions:imlayout:imlayout-c")

// Extension ImGuiColorTextEdit
include(":extensions:ImGuiColorTextEdit:textedit-build")
include(":extensions:ImGuiColorTextEdit:textedit-base")
include(":extensions:ImGuiColorTextEdit:textedit-core")
include(":extensions:ImGuiColorTextEdit:textedit-jni")
include(":extensions:ImGuiColorTextEdit:textedit-ffm")
include(":extensions:ImGuiColorTextEdit:textedit-web")
include(":extensions:ImGuiColorTextEdit:textedit-c")

// Extension imgui-node-editor
include(":extensions:imgui-node-editor:nodeeditor-build")
include(":extensions:imgui-node-editor:nodeeditor-base")
include(":extensions:imgui-node-editor:nodeeditor-core")
include(":extensions:imgui-node-editor:nodeeditor-jni")
include(":extensions:imgui-node-editor:nodeeditor-ffm")
include(":extensions:imgui-node-editor:nodeeditor-web")
include(":extensions:imgui-node-editor:nodeeditor-c")

//includeBuild("E:\\Dev\\Projects\\java\\jParser") {
//    dependencySubstitution {
//        substitute(module("com.github.xpenatan.jParser:gen-build")).using(project(":jParser:gen:gen-build"))
//        substitute(module("com.github.xpenatan.jParser:gen-build-tool")).using(project(":jParser:gen:gen-build-tool"))
//        substitute(module("com.github.xpenatan.jParser:gen-core")).using(project(":jParser:gen:gen-core"))
//        substitute(module("com.github.xpenatan.jParser:gen-jni")).using(project(":jParser:gen:gen-jni"))
//        substitute(module("com.github.xpenatan.jParser:gen-ffm")).using(project(":jParser:gen:gen-ffm"))
//        substitute(module("com.github.xpenatan.jParser:gen-idl")).using(project(":jParser:gen:gen-idl"))
//        substitute(module("com.github.xpenatan.jParser:gen-web")).using(project(":jParser:gen:gen-web"))
//        substitute(module("com.github.xpenatan.jParser:api-core")).using(project(":jParser:api:api-core"))
//        substitute(module("com.github.xpenatan.jParser:api-web")).using(project(":jParser:api:api-web"))
//        substitute(module("com.github.xpenatan.jParser:loader-core")).using(project(":jParser:loader:loader-core"))
//        substitute(module("com.github.xpenatan.jParser:loader-web")).using(project(":jParser:loader:loader-web"))
//        substitute(module("com.github.xpenatan.jParser:runtime-base")).using(project(":jParser:runtime:runtime-base"))
//        substitute(module("com.github.xpenatan.jParser:runtime-core")).using(project(":jParser:runtime:runtime-core"))
//        substitute(module("com.github.xpenatan.jParser:runtime-web")).using(project(":jParser:runtime:runtime-web"))
//        substitute(module("com.github.xpenatan.jParser:runtime-jni")).using(project(":jParser:runtime:runtime-jni"))
//        substitute(module("com.github.xpenatan.jParser:runtime-ffm")).using(project(":jParser:runtime:runtime-ffm"))
////        substitute(module("com.github.xpenatan.jParser:runtime-android")).using(project(":jParser:runtime:runtime-android"))
//    }
//}

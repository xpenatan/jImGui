# jImGui

![Build](https://github.com/xpenatan/jImGui/actions/workflows/snapshot.yml/badge.svg)

[![Maven Central Version](https://img.shields.io/maven-central/v/com.github.xpenatan.jImGui/imgui-core)](https://central.sonatype.com/namespace/com.github.xpenatan.jImGui)
[![Static Badge](https://img.shields.io/badge/snapshot---SNAPSHOT-red)](https://central.sonatype.com/service/rest/repository/browse/maven-snapshots/com/github/xpenatan/jImGui/)

jImGui is a java binding for C++ [dear-imgui](https://github.com/ocornut/imgui). <br>
It uses webidl file to generate java methods with the help of [jParser](https://github.com/xpenatan/jParser). <br>
It's meant to be small and 1-1 to C++. ImGui::Begin() is ImGui.Begin() and so on.

<p align="center"><img src="https://i.imgur.com/rXk4Aq0.gif"/></p>

## Supported extensions:
[imgui-node-editor](https://github.com/thedmd/imgui-node-editor) <br>
[ImGuiColorTextEdit](https://github.com/santaclose/ImGuiColorTextEdit/) <br>
[ImLayout](https://github.com/xpenatan/jImGui/tree/master/extensions/imlayout) <br>

### ImGui current state:

| Emscripten | Windows | Linux | Mac | Android | iOS |
|:----------:|:-------:|:-----:|:---:|:-------:|:---:|
|     ✅      | ✅ | ✅ |  ✅  | ⚠️ | ❌ |

* ✅: Have a working build.
* ⚠️: Have a working build, but it's not ready yet.
* ❌: Build not ready.

Note: 
```
* Only snapshot builds are currently available. 
* There are 2 ImGui builds. The first contains ImGui only. The second (Ext) contains ImGui with extensions. 
```

## Setup

    jImGuiVersion = "-SNAPSHOT"

```groovy
// Add repository to Root gradle
repositories {
    mavenLocal()
    mavenCentral()
    maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
    maven { url "https://oss.sonatype.org/content/repositories/releases/" }
}
```

### Core module
```groovy
dependencies {
    implementation("com.github.xpenatan.jImGui:imgui-core:$project.jImGuiVersion")
    implementation("com.github.xpenatan.jImGui:fdx-impl:$project.jImGuiVersion")

    // Extensions
    implementation "com.github.xpenatan.jImGui:imlayout-core:$project.jImGuiVersion"
    implementation "com.github.xpenatan.jImGui:textedit-core:$project.jImGuiVersion"
    implementation "com.github.xpenatan.jImGui:nodeeditor-core:$project.jImGuiVersion"
}
```

### Desktop module
```groovy
dependencies {
    implementation("com.github.xpenatan.jImGui:imgui-jni:$project.jImGuiVersion")
    implementation("com.github.xpenatan.jImGui:imgui-ffm:$project.jImGuiVersion")

    // Extensions
    implementation "com.github.xpenatan.jImGui:imlayout-jni:$project.jImGuiVersion"
    implementation "com.github.xpenatan.jImGui:imlayout-ffm:$project.jImGuiVersion"
    implementation "com.github.xpenatan.jImGui:textedit-jni:$project.jImGuiVersion"
    implementation "com.github.xpenatan.jImGui:textedit-ffm:$project.jImGuiVersion"
    implementation "com.github.xpenatan.jImGui:nodeeditor-jni:$project.jImGuiVersion"
    implementation "com.github.xpenatan.jImGui:nodeeditor-ffm:$project.jImGuiVersion"
}
```

### TeaVM module
```groovy
dependencies {
    implementation("com.github.xpenatan.jImGui:imgui-web:$project.jImGuiVersion")

    // Extensions
    implementation "com.github.xpenatan.jImGui:imlayout-web:$project.jImGuiVersion"
    implementation "com.github.xpenatan.jImGui:textedit-web:$project.jImGuiVersion"
    implementation "com.github.xpenatan.jImGui:nodeeditor-web:$project.jImGuiVersion"
}
```

## Build source

* Requirements: Java 11, mingw64 and emscripten
* Windows only for now.

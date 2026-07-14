plugins {
    id("java")
    id("maven-publish")
    id("signing")
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

val generatedBindingModules = mapOf(
    ":imgui:builder:jParser_generate" to listOf(
        ":imgui:base",
        ":imgui:core",
        ":imgui:shared:jni",
        ":imgui:shared:c",
        ":imgui:desktop:ffm",
        ":imgui:web:wasm",
        ":imgui:android:jni"
    ),
    ":extensions:imlayout:imlayout-build:jParser_generate" to listOf(
        ":extensions:imlayout:imlayout-base",
        ":extensions:imlayout:imlayout-core",
        ":extensions:imlayout:imlayout-jni",
        ":extensions:imlayout:imlayout-ffm",
        ":extensions:imlayout:imlayout-web",
        ":extensions:imlayout:imlayout-c"
    ),
    ":extensions:imgui-node-editor:nodeeditor-build:jParser_generate" to listOf(
        ":extensions:imgui-node-editor:nodeeditor-base",
        ":extensions:imgui-node-editor:nodeeditor-core",
        ":extensions:imgui-node-editor:nodeeditor-jni",
        ":extensions:imgui-node-editor:nodeeditor-ffm",
        ":extensions:imgui-node-editor:nodeeditor-web",
        ":extensions:imgui-node-editor:nodeeditor-c"
    ),
    ":extensions:ImGuiColorTextEdit:textedit-build:jParser_generate" to listOf(
        ":extensions:ImGuiColorTextEdit:textedit-base",
        ":extensions:ImGuiColorTextEdit:textedit-core",
        ":extensions:ImGuiColorTextEdit:textedit-jni",
        ":extensions:ImGuiColorTextEdit:textedit-ffm",
        ":extensions:ImGuiColorTextEdit:textedit-web",
        ":extensions:ImGuiColorTextEdit:textedit-c"
    )
)

data class HostJParserTargets(val jni: String, val ffm: String, val c: String)

val currentHostJParserTargets = run {
    val osName = System.getProperty("os.name").lowercase()
    val osArch = System.getProperty("os.arch").lowercase()
    when {
        osName.contains("windows") -> HostJParserTargets("windows64_jni", "windows64_ffm", "windows64_teavm_c")
        osName.contains("linux") -> HostJParserTargets("linux64_jni", "linux64_ffm", "linux64_teavm_c")
        osName.contains("mac") && (osArch.contains("aarch64") || osArch.contains("arm64")) ->
            HostJParserTargets("macArm_jni", "macArm_ffm", "macArm_teavm_c")
        osName.contains("mac") -> HostJParserTargets("mac64_jni", "mac64_ffm", "mac64_teavm_c")
        else -> null
    }
}

data class NativeBindingConsumer(val modulePath: String, val taskName: String, val builderTask: String)

val nativeBindingConsumers = currentHostJParserTargets?.let { host ->
    listOf(
        NativeBindingConsumer(":imgui:desktop:jni", "jar", ":imgui:builder:jParser_build_${host.jni}"),
        NativeBindingConsumer(":imgui:desktop:ffm", "jar", ":imgui:builder:jParser_build_${host.ffm}"),
        NativeBindingConsumer(":imgui:desktop:c", "jar", ":imgui:builder:jParser_build_${host.c}"),
        NativeBindingConsumer(":extensions:imlayout:imlayout-jni", "jar",
            ":extensions:imlayout:imlayout-build:jParser_build_${host.jni}"),
        NativeBindingConsumer(":extensions:imlayout:imlayout-ffm", "jar",
            ":extensions:imlayout:imlayout-build:jParser_build_${host.ffm}"),
        NativeBindingConsumer(":extensions:imlayout:imlayout-c", "jar",
            ":extensions:imlayout:imlayout-build:jParser_build_${host.c}"),
        NativeBindingConsumer(":extensions:imgui-node-editor:nodeeditor-jni", "jar",
            ":extensions:imgui-node-editor:nodeeditor-build:jParser_build_${host.jni}"),
        NativeBindingConsumer(":extensions:imgui-node-editor:nodeeditor-ffm", "jar",
            ":extensions:imgui-node-editor:nodeeditor-build:jParser_build_${host.ffm}"),
        NativeBindingConsumer(":extensions:imgui-node-editor:nodeeditor-c", "jar",
            ":extensions:imgui-node-editor:nodeeditor-build:jParser_build_${host.c}"),
        NativeBindingConsumer(":extensions:ImGuiColorTextEdit:textedit-jni", "jar",
            ":extensions:ImGuiColorTextEdit:textedit-build:jParser_build_${host.jni}"),
        NativeBindingConsumer(":extensions:ImGuiColorTextEdit:textedit-ffm", "jar",
            ":extensions:ImGuiColorTextEdit:textedit-build:jParser_build_${host.ffm}"),
        NativeBindingConsumer(":extensions:ImGuiColorTextEdit:textedit-c", "jar",
            ":extensions:ImGuiColorTextEdit:textedit-build:jParser_build_${host.c}")
    )
}.orEmpty()

gradle.projectsEvaluated {
    generatedBindingModules.forEach { (generatorTask, modulePaths) ->
        modulePaths.forEach { modulePath ->
            project(modulePath).tasks.configureEach {
                val consumesGeneratedBindings = name == "compileJava"
                        || name == "processResources"
                        || name == "javadoc"
                        || name == "sourcesJar"
                        || name == "preBuild"
                        || (name.startsWith("compile") && name.endsWith("JavaWithJavac"))
                if(consumesGeneratedBindings) {
                    dependsOn(generatorTask)
                }
            }
        }
    }
    nativeBindingConsumers.forEach { consumer ->
        project(consumer.modulePath).tasks.named(consumer.taskName) {
            dependsOn(consumer.builderTask)
        }
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

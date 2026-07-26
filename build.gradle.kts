plugins {
    id("java")
    alias(libs.plugins.easyPublishing)
}

val jImGuiGroup = libs.versions.jImGuiGroup.get()
val examplesUseRepoLibs = libs.versions.useRepoLibs.get().toBoolean()
val repoLibVersion = libs.versions.repoLibVersion.get()

extra["examplesUseRepoLibs"] = examplesUseRepoLibs

val publishingModules = linkedMapOf(
    ":backends:gdx:gdx-shared-impl" to "gdx-shared-impl",
    ":backends:gdx:gdx-gl-impl" to "gdx-gl-impl",
    ":backends:fdx:fdx-impl" to "fdx-impl",
    ":imgui:core" to "imgui-core",
    ":imgui:shared:jni" to "imgui-shared-jni",
    ":imgui:shared:c" to "imgui-shared-c",
    ":imgui:desktop:jni" to "imgui-jni",
    ":imgui:desktop:ffm" to "imgui-ffm",
    ":imgui:desktop:c" to "imgui-c",
    ":imgui:web:wasm" to "imgui-web",
    ":imgui:android:jni" to "imgui-android",
    ":imgui:android:c" to "imgui-android-c",
    ":extensions:imlayout:imlayout-core" to "imlayout-core",
    ":extensions:imlayout:imlayout-jni" to "imlayout-jni",
    ":extensions:imlayout:imlayout-ffm" to "imlayout-ffm",
    ":extensions:imlayout:imlayout-web" to "imlayout-web",
    ":extensions:imlayout:imlayout-c" to "imlayout-c",
    ":extensions:ImGuiColorTextEdit:textedit-core" to "textedit-core",
    ":extensions:ImGuiColorTextEdit:textedit-jni" to "textedit-jni",
    ":extensions:ImGuiColorTextEdit:textedit-ffm" to "textedit-ffm",
    ":extensions:ImGuiColorTextEdit:textedit-web" to "textedit-web",
    ":extensions:ImGuiColorTextEdit:textedit-c" to "textedit-c",
    ":extensions:imgui-node-editor:nodeeditor-core" to "nodeeditor-core",
    ":extensions:imgui-node-editor:nodeeditor-jni" to "nodeeditor-jni",
    ":extensions:imgui-node-editor:nodeeditor-ffm" to "nodeeditor-ffm",
    ":extensions:imgui-node-editor:nodeeditor-web" to "nodeeditor-web",
    ":extensions:imgui-node-editor:nodeeditor-c" to "nodeeditor-c",
)

allprojects  {
    val usesExampleRepoLibs = path.startsWith(":examples:") ||
            path == ":backends:gdx:gdx-gl-lwjgl3-impl"

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
                useVersion(libs.versions.jParser.get())
                because("jImGui builds against one jParser version across runtime, generator, and plugin artifacts")
            }
            else if(requested.group == "com.github.xpenatan.jWebGPU") {
                useVersion(libs.versions.jWebGPU.get())
                because("The FDX WebGPU backend must use one generated jWebGPU API")
            }
            else if(usesExampleRepoLibs && examplesUseRepoLibs && requested.group == jImGuiGroup) {
                useVersion(repoLibVersion)
                because("The examples are configured to test published jImGui artifacts")
            }
        }
    }
}

if(examplesUseRepoLibs) {
    logger.lifecycle("jImGui examples: using Maven artifacts version $repoLibVersion")
    subprojects {
        if(path.startsWith(":examples:") || path == ":backends:gdx:gdx-gl-lwjgl3-impl") {
            configurations.configureEach {
                resolutionStrategy.dependencySubstitution {
                    publishingModules.forEach { (projectPath, artifactId) ->
                        substitute(project(projectPath))
                            .using(module("$jImGuiGroup:$artifactId:$repoLibVersion"))
                            .because("useRepoLibs is enabled")
                    }
                }
            }
        }
    }
}

easyPublishing {
    modules(publishingModules.keys.toList())

    groupId.set(libs.versions.jImGuiGroup)
    releaseVersion.set(libs.versions.jImGuiRelease)
    snapshotVersion.set(libs.versions.jImGuiSnapshot)

    snapshotRepositoryUrl.set("https://central.sonatype.com/repository/maven-snapshots/")
    releaseRepositoryUrl.set("https://central.sonatype.com")
    username.set(providers.environmentVariable("CENTRAL_PORTAL_USERNAME"))
    password.set(providers.environmentVariable("CENTRAL_PORTAL_PASSWORD"))
    signingKey.set(providers.environmentVariable("SIGNING_KEY"))
    signingPassword.set(providers.environmentVariable("SIGNING_PASSWORD"))

    pomName.set(libs.versions.jImGuiName)
    pomDescription.set("ImGui Java Bindings")
    projectUrl.set("https://github.com/xpenatan/jImGui")

    developerId.set("Xpe")
    developerName.set("Natan")

    scmUrl.set("https://github.com/xpenatan/jImGui")
    scmConnection.set("scm:git:https://github.com/xpenatan/jImGui.git")
    scmDeveloperConnection.set("scm:git:ssh://git@github.com/xpenatan/jImGui.git")
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

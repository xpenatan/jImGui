plugins {
    id("java")
    alias(libs.plugins.easyPublishing)
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
                useVersion(libs.versions.jParser.get())
                because("jImGui builds against one jParser version across runtime, generator, and plugin artifacts")
            }
            else if(requested.group == "com.github.xpenatan.jWebGPU") {
                useVersion(libs.versions.jWebGPU.get())
                because("GDX and FDX WebGPU backends must use one generated jWebGPU API")
            }
        }
    }
}

val publishingModules = listOf(
    ":backends:gdx:gdx-shared-impl",
    ":backends:gdx:gdx-gl-impl",
    ":backends:gdx:gdx-wgpu-impl",
    ":backends:fdx:fdx-impl",
    ":imgui:core",
    ":imgui:shared:jni",
    ":imgui:shared:c",
    ":imgui:desktop:jni",
    ":imgui:desktop:ffm",
    ":imgui:desktop:c",
    ":imgui:web:wasm",
    ":imgui:android:jni",
    ":imgui:android:c",
    ":extensions:imlayout:imlayout-core",
    ":extensions:imlayout:imlayout-jni",
    ":extensions:imlayout:imlayout-ffm",
    ":extensions:imlayout:imlayout-web",
    ":extensions:imlayout:imlayout-c",
    ":extensions:ImGuiColorTextEdit:textedit-core",
    ":extensions:ImGuiColorTextEdit:textedit-jni",
    ":extensions:ImGuiColorTextEdit:textedit-ffm",
    ":extensions:ImGuiColorTextEdit:textedit-web",
    ":extensions:ImGuiColorTextEdit:textedit-c",
    ":extensions:imgui-node-editor:nodeeditor-core",
    ":extensions:imgui-node-editor:nodeeditor-jni",
    ":extensions:imgui-node-editor:nodeeditor-ffm",
    ":extensions:imgui-node-editor:nodeeditor-web",
    ":extensions:imgui-node-editor:nodeeditor-c",
)

easyPublishing {
    modules(publishingModules)

    groupId.set(libs.versions.jImGuiGroup)
    releaseVersion.set(libs.versions.jImGuiRelease)
    snapshotVersion.set(libs.versions.jImGuiSnapshot)

    snapshotRepositoryUrl.set("https://central.sonatype.com/repository/maven-snapshots/")
    releaseRepositoryUrl.set("https://central.sonatype.com")
    username.set(providers.environmentVariable("CENTRAL_PORTAL_USERNAME"))
    password.set(providers.environmentVariable("CENTRAL_PORTAL_PASSWORD"))
    signingKey.set(providers.environmentVariable("SIGNING_KEY"))
    signingPassword.set(providers.environmentVariable("SIGNING_PASSWORD"))

    pomName.set("jImGui")
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

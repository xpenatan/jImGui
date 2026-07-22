plugins {
    id("com.github.xpenatan.easy-publishing")
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

LibExt.isRelease = rootProject.extra["easyPublishing.releaseRequested"] as Boolean

easyPublishing {
    modules(publishingModules)

    groupId.set(LibExt.groupId)
    releaseVersion.set(providers.gradleProperty("version"))
    snapshotVersion.set("-SNAPSHOT")

    snapshotRepositoryUrl.set("https://central.sonatype.com/repository/maven-snapshots/")
    releaseRepositoryUrl.set("https://central.sonatype.com")
    username.set(providers.environmentVariable("CENTRAL_PORTAL_USERNAME"))
    password.set(providers.environmentVariable("CENTRAL_PORTAL_PASSWORD"))
    signingKey.set(providers.environmentVariable("SIGNING_KEY"))
    signingPassword.set(providers.environmentVariable("SIGNING_PASSWORD"))

    pomName.set(LibExt.libName)
    pomDescription.set("ImGui Java Bindings")
    projectUrl.set("https://github.com/xpenatan/jImGui")

    developerId.set("Xpe")
    developerName.set("Natan")

    scmUrl.set("https://github.com/xpenatan/jImGui")
    scmConnection.set("scm:git:https://github.com/xpenatan/jImGui.git")
    scmDeveloperConnection.set("scm:git:ssh://git@github.com/xpenatan/jImGui.git")
}

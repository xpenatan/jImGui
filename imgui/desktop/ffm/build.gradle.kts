plugins {
    id("java-library")
}

val moduleName = "imgui-ffm"

val nativeRoot = file("$projectDir/../../builder/build/c++/libs")
val windowsFile = "$nativeRoot/windows/vc/ffm/imgui64.dll"
val linuxFile = "$nativeRoot/linux/ffm/libimgui64.so"
val macArmFile = "$nativeRoot/mac/arm/ffm/libimguiarm64.dylib"
val macFile = "$nativeRoot/mac/ffm/libimgui64.dylib"

val hostNativeBuild: Pair<String, String>? = run {
    val osName = System.getProperty("os.name").lowercase()
    val osArch = System.getProperty("os.arch").lowercase()
    when {
        osName.contains("windows") ->
            ":imgui:builder:jParser_build_windows64_ffm" to windowsFile
        osName.contains("linux") && (osArch.contains("amd64") || osArch.contains("x86_64")) ->
            ":imgui:builder:jParser_build_linux64_ffm" to linuxFile
        osName.contains("mac") && (osArch.contains("aarch64") || osArch.contains("arm64")) ->
            ":imgui:builder:jParser_build_macArm_ffm" to macArmFile
        osName.contains("mac") ->
            ":imgui:builder:jParser_build_mac64_ffm" to macFile
        else -> null
    }
}

dependencies {
    api(libs.bundles.jParserDesktopFFMArtifacts)
}

val platformFiles = linkedMapOf(
    "windows_x64" to windowsFile,
    "linux_x64" to linuxFile,
    "mac_x64" to macFile,
    "mac_arm64" to macArmFile
)
val packagedPlatformFiles = platformFiles.filterValues { nativeFile ->
    nativeFile == hostNativeBuild?.second || file(nativeFile).isFile
}

val nativeJars = packagedPlatformFiles.map { (platformName, nativeFile) ->
    val taskSuffix = platformName
        .split("-")
        .joinToString("") { token -> token.replaceFirstChar(Char::uppercaseChar) }

    platformName to tasks.register<Jar>("nativeJar$taskSuffix") {
        hostNativeBuild
            ?.takeIf { (_, hostNativeFile) -> hostNativeFile == nativeFile }
            ?.let { (buildTask, _) -> dependsOn(buildTask) }
        from(provider { file(nativeFile) })
        doFirst {
            check(file(nativeFile).isFile) {
                "Native library was not produced: $nativeFile"
            }
        }
        archiveClassifier.set(platformName)
    }
}

val desktopNativeJar = tasks.register<Jar>("nativeJarDesktop") {
    archiveClassifier.set("desktop")
    hostNativeBuild?.let { (buildTask, _) -> dependsOn(buildTask) }
    from(provider {
        platformFiles.values.map(::file).filter(File::isFile)
    })
    doFirst {
        hostNativeBuild?.second?.let { nativeFile ->
            check(file(nativeFile).isFile) {
                "Native library was not produced: $nativeFile"
            }
        }
    }
}

val nativeRuntime by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

val taskNames = gradle.startParameter.taskNames
fun isTaskRequested(taskName: String): Boolean {
    return taskNames.any { it == taskName || it.endsWith(":$taskName") }
}
val isPrepareDeployTask = isTaskRequested("prepareRelease") || isTaskRequested("prepareSnapshot")
val isPublishTask = taskNames.any { it.contains("publish", ignoreCase = true) }
val includeNativesInMainJar = !(isPrepareDeployTask || isPublishTask)
hostNativeBuild?.let { (buildTask, _) ->
    tasks.named("compileJava") {
        dependsOn(buildTask)
    }
    tasks.matching { it.name == "sourcesJar" }.configureEach {
        dependsOn(buildTask)
    }
}
tasks.jar {
    if(includeNativesInMainJar) {
        hostNativeBuild?.let { (buildTask, _) -> dependsOn(buildTask) }
        from(provider {
            listOf(windowsFile, linuxFile, macFile, macArmFile)
                .map(::file)
                .filter(File::isFile)
        })
    }
}

artifacts {
    nativeJars.forEach { (_, nativeJar) -> add(nativeRuntime.name, nativeJar) }
    add(nativeRuntime.name, desktopNativeJar)
}

tasks.named("clean") {
    doFirst {
        val srcPath = "$projectDir/src/main/java"
        project.delete(files(srcPath))
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }

        nativeJars.forEach { (platformName, nativeJar) ->
            create<MavenPublication>("mavenNative$platformName") {
                artifactId = "${moduleName}_${platformName}"
                artifact(nativeJar) {
                    classifier = null
                }
            }
        }

        create<MavenPublication>("mavenNativeDesktop") {
            artifactId = "${moduleName}_desktop"
            artifact(desktopNativeJar) {
                classifier = null
            }
        }
    }
}

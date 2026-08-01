plugins {
    id("java")
}

val moduleName = "imlayout-ffm"

val libDir = "${project.projectDir}/../imlayout-build/build/c++/libs"
val windowsFile = "$libDir/windows/vc/ffm/imlayout64.dll"
val linuxFile = "$libDir/linux/ffm/libimlayout64.so"
val macArmFile = "$libDir/mac/arm/ffm/libimlayoutarm64.dylib"
val macFile = "$libDir/mac/ffm/libimlayout64.dylib"

val hostNativeBuild: Pair<String, String>? = run {
    val osName = System.getProperty("os.name").lowercase()
    val osArch = System.getProperty("os.arch").lowercase()
    when {
        osName.contains("windows") ->
            ":extensions:imlayout:imlayout-build:jParser_build_windows64_ffm" to windowsFile
        osName.contains("linux") && (osArch.contains("amd64") || osArch.contains("x86_64")) ->
            ":extensions:imlayout:imlayout-build:jParser_build_linux64_ffm" to linuxFile
        osName.contains("mac") && (osArch.contains("aarch64") || osArch.contains("arm64")) ->
            ":extensions:imlayout:imlayout-build:jParser_build_macArm_ffm" to macArmFile
        osName.contains("mac") ->
            ":extensions:imlayout:imlayout-build:jParser_build_mac64_ffm" to macFile
        else -> null
    }
}

dependencies {
    implementation(project(":imgui:desktop:ffm"))
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

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaFFM.get())
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.named("clean") {
    doFirst {
        val srcPath = "$projectDir/src/main/java"
        project.delete(files(srcPath))
    }
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

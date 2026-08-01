plugins {
    id("java-library")
}

val moduleName = "imgui-ffm"

val nativeRoot = file("$projectDir/../../builder/build/c++/libs")
val windowsFile = "$nativeRoot/windows/vc/ffm/imgui64.dll"
val linuxFile = "$nativeRoot/linux/ffm/libimgui64.so"
val macArmFile = "$nativeRoot/mac/arm/ffm/libimguiarm64.dylib"
val macFile = "$nativeRoot/mac/ffm/libimgui64.dylib"

dependencies {
    api(libs.bundles.jParserDesktopFFMArtifacts)
}

val platforms: MutableMap<String, Jar.() -> Unit> = mutableMapOf()
if(file(windowsFile).exists()) {
    platforms["windows_x64"] = { from(windowsFile) }
}
if(file(linuxFile).exists()) {
    platforms["linux_x64"] = { from(linuxFile) }
}
if(file(macFile).exists()) {
    platforms["mac_x64"] = { from(macFile) }
}
if(file(macArmFile).exists()) {
    platforms["mac_arm64"] = { from(macArmFile) }
}

val nativeJars = platforms.map { (platformName, config) ->
    val taskSuffix = platformName
        .split("-")
        .joinToString("") { token -> token.replaceFirstChar(Char::uppercaseChar) }

    platformName to tasks.register<Jar>("nativeJar$taskSuffix") {
        config()
        archiveClassifier.set(platformName)
    }
}

val desktopNativeJar = tasks.register<Jar>("nativeJarDesktop") {
    archiveClassifier.set("desktop")
    platforms.values.forEach { config -> config() }
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
tasks.jar {
    if(includeNativesInMainJar) {
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

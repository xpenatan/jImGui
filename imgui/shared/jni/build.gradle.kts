plugins {
    id("java-library")
}

val moduleName = "imgui-shared-jni"
group = "${LibExt.groupId}.shared"

base {
    archivesName.set(moduleName)
}

dependencies {
    api("com.github.xpenatan.jParser:api-core:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:loader-core:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:runtime-jni:${LibExt.jParserVersion}")
    testImplementation("junit:junit:${LibExt.jUnitVersion}")
}

val hostNativeTest = run {
    val nativeRoot = file("../../builder/build/c++/libs")
    val osName = System.getProperty("os.name").lowercase()
    val osArch = System.getProperty("os.arch").lowercase()
    when {
        osName.contains("windows") -> Pair(
            ":imgui:builder:jParser_build_windows64_jni",
            file("$nativeRoot/windows/vc/jni/imgui64.dll"))
        osName.contains("linux") -> Pair(
            ":imgui:builder:jParser_build_linux64_jni",
            file("$nativeRoot/linux/jni/libimgui64.so"))
        osName.contains("mac") && (osArch.contains("aarch64") || osArch.contains("arm64")) -> Pair(
            ":imgui:builder:jParser_build_macArm_jni",
            file("$nativeRoot/mac/arm/jni/libimguiarm64.dylib"))
        osName.contains("mac") -> Pair(
            ":imgui:builder:jParser_build_mac64_jni",
            file("$nativeRoot/mac/jni/libimgui64.dylib"))
        else -> null
    }
}

tasks.withType<Test>().configureEach {
    hostNativeTest?.let { (imguiBuild, library) ->
        dependsOn(imguiBuild)
        systemProperty("jimgui.test.imgui.native", library.absolutePath)
    }
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src/main/java"))
    }
}

tasks.named("clean") {
    doFirst {
        val srcPath = "$projectDir/src/main/java"
        project.delete(files(srcPath))
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaMainTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaMainTarget)
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
    }
}

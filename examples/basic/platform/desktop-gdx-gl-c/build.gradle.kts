plugins {
    id("java")
}

val imguiRuntimeProject = ":imgui:desktop:c"
val imguiSharedProject = ":imgui:shared:c"
val teaVMBuilderMainClass = "imgui.example.basic.gdx.c.ImGuiBasicTeaVMCBuilder"
val glfwBuildRoot = layout.buildDirectory.dir("dist/glfw")

val imguiRuntimeClasspath by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":examples:basic:core"))
    implementation(project(":examples:basic:gdx"))
    implementation(project(":backends:gdx:gdx-gl-impl"))
    implementation(project(imguiRuntimeProject))
    implementation("com.github.xpenatan.gdx-teavm:backend-glfw:${LibExt.gdxTeaVMVersion}")

    imguiRuntimeClasspath(project(imguiRuntimeProject))
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaWebTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaWebTarget)
}

fun currentHostImGuiCBuildTask(): String? {
    val osName = System.getProperty("os.name").lowercase()
    val osArch = System.getProperty("os.arch").lowercase()
    return when {
        osName.contains("windows") -> ":imgui:builder:jParser_build_windows64_teavm_c"
        osName.contains("linux") -> ":imgui:builder:jParser_build_linux64_teavm_c"
        osName.contains("mac") && (osArch.contains("aarch64") || osArch.contains("arm64")) ->
            ":imgui:builder:jParser_build_macArm_teavm_c"
        osName.contains("mac") -> ":imgui:builder:jParser_build_mac64_teavm_c"
        else -> null
    }
}

val prepareTeaVMCBuildRoot = tasks.register("prepareTeaVMCBuildRoot") {
    group = "example-desktop-c"
    outputs.dir(glfwBuildRoot)
    outputs.upToDateWhen { false }
    doLast {
        val buildRoot = glfwBuildRoot.get().asFile
        delete(buildRoot.resolve("c"))
        delete(buildRoot.resolve("build/cmake"))
        buildRoot.mkdirs()
    }
}

fun Task.configureRuntimeInputs() {
    dependsOn("classes", prepareTeaVMCBuildRoot)
    currentHostImGuiCBuildTask()?.let { nativeBuildTask ->
        dependsOn(nativeBuildTask)
        project(imguiSharedProject).tasks.named("processResources") {
            mustRunAfter(nativeBuildTask)
        }
        project(imguiSharedProject).tasks.named("jar") {
            mustRunAfter(nativeBuildTask)
        }
        project(imguiRuntimeProject).tasks.named("jar") {
            mustRunAfter(nativeBuildTask)
        }
    }
    dependsOn("$imguiRuntimeProject:jar")
    inputs.files(imguiRuntimeClasspath)
}

fun JavaExec.configureTeaVM(action: String, buildType: String = "Debug") {
    mainClass.set(teaVMBuilderMainClass)
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = projectDir
    maxHeapSize = "2048m"
    configureRuntimeInputs()
    args(buildType, action)
}

tasks.register<JavaExec>("imgui_basic_c_gdx_desktop_gl_generate") {
    group = "example-desktop-c"
    description = "Generate the basic jImGui GDX OpenGL desktop example with TeaVM C."
    configureTeaVM("generate")
}

tasks.register<JavaExec>("imgui_basic_c_gdx_desktop_gl_build") {
    group = "example-desktop-c"
    description = "Build the basic jImGui GDX OpenGL desktop example with TeaVM C."
    configureTeaVM("build")
}

tasks.register<JavaExec>("imgui_basic_c_gdx_desktop_gl_run") {
    group = "example-desktop-c"
    description = "Build and run the basic jImGui GDX OpenGL desktop example with TeaVM C."
    configureTeaVM("run")
}

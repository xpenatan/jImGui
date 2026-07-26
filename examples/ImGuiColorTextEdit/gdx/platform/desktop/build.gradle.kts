plugins {
    id("java")
}

val useRepoLibs = rootProject.extra["examplesUseRepoLibs"] as Boolean
val imguiRuntimeProject = ":imgui:desktop:c"
val imguiSharedProject = ":imgui:shared:c"
val extensionRuntimeProject = ":extensions:ImGuiColorTextEdit:textedit-c"
val extensionBuilderProject = ":extensions:ImGuiColorTextEdit:textedit-build"
val teaVMBuilderMainClass = "imgui.example.textedit.gdx.c.TextEditTeaVMCBuilder"
val glfwBuildRoot = layout.buildDirectory.dir("dist/glfw")

val nativeRuntimeClasspath by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    implementation(libs.gdxCore)
    implementation(project(":examples:basic:gdx:core"))
    implementation(project(":examples:ImGuiColorTextEdit:core"))
    implementation(project(":backends:gdx:gdx-gl-impl"))
    implementation(project(imguiRuntimeProject))
    implementation(project(extensionRuntimeProject))
    implementation(libs.gdxTeaVMBackendGlfw)

    compileOnly(project(":imgui:core"))
    compileOnly(project(":extensions:ImGuiColorTextEdit:textedit-core"))
    nativeRuntimeClasspath(project(extensionRuntimeProject))
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
}

fun currentHostExtensionCBuildTask(): String? {
    val osName = System.getProperty("os.name").lowercase()
    val osArch = System.getProperty("os.arch").lowercase()
    return when {
        osName.contains("windows") -> "$extensionBuilderProject:jParser_build_windows64_teavm_c"
        osName.contains("linux") -> "$extensionBuilderProject:jParser_build_linux64_teavm_c"
        osName.contains("mac") && (osArch.contains("aarch64") || osArch.contains("arm64")) ->
            "$extensionBuilderProject:jParser_build_macArm_teavm_c"
        osName.contains("mac") -> "$extensionBuilderProject:jParser_build_mac64_teavm_c"
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
    if(!useRepoLibs) {
        currentHostExtensionCBuildTask()?.let { nativeBuildTask ->
            dependsOn(nativeBuildTask)
            listOf(imguiSharedProject, imguiRuntimeProject, extensionRuntimeProject).forEach { projectPath ->
                project(projectPath).tasks.matching { it.name == "processResources" || it.name == "jar" }.configureEach {
                    mustRunAfter(nativeBuildTask)
                }
            }
        }
        dependsOn("$imguiRuntimeProject:jar", "$extensionRuntimeProject:jar")
    }
    inputs.files(nativeRuntimeClasspath)
}

fun JavaExec.configureTeaVM(action: String, buildType: String = "Debug") {
    mainClass.set(teaVMBuilderMainClass)
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = projectDir
    maxHeapSize = "2048m"
    configureRuntimeInputs()
    args(buildType, action)
}

tasks.register<JavaExec>("imgui_textedit_c_gdx_desktop_gl_generate") {
    group = "example-desktop-c"
    description = "Generate the ImGuiColorTextEdit GDX OpenGL desktop example with TeaVM C."
    configureTeaVM("generate")
}

tasks.register<JavaExec>("imgui_textedit_c_gdx_desktop_gl_build") {
    group = "example-desktop-c"
    description = "Build the ImGuiColorTextEdit GDX OpenGL desktop example with TeaVM C."
    configureTeaVM("build")
}

tasks.register<JavaExec>("imgui_textedit_c_gdx_desktop_gl_run") {
    group = "example-desktop-c"
    description = "Build and run the ImGuiColorTextEdit GDX OpenGL desktop example with TeaVM C."
    configureTeaVM("run")
}

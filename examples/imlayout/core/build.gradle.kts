plugins {
    id("java")
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
}

dependencies {
    implementation(project(":examples:shared"))

    if(providers.gradleProperty("useRepoLibs").map(String::toBoolean).getOrElse(false)) {
        compileOnly(libs.jImGuiImguiCore)
        compileOnly(libs.jImGuiImlayoutCore)
    }
    else {
        compileOnly(project(":imgui:core"))
        compileOnly(project(":extensions:imlayout:imlayout-core"))
    }

    implementation(libs.jParserLoaderCore)
}

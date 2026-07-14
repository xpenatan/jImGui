import java.io.File
import java.util.*

object LibExt {
    const val groupId = "com.github.xpenatan.jImGui"
    const val libName = "jImGui"
    var isRelease = false
    var libVersion: String = ""
        get() {
            return getVersion()
        }

    const val javaMainTarget = "1.8"
    const val javaWebTarget = "17"
    const val javaModernTarget = "17"
    const val javaFFMTarget = "25"

    const val jParserVersion = "1.2.3"
    const val teaVMVersion = "0.15.0"
    const val imguiVersion = "1.92.4-docking"
    const val libFdxVersion = "-SNAPSHOT"
    const val gdxVersion = "1.14.2"
    const val gdxTeaVMVersion = "-SNAPSHOT"
    const val gdxWebGPUGroup = "com.github.xpenatan.gdx-webgpu"
    const val gdxWebGPUVersion = "dev-SNAPSHOT"
    const val jWebGPUVersion = "-SNAPSHOT"

    const val jUnitVersion = "4.12"

    const val useRepoLibs = false
}

private fun getVersion(): String {
    var libVersion = "-SNAPSHOT"
    val file = File("gradle.properties")
    if(file.exists()) {
        val properties = Properties()
        properties.load(file.inputStream())
        val version = properties.getProperty("version")
        if(LibExt.isRelease) {
            libVersion = version
        }
    }
    else {
        if(LibExt.isRelease) {
            throw RuntimeException("properties should exist")
        }
    }
    return libVersion
}

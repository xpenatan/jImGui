import org.gradle.api.file.RelativePath
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption

plugins {
    id("java")
}

val buildDirFile = layout.buildDirectory.get().asFile
val imguiSourceRoot = buildDirFile.resolve("imgui-source")
val imguiArchiveFile = buildDirFile.resolve("tmp/imgui-source.zip")

tasks.register("imgui_download_source") {
    group = "imgui"
    description = "Download Dear ImGui ${LibExt.imguiVersion} source into the build directory."
    inputs.property("imguiVersion", LibExt.imguiVersion)
    outputs.dir(imguiSourceRoot)
    onlyIf {
        !imguiSourceRoot.resolve("imgui.h").isFile
    }

    doLast {
        val url = "https://github.com/ocornut/imgui/archive/v${LibExt.imguiVersion}.zip"
        println("URL: $url")
        delete(imguiSourceRoot)
        imguiArchiveFile.parentFile.mkdirs()
        URL(url).openStream().use { input ->
            Files.copy(input, imguiArchiveFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        copy {
            from(zipTree(imguiArchiveFile)) {
                eachFile {
                    val strippedSegments = relativePath.segments.drop(1)
                    if(strippedSegments.isEmpty()) {
                        exclude()
                    }
                    else {
                        relativePath = RelativePath(!isDirectory, *strippedSegments.toTypedArray())
                    }
                }
                includeEmptyDirs = false
            }
            into(imguiSourceRoot)
        }
        delete(imguiArchiveFile)
    }
}

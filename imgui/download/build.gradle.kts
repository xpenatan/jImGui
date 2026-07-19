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
    inputs.property("imguiTagObject", LibExt.imguiTagObject)
    inputs.property("imguiSourceCommit", LibExt.imguiSourceCommit)
    outputs.dir(imguiSourceRoot)

    doLast {
        // Use the immutable annotated-tag object rather than the movable tag name.
        val url = "https://github.com/ocornut/imgui/archive/${LibExt.imguiTagObject}.zip"
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

        val imguiHeader = imguiSourceRoot.resolve("imgui.h")
        val internalHeader = imguiSourceRoot.resolve("imgui_internal.h")
        check(imguiHeader.isFile && internalHeader.isFile) {
            "Dear ImGui archive ${LibExt.imguiVersion} did not contain the expected headers"
        }
        val imguiHeaderText = imguiHeader.readText()
        check(Regex("""(?m)^#define\s+IMGUI_VERSION_NUM\s+19280\s*$""").containsMatchIn(imguiHeaderText)) {
            "Expected Dear ImGui 1.92.8 headers for ${LibExt.imguiVersion}"
        }
        check(Regex("""(?m)^#define\s+IMGUI_HAS_DOCK(?:\s|$)""").containsMatchIn(imguiHeaderText)) {
            "Expected docking support in Dear ImGui ${LibExt.imguiVersion}"
        }
        imguiSourceRoot.resolve("jimgui-upstream.txt").writeText(
            "tag=v${LibExt.imguiVersion}\n" +
                "tagObject=${LibExt.imguiTagObject}\n" +
                "commit=${LibExt.imguiSourceCommit}\n" +
                "source=$url\n"
        )
    }
}

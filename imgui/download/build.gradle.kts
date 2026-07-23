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
val imguiVersion = libs.versions.dearImgui.get()
val imguiTagObject = libs.versions.dearImguiTagObject.get()
val imguiSourceCommit = libs.versions.dearImguiSourceCommit.get()
val imguiVersionNumber = libs.versions.dearImguiVersionNumber.get()
val imguiBaseVersion = imguiVersion.substringBefore("-")

tasks.register("imgui_download_source") {
    group = "imgui"
    description = "Download Dear ImGui $imguiVersion source into the build directory."
    inputs.property("imguiVersion", imguiVersion)
    inputs.property("imguiTagObject", imguiTagObject)
    inputs.property("imguiSourceCommit", imguiSourceCommit)
    outputs.dir(imguiSourceRoot)

    doLast {
        // Use the immutable annotated-tag object rather than the movable tag name.
        val url = "https://github.com/ocornut/imgui/archive/$imguiTagObject.zip"
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
            "Dear ImGui archive $imguiVersion did not contain the expected headers"
        }
        val imguiHeaderText = imguiHeader.readText()
        check(Regex("""(?m)^#define\s+IMGUI_VERSION_NUM\s+$imguiVersionNumber\s*$""").containsMatchIn(imguiHeaderText)) {
            "Expected Dear ImGui $imguiBaseVersion headers for $imguiVersion"
        }
        check(Regex("""(?m)^#define\s+IMGUI_HAS_DOCK(?:\s|$)""").containsMatchIn(imguiHeaderText)) {
            "Expected docking support in Dear ImGui $imguiVersion"
        }
        imguiSourceRoot.resolve("jimgui-upstream.txt").writeText(
            "tag=v$imguiVersion\n" +
                "tagObject=$imguiTagObject\n" +
                "commit=$imguiSourceCommit\n" +
                "source=$url\n"
        )
    }
}

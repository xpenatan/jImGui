import de.undercouch.gradle.tasks.download.Download
import java.io.File

plugins {
    alias(libs.plugins.downloadPlugin)
}

val buildDir = layout.buildDirectory.get().asFile
val zippedPath = "${buildDir}/nodeeditor.zip"
val sourcePath = "${buildDir}/nodeeditor/"
val sourceDestination = "${buildDir}/imgui-node-editor/"
val nodeEditorCommit = libs.versions.nodeEditorSourceCommit.get()

tasks.register<Download>("download_source") {
    group = "node-editor"
    description = "Download source"
    src("https://github.com/NogginBops/imgui-node-editor/archive/$nodeEditorCommit.zip")
    dest(File(zippedPath))
    doLast {
        copy {
            from(zipTree(dest))
            into(sourcePath)
        }
        delete(sourceDestination)
        copy {
            from("$sourcePath/imgui-node-editor-$nodeEditorCommit")
            into(sourceDestination)
        }
        delete(sourcePath)
        delete(zippedPath)
    }
}

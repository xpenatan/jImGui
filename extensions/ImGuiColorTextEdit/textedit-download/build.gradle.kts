import de.undercouch.gradle.tasks.download.Download
import java.io.File

plugins {
    alias(libs.plugins.downloadPlugin)
}

val buildDir = layout.buildDirectory.get().asFile
val zippedPath = "${buildDir}/text-edit.zip"
val sourcePath = "${buildDir}/text-edit/"
val sourceDestination = "${buildDir}/ImGuiColorTextEdit/"
val textEditCommit = libs.versions.textEditSourceCommit.get()

tasks.register<Download>("download_textedit_source") {
    group = "textedit"
    description = "Download source"
    src("https://github.com/goossens/ImGuiColorTextEdit/archive/$textEditCommit.zip")
    dest(File(zippedPath))
    doLast {
        copy {
            from(zipTree(dest))
            into(sourcePath)
        }
        delete(sourceDestination)
        copy {
            from("$sourcePath/ImGuiColorTextEdit-$textEditCommit")
            into(sourceDestination)
        }
        delete(sourcePath)
        delete(zippedPath)
    }
}

tasks.register("download_source") {
    group = "textedit"
    description = "Download source"
    dependsOn("download_textedit_source")
}

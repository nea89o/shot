package moe.nea.shot.plugin

import moe.nea.shot.Shots
import org.gradle.api.artifacts.transform.InputArtifact
import org.gradle.api.artifacts.transform.TransformAction
import org.gradle.api.artifacts.transform.TransformOutputs
import org.gradle.api.artifacts.transform.TransformParameters
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import java.io.FileOutputStream
import java.io.Serializable
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

abstract class ShotTransform : TransformAction<ShotTransform.Props> {
    interface Props : TransformParameters, Serializable {
        @get:Input
        var shot: Shots
    }

    @get:InputArtifact
    abstract val inputArtifact: Provider<FileSystemLocation>

    override fun transform(outputs: TransformOutputs) {
        val input = inputArtifact.get().asFile
        val output = outputs.file("${input.nameWithoutExtension}-shot.jar")
        output.parentFile.mkdirs()
        ZipFile(input).use { inputZipFile ->
            ZipOutputStream(FileOutputStream(output)).use { zipOutputStream ->
                parameters.shot.processZipFile(inputZipFile, zipOutputStream)
            }
        }
    }
}

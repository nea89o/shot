package moe.nea.shot.plugin

import moe.nea.shot.ShotParser
import moe.nea.shot.Shots
import org.gradle.api.Project
import org.gradle.api.attributes.Attribute
import java.io.File

abstract class ShotExtension(val project: Project) {
    val attribute: Attribute<String> = Attribute.of("moe.nea.shot", String::class.java)

    init {
        project.dependencies.attributesSchema.attribute(attribute)
        project.configurations.all {
            if (it.isCanBeResolved) {
                it.attributes.attribute(attribute, "identity")
            }
        }
    }

    fun shot(name: String, file: File): ShotWrapper {
        val shotParser = ShotParser()
        val shots = Shots(shotParser.parse(file.readText().lines()))
        project.dependencies.registerTransform(ShotTransform::class.java) {
            it.parameters.shot = shots
            it.from.attribute(attribute, name)
            it.to.attribute(attribute, "identity")
        }
        return ShotWrapper(attribute, name)
    }
}

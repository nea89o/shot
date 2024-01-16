package moe.nea.shot.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class ShotPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create("shots", ShotExtension::class.java, target)
    }
}
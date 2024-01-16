// SPDX-FileCopyrightText: 2024 Linnea Gräf <nea@nea.moe>
//
// SPDX-License-Identifier: CC0-1.0

plugins {
    `java-gradle-plugin`
    kotlin("jvm")
    id("com.gradle.plugin-publish") version "1.2.1"
    id("com.github.johnrengelman.shadow") version ("8.1.1")
}


repositories {
    mavenCentral()
}

val shade by configurations.creating
dependencies {
    implementation(project(":"))
    shade(project(":")) { isTransitive = false }
}
tasks.jar {
    archiveClassifier.set("thin")
}
tasks.shadowJar {
    configurations = listOf(shade)
    archiveClassifier.set("")
}
gradlePlugin {
    val shot by plugins.creating {
        id = "moe.nea.shot"
        implementationClass = "moe.nea.shot.plugin.ShotPlugin"
    }
}

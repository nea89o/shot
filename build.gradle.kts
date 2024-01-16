// SPDX-FileCopyrightText: 2024 Linnea Gräf <nea@nea.moe>
//
// SPDX-License-Identifier: CC0-1.0

plugins {
    kotlin("jvm") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
    `maven-publish`
}

allprojects {
    group = "moe.nea.shot"
    version = "1.0.0"
    repositories {
        mavenCentral()
    }
    afterEvaluate {
        kotlin {
            jvmToolchain(8)
        }
    }
}

dependencies {
    implementation("org.ow2.asm:asm:9.5")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("shots") {
            from(components["java"])
        }
    }
}


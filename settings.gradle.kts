// SPDX-FileCopyrightText: 2024 Linnea Gräf <nea@nea.moe>
//
// SPDX-License-Identifier: CC0-1.0

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "shot"

include("plugin")

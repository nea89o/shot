<!--
SPDX-FileCopyrightText: 2024 Linnea Gräf <nea@nea.moe>

SPDX-License-Identifier: CC0-1.0
-->

# SHOT - Simple JAR injections and transformations

Focus on *simple*. This does not do any code transformations, it is mostly intended for simple formatting changes.

Things you can modify right now:

- Inject Annotations


## Usage:

Create a file like so:

```
com.example.SomeClass:
    someField:
        annotate org.jetbrains.annotations.NotNull # Annotate field as @NotNull using Jetbrains annotations
        annotate java.lang.Deprecated
    someMethod():
        # Annotate a method with no arguments
        annotate org.jetbrains.annotations.NotNull # Annotate the return value as NotNull
    someMethod(java.lang.String):
        annotateParameter 0 org.jetbrains.annotations.NotNull # Annotate the first parameter as NotNull

com.example.SomeOtherClass:
    testMethod(java.lang.String[], int):
        # Reference arrays and primitives like this
        annotateParameter 0 org.jetbrains.annotations.NotNull # Annotate the first parameter as NotNull

```

In your gradle settings script, use it like this:

```kt
settings {
    pluginManagement {
        repositories {
            maven("https://repo.moe.nea/releases")
        }
    }
}
```

then in your gradle build script, use it like this (check https://repo.nea.moe/#releases/moe/nea/shot for the latest
version): 

```kt
plugins {
    id("moe.nea.shot") version "1.0.0"
}

val shot = shots.shot("identifier", project.file("shots.txt"))

dependencies {
    implementation("com.example:module:1.0.0") {
        shot.applyTo(this)
    }
}

// When using with architectury loom (needs to be *directly* after the dependencies block):
configurations.getByName("minecraftNamed").dependencies.forEach {
    shot.applyTo(it as HasConfigurableAttributes<*>)
}
```


package moe.nea.shot

import java.io.Serializable


data class ClassRef(val className: String) : Serializable {
    val path: String get() = "$nudeJvmRef.class"
    val jvmRef: String get() = "L$nudeJvmRef;"
    val nudeJvmRef: String get() = className.replace(".", "/")

    companion object {
        fun fromPath(path: String): ClassRef? {
            if (!path.endsWith(".class")) return null
            return ClassRef(path.removeSuffix(".class").replace("/", "."))
        }
    }
}
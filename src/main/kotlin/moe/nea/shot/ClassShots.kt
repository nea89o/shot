package moe.nea.shot

import java.io.Serializable

data class ClassShots(
    val annotations: List<ClassRef>,
    val methodShots: Map<MethodRef, MethodShots>,
    val fieldShots: Map<FieldRef, FieldShots>,
) : Serializable
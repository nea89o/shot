package moe.nea.shot

import java.io.Serializable

data class MethodShots(
    val annotations: List<ClassRef>,
    val parameterAnnotations: Map<Int, List<ClassRef>>,
) : Serializable
package moe.nea.shot

import java.io.Serializable

data class MethodRef(val name: String, val argumentDescriptor: String) : Serializable

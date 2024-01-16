package moe.nea.shot

import org.objectweb.asm.*

class InjectionApplicator(
    val injections: ClassShots,
    writer: ClassWriter
) : ClassVisitor(Opcodes.ASM9, writer) {

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        for (annotation in injections.annotations) {
            super.visitAnnotation(annotation.jvmRef, false).visitEnd()
        }
    }

    override fun visitField(
        access: Int,
        name: String,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        val fieldVisitor = super.visitField(access, name, descriptor, signature, value)
        val fieldShots = injections.fieldShots[FieldRef(name)] ?: return fieldVisitor
        for (annotation in fieldShots.annotations) {
            fieldVisitor.visitAnnotation(annotation.jvmRef, false)
                .visitEnd()
        }
        return fieldVisitor
    }

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val parentVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        val methodShots = injections.methodShots[MethodRef(name, descriptor.replaceAfter(")", ""))] ?: return parentVisitor
        for (injection in methodShots.annotations) {
            parentVisitor.visitAnnotation(injection.jvmRef, false)
                .visitEnd()
        }
        var maxParameterCount = 0
        for ((parameter, annotations) in methodShots.parameterAnnotations) {
            maxParameterCount = maxOf(maxParameterCount, parameter + 1)
            for (annotation in annotations) {
                parentVisitor
                    .visitParameterAnnotation(parameter, annotation.jvmRef, false)
                    .visitEnd()
            }
        }
        return object : MethodVisitor(Opcodes.ASM8, parentVisitor) {
            override fun visitAnnotableParameterCount(parameterCount: Int, visible: Boolean) {
                super.visitAnnotableParameterCount(
                    if (visible) parameterCount else maxOf(parameterCount, maxParameterCount), visible)
            }
        }
    }
}
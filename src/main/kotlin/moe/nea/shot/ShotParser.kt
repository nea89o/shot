package moe.nea.shot

import java.util.*

class ShotParser {

    private data class Builder(
        val indentation: Int,
        val handler: Handler
    )

    private interface Handler {
        fun handleLine(line: String): Handler?
    }

    private inner class MethodInjectionBuilder : Handler {
        val annotations = mutableListOf<ClassRef>()
        val parameterAnnotations = mutableMapOf<Int, MutableList<ClassRef>>()
        override fun handleLine(line: String): Handler? {
            if (line.startsWith("annotate ")) {
                annotations.add(ClassRef(line.substring("annotate ".length).trim()))
                return null
            }
            if (line.startsWith("annotateParameter ")) {
                val (idx, name) = line.substring("annotateParameter ".length).split(" ")
                parameterAnnotations.getOrPut(idx.toInt()) { mutableListOf() }.add(ClassRef(name))
                return null
            }
            error("Unknown directive")
        }
    }

    private inner class FieldInjectionBuilder : Handler {
        val annotations = mutableListOf<ClassRef>()
        override fun handleLine(line: String): Handler? {
            if (line.startsWith("annotate ")) {
                annotations.add(ClassRef(line.substring("annotate ".length).trim()))
                return null
            }
            error("Unknown directive")
        }
    }

    private inner class ClassInjectionBuilder : Handler {
        val methodHandlers = mutableMapOf<MethodRef, MethodInjectionBuilder>()
        val fieldHandlers = mutableMapOf<FieldRef, FieldInjectionBuilder>()
        val annotations = mutableListOf<ClassRef>()
        override fun handleLine(line: String): Handler? {
            if (line.endsWith(":")) {
                val data = line.substring(0, line.length - 1).trim()
                if (data.contains("(")) {
                    return methodHandlers.getOrPut(parseMethod(data)) { MethodInjectionBuilder() }
                }
                if (" " !in data) {
                    return fieldHandlers.getOrPut(FieldRef(data)) { FieldInjectionBuilder() }
                }
                error("Unknown condition")
            }
            if (line.startsWith("annotate ")) {
                annotations.add(ClassRef(line.substring("annotate ".length).trim()))
                return null
            }
            error("Unknown line $line")
        }

        private fun parseMethod(data: String): MethodRef {
            require(data.endsWith(")"))
            val name = data.substringBefore("(")
            val parameterDescriptor = data.substringAfter("(").dropLast(1)
            val parameters = parameterDescriptor.split(",").filter { it.isNotBlank() }.map {
                mapTypeToDescriptor(false, it.trim())
            }
            return MethodRef(name, parameters.joinToString("", "(", ")"))
        }
    }

    private fun mapTypeToDescriptor(allowVoid: Boolean, name: String): String {
        require(" " !in name)
        if (name.endsWith("[]")) {
            return "[" + mapTypeToDescriptor(allowVoid, name.substring(0, name.length - 2))
        }
        return when (name) {
            "void" -> if (allowVoid) "V" else error("Void not allowed")
            "boolean" -> "Z"
            "byte" -> "B"
            "int" -> "I"
            "double" -> "D"
            "long" -> "J"
            "short" -> "S"
            "float" -> "F"
            "char" -> "C"
            else -> "L" + name.replace(".", "/") + ";"
        }
    }

    private inner class Root : Handler {
        val map = mutableMapOf<ClassRef, ClassInjectionBuilder>()
        override fun handleLine(line: String): Handler? {
            require(line.endsWith(":"))
            val className = ClassRef(line.substring(0, line.length - 1).trim())
            return map.getOrPut(className) { ClassInjectionBuilder() }
        }
    }

    private val root = Root()
    private val indentations = Stack<Builder>().also {
        it.add(Builder(0, root))
    }
    private var nextHandler: Handler? = null

    fun parse(lines: Iterable<String>): Map<ClassRef, ClassShots> {
        lines.forEach { parseLine(it) }
        return root.map.mapValues {
            ClassShots(
                it.value.annotations,
                it.value.methodHandlers.mapValues {
                    MethodShots(
                        it.value.annotations,
                        it.value.parameterAnnotations
                    )
                },
                it.value.fieldHandlers.mapValues {
                    FieldShots(
                        it.value.annotations
                    )
                }
            )
        }
    }

    fun parseLine(line: String) {
        val indentation = line.takeWhile { it == ' ' }.length

        if (indentation > indentations.peek().indentation) {
            indentations.push(Builder(indentation, nextHandler ?: error("Illegal increase in indentation")))
            nextHandler = null
        }
        while (indentation < indentations.peek().indentation) {
            indentations.pop()
        }

        val nonCommentedLine = line.substringBefore('#')
        if (nonCommentedLine.isBlank()) {
            return
        }
        nextHandler = indentations.peek().handler.handleLine(line.trim())
    }

}
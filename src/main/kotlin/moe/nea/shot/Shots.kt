package moe.nea.shot

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.Serializable
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

data class Shots(
    val injections: Map<ClassRef, ClassShots>,
) : Serializable {

    fun processEntry(toInject: ClassShots, bytes: ByteArray): ByteArray {
        val classReader = ClassReader(bytes)
        val classWriter = ClassWriter(0)
        val visitor = InjectionApplicator(toInject, classWriter)
        classReader.accept(visitor, 0)
        return classWriter.toByteArray()
    }

    fun processZipFile(input: ZipFile, output: ZipOutputStream) {
        for (entry in input.entries()) {
            val classRef = ClassRef.fromPath(entry.name)
            val toInject = classRef?.let(injections::get)
            output.putNextEntry(entry)
            if (toInject == null) {
                input.getInputStream(entry).copyTo(output)
            } else {
                val bytes = input.getInputStream(entry).use { it.readBytes() }
                val modifiedBytes = processEntry(toInject, bytes)
                output.write(modifiedBytes)
            }
        }
    }


    /**
     * Run on an extracted zip, (or alternatively, a jar file systems root path)
     */
    fun process(sourceFiles: Path, targetFiles: Path) {
        for ((classRef, toInject) in injections.entries) {
            val classSource = sourceFiles.resolve(classRef.path)
            val classTarget = targetFiles.resolve(classRef.path)
            if (!classSource.exists()) {
                continue
            }
            classTarget.parent.createDirectories()
            Files.write(classTarget, processEntry(toInject, Files.readAllBytes(classSource)))
        }
    }
}
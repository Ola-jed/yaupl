import java.io.File
import java.io.PrintWriter

object GenerateAst {
    fun defineAst(outputDir: String, baseName: String, types: List<String>) {
        val path = "$outputDir/$baseName.kt"
        val file = File(path)
        file.parentFile.mkdirs()
        if (!file.exists()) {
            file.createNewFile()
        }

        val writer = PrintWriter(path, "UTF-8")
        writer.println("package ast\n")
        writer.println("import core.scanner.Token\n")
        writer.println("sealed class $baseName {")
        defineVisitor(writer, baseName, types)
        writer.println("    abstract fun <R> accept(visitor: Visitor<R>): R\n")

        for (type in types) {
            val components = type.split(":")
            val className = components[0].trim()
            val fieldList = components.getOrNull(1)?.trim()
            defineType(writer, className, baseName, fieldList)
        }

        writer.println("}")
        writer.close()
    }

    private fun defineType(writer: PrintWriter, className: String, baseName: String, fieldList: String?) {
        if (fieldList != null) {
            writer.println("    class $className(")
            val fields = fieldList.split(", ")
            for (field in fields) {
                val fieldData = field.split(" ")
                writer.println("        val ${fieldData[1]}: ${fieldData[0]},")
            }
            writer.println("    ) : $baseName() {")
        } else {
            writer.println("    class $className (): $baseName() {")
        }

        writer.println("        override fun <R> accept(visitor: Visitor<R>) = visitor.visit$className$baseName(this)")
        writer.println("    }\n")
    }

    private fun defineVisitor(writer: PrintWriter, baseName: String, types: List<String>) {
        writer.println("    interface Visitor<R> {")

        for (type in types) {
            val typeName = type.split(":")[0].trim()
            writer.println("        fun visit$typeName$baseName(${baseName.lowercase()}: $typeName): R")
        }

        writer.println("    }")
        writer.println()
    }
}
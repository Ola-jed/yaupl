package tools

import tools.GenerateAst.defineAst
import java.io.File
import java.io.PrintWriter
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 1) {
        System.err.println("Usage: generate_ast <output directory>")
        exitProcess(64)
    }

    val outputDir = args[0]
    defineAst(
        outputDir, "Expr", listOf(
            "Binary : Expr left, Token operator, Expr right",
            "Grouping : Expr expression",
            "Literal : Any value",
            "Unary : Token operator, Expr right"
        )
    )
}

object GenerateAst {
    fun defineAst(outputDir: String, baseName: String, types: List<String>) {
        val path = "$outputDir/$baseName.kt"
        val file = File(path)
        file.parentFile.mkdirs()
        if (!file.exists()) {
            file.createNewFile()
        }

        val writer = PrintWriter(path, "UTF-8")
        writer.println("import core.Token\n")
        writer.println("sealed class $baseName {")
        for (type in types) {
            val className = type.split(":")[0].trim()
            val fields = type.split(":")[1].trim()
            defineType(writer, className, fields)
        }
        writer.println("}")
        writer.close()
    }

    private fun defineType(writer: PrintWriter, className: String, fieldList: String) {
        writer.println("    class $className (")
        val fields = fieldList.split(", ")
        for (field in fields) {
            val fieldData = field.split(" ")
            writer.println("        val ${fieldData[1]} : ${fieldData[0]},")
        }
        writer.println("    )\n")
    }
}


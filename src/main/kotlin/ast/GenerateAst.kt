package ast

import ast.GenerateAst.defineAst
import java.io.File
import java.io.PrintWriter
import kotlin.system.exitProcess

// Tool to generate the needed classes for the ast
fun main(args: Array<String>) {
    if (args.size != 1) {
        System.err.println("Usage: generate_ast <output directory>")
        exitProcess(64)
    }

    val outputDir = args[0]
    defineAst(
        outputDir, "Expr", listOf(
            "Assign : Token name, Expr value",
            "Binary : Expr left, Token operator, Expr right",
            "Grouping : Expr expression",
            "Literal : Any? value",
            "Logical : Expr left, Token operator, Expr right",
            "Unary : Token operator, Expr right",
            "Variable : Token name"
        )
    )

    defineAst(
        outputDir, "Stmt", listOf(
            "Block : List<Stmt> statements",
            "Expression : Expr expression",
            "If : Expr condition, Stmt thenBranch, Stmt? elseBranch",
            "Print : Expr expression",
            "VariableDeclaration : Token name, Expr initializer",
            "While : Expr condition, Stmt body"
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
        writer.println("package ast\n")
        writer.println("import core.scanner.Token\n")
        writer.println("sealed class $baseName {")
        defineVisitor(writer, baseName, types)

        writer.println("    abstract fun <R> accept(visitor: Visitor<R>) : R\n")

        for (type in types) {
            val className = type.split(":")[0].trim()
            val fields = type.split(":")[1].trim()
            defineType(writer, className, baseName, fields)
        }
        writer.println("}")
        writer.close()
    }

    private fun defineType(writer: PrintWriter, className: String, baseName: String, fieldList: String) {
        writer.println("    class $className (")
        val fields = fieldList.split(", ")
        for (field in fields) {
            val fieldData = field.split(" ")
            writer.println("        val ${fieldData[1]} : ${fieldData[0]},")
        }
        writer.println("    ) : $baseName() {")
        writer.println("        override fun<R> accept(visitor: Visitor<R>) = visitor.visit$className$baseName(this)")
        writer.println("    }\n")
    }

    private fun defineVisitor(writer: PrintWriter, baseName: String, types: List<String>) {
        writer.println("    interface Visitor<R> {")

        for (type in types) {
            val typeName = type.split(":")[0].trim()
            writer.println("        fun visit$typeName$baseName(${baseName.lowercase()}: $typeName) : R")
        }

        writer.println("    }")
        writer.println()
    }
}


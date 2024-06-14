package ast

import ast.GenerateAst.defineAst
import java.io.File
import java.io.PrintWriter

// Tool to generate the needed classes for the ast
// To add a new expression or statement type, just add it to the list and run (change thed output dir if needed)
fun main() {
    val outputDir = "src/main/kotlin/ast"

    defineAst(
        outputDir, "Expr", listOf(
            "Assign : Token name, Expr value",
            "Binary : Expr left, Token operator, Expr right",
            "Call : Expr callee, Token paren, List<Expr> arguments",
            "Get : Expr obj, Token name",
            "Grouping : Expr expression",
            "Literal : Any? value",
            "Logical : Expr left, Token operator, Expr right",
            "Set : Expr obj, Token name, Expr value",
            "Super : Token keyword, Token method",
            "This : Token keyword",
            "Unary : Token operator, Expr right",
            "Variable : Token name"
        )
    )

    defineAst(
        outputDir, "Stmt", listOf(
            "Block : List<Stmt> statements",
            "Class : Token name, Expr.Variable? superclass, List<Stmt.Function> methods",
            "Expression : Expr expression",
            "Function : Token name, List<Token> params, List<Stmt> body",
            "If : Expr condition, Stmt thenBranch, Stmt? elseBranch",
            "Print : Expr expression",
            "Return : Token keyword, Expr? value",
            "VariableDeclaration : Token name, Expr initializer",
            "While : Expr condition, Stmt body",
            "Break: Token item",
            "Continue: Token item",
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
            writer.println("    class $className (")
            val fields = fieldList.split(", ")
            for (field in fields) {
                val fieldData = field.split(" ")
                writer.println("        val ${fieldData[1]} : ${fieldData[0]},")
            }
            writer.println("    ) : $baseName() {")
        } else {
            writer.println("    class $className () : $baseName() {")
        }

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


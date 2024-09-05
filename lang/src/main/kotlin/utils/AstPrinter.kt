package utils

import ast.Expr
import ast.Stmt

class AstPrinter : Expr.Visitor<String>, Stmt.Visitor<String> {
    fun print(expr: Expr): String {
        return expr.accept(this)
    }

    fun print(stmt: Stmt): String {
        return stmt.accept(this)
    }

    override fun visitAssignExpr(expr: Expr.Assign): String {
        return "assignment (${expr.name.lexeme} := ${expr.value.accept(this)})"
    }

    override fun visitBinaryExpr(expr: Expr.Binary): String {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right)
    }

    override fun visitCallExpr(expr: Expr.Call): String {
        return expr.callee.accept(this) + parenthesize("", *(expr.arguments.toTypedArray()))
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): String {
        return parenthesize("group", expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): String {
        return if (expr.value == null) "null" else expr.value.toString()
    }

    override fun visitStringLiteralExpr(expr: Expr.StringLiteral): String {
        return expr.value
    }

    override fun visitLogicalExpr(expr: Expr.Logical): String {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right)
    }

    override fun visitGetExpr(expr: Expr.Get): String {
        return "${parenthesize("get", expr.obj)}${expr.name.lexeme}"
    }

    override fun visitSetExpr(expr: Expr.Set): String {
        return "${parenthesize("set", expr.obj)}${expr.name.lexeme} = ${expr.value.accept(this)}"
    }

    override fun visitSuperExpr(expr: Expr.Super): String {
        return "super"
    }

    override fun visitThisExpr(expr: Expr.This): String {
        return "this"
    }

    override fun visitUnaryExpr(expr: Expr.Unary): String {
        return parenthesize(expr.operator.lexeme, expr.right)
    }

    override fun visitVariableExpr(expr: Expr.Variable): String {
        return "var(${expr.name})"
    }

    override fun visitArrayLiteralExpr(expr: Expr.ArrayLiteral): String {
        val sb = StringBuilder()
        sb.append("Array [")

        for ((index, element) in expr.elements.withIndex()) {
            sb.append(element?.accept(this) ?: "null")

            if (index != expr.elements.lastIndex) {
                sb.append(", ")
            }
        }

        sb.append(']')
        return sb.toString()
    }

    private fun parenthesize(name: String, vararg expressions: Expr): String {
        val sb = StringBuilder()
        sb.append("($name")

        for (expression in expressions) {
            sb.append(" ${expression.accept(this)}")
        }

        sb.append(')')
        return sb.toString()
    }

    override fun visitBlockStmt(stmt: Stmt.Block): String {
        val sb = StringBuilder()
        sb.appendLine("block {")

        for (statement in stmt.statements) {
            sb.appendLine("\t ${statement.accept(this)}")
        }

        sb.appendLine("} end block")
        return sb.toString()
    }

    override fun visitClassStmt(stmt: Stmt.Class): String {
        val sb = StringBuilder()
        sb.append("class (${stmt.name.lexeme}")
        if(stmt.superclass != null) {
            sb.append(" extends ${stmt.superclass.name.lexeme}")
        }
        sb.append(")")
        return sb.toString()
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression): String {
        return stmt.expression.accept(this)
    }

    override fun visitFunctionStmt(stmt: Stmt.Function): String {
        val sb = StringBuilder()
        sb.appendLine("function (${stmt.name.lexeme}")
        stmt.body.forEach { sb.appendLine(it.accept(this)) }
        sb.append(")")
        return sb.toString()
    }

    override fun visitIfStmt(stmt: Stmt.If): String {
        val sb = StringBuilder()
        sb.append(parenthesize("if", stmt.condition))
        sb.append(" then : ")
        sb.append(stmt.thenBranch.accept(this))
        if (stmt.elseBranch != null) {
            sb.append("else : ")
            sb.append(stmt.elseBranch.accept(this))
        }

        return sb.toString()
    }

    override fun visitPrintStmt(stmt: Stmt.Print): String {
        return parenthesize("print", stmt.expression)
    }

    override fun visitReturnStmt(stmt: Stmt.Return): String {
        return if (stmt.value != null) parenthesize("return", stmt.value) else parenthesize("return")
    }

    override fun visitVariableDeclarationStmt(stmt: Stmt.VariableDeclaration): String {
        return parenthesize("variable declaration : ${stmt.name}", stmt.initializer)
    }

    override fun visitConstantDeclarationStmt(stmt: Stmt.ConstantDeclaration): String {
        return parenthesize("constant declaration : ${stmt.name}", stmt.initializer)
    }

    override fun visitWhileStmt(stmt: Stmt.While): String {
        return "${parenthesize("while", stmt.condition)} ${stmt.body.accept(this)}"
    }

    override fun visitDoWhileStmt(stmt: Stmt.DoWhile): String {
        return "${parenthesize("do while", stmt.condition)} ${stmt.body.accept(this)}"
    }

    override fun visitBreakStmt(stmt: Stmt.Break): String {
        return "break"
    }

    override fun visitContinueStmt(stmt: Stmt.Continue): String {
        return "continue"
    }

    override fun visitImportStmt(stmt: Stmt.Import): String {
        return "import ${stmt.path}"
    }
}
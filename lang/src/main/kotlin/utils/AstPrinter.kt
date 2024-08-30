package utils

import ast.Expr

class AstPrinter : Expr.Visitor<String> {
    fun print(expr: Expr): String {
        return expr.accept(this)
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
        return "var(${expr.accept(this)})"
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
}
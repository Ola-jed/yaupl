package utils

import ast.Expr
import core.enum.TokenType

class ReversePolishConverter : Expr.Visitor<String> {
    override fun visitAssignExpr(expr: Expr.Assign): String {
        return "assign(${expr.name.lexeme}, ${expr.value.accept(this)})"
    }

    override fun visitBinaryExpr(expr: Expr.Binary): String {
        return "${expr.left.accept(this)} ${expr.right.accept(this)} ${expr.operator.lexeme}"
    }

    override fun visitCallExpr(expr: Expr.Call): String {
        return "function call ${expr.callee.accept(this)} (${expr.arguments.joinToString(",") { it.accept(this) }})"
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): String {
        return expr.expression.accept(this)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): String {
        return if (expr.value == null) "null" else expr.value.toString()
    }

    override fun visitLogicalExpr(expr: Expr.Logical): String {
        return "${expr.left.accept(this)} ${expr.right.accept(this)} ${expr.operator.lexeme}"
    }

    override fun visitGetExpr(expr: Expr.Get): String {
        return "(${expr.obj.accept(this)}.${expr.name.lexeme})"
    }

    override fun visitSetExpr(expr: Expr.Set): String {
        return "set (${expr.obj.accept(this)}.${expr.name.lexeme}) ${expr.value.accept(this)}"
    }

    override fun visitSuperExpr(expr: Expr.Super): String {
        return "super"
    }

    override fun visitThisExpr(expr: Expr.This): String {
        return "this"
    }

    override fun visitUnaryExpr(expr: Expr.Unary): String {
        val lexemeValue = when (expr.operator.type) {
            TokenType.MINUS -> "NEGATE"
            TokenType.BANG -> "NOT"
            else -> expr.operator.literal
        }

        return "${expr.right.accept(this)} $lexemeValue"
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
}
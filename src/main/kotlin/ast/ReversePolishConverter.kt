package ast

import core.enum.TokenType

class ReversePolishConverter: Expr.Visitor<String> {
    override fun visitBinaryExpr(expr: Expr.Binary): String {
        return "${expr.left.accept(this)} ${expr.right.accept(this)} ${expr.operator.lexeme}"
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): String {
        return expr.expression.accept(this)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): String {
        return if (expr.value == null) "null" else expr.value.toString()
    }

    override fun visitUnaryExpr(expr: Expr.Unary): String {
        val lexemeValue = when(expr.operator.type) {
            TokenType.MINUS -> "NEGATE"
            TokenType.BANG -> "NOT"
            else -> expr.operator.literal
        }

        return "${expr.right.accept(this)} $lexemeValue"
    }
}
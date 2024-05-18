package ast

import core.scanner.Token

sealed class Expr {
    interface Visitor<R> {
        fun visitAssignExpr(expr: Assign) : R
        fun visitBinaryExpr(expr: Binary) : R
        fun visitGroupingExpr(expr: Grouping) : R
        fun visitLiteralExpr(expr: Literal) : R
        fun visitUnaryExpr(expr: Unary) : R
        fun visitVariableExpr(expr: Variable) : R
    }

    abstract fun <R> accept( visitor: Visitor<R>) : R

    class Assign (
        val name : Token,
        val value : Expr,
    ) : Expr() {
        override fun<R> accept(visitor: Visitor<R>) = visitor.visitAssignExpr(this)
    }

    class Binary (
        val left : Expr,
        val operator : Token,
        val right : Expr,
    ) : Expr() {
        override fun<R> accept(visitor: Visitor<R>) = visitor.visitBinaryExpr(this)
    }

    class Grouping (
        val expression : Expr,
    ) : Expr() {
        override fun<R> accept(visitor: Visitor<R>) = visitor.visitGroupingExpr(this)
    }

    class Literal (
        val value : Any?,
    ) : Expr() {
        override fun<R> accept(visitor: Visitor<R>) = visitor.visitLiteralExpr(this)
    }

    class Unary (
        val operator : Token,
        val right : Expr,
    ) : Expr() {
        override fun<R> accept(visitor: Visitor<R>) = visitor.visitUnaryExpr(this)
    }

    class Variable (
        val name : Token,
    ) : Expr() {
        override fun<R> accept(visitor: Visitor<R>) = visitor.visitVariableExpr(this)
    }

}

package ast

import core.Token

sealed class Expr {
    interface Visitor<R> {
        fun visitBinaryExpr(expr: Binary) : R
        fun visitGroupingExpr(expr: Grouping) : R
        fun visitLiteralExpr(expr: Literal) : R
        fun visitUnaryExpr(expr: Unary) : R
    }

    abstract fun<R> accept( visitor: Visitor<R>)

    class Binary (
        val left : Expr,
        val operator : Token,
        val right : Expr,
    ) {
        @Override
        fun<R> accept(visitor: Visitor<R>) = visitor.visitBinaryExpr(this)
    }

    class Grouping (
        val expression : Expr,
    ) {
        @Override
        fun<R> accept(visitor: Visitor<R>) = visitor.visitGroupingExpr(this)
    }

    class Literal (
        val value : Any,
    ) {
        @Override
        fun<R> accept(visitor: Visitor<R>) = visitor.visitLiteralExpr(this)
    }

    class Unary (
        val operator : Token,
        val right : Expr,
    ) {
        @Override
        fun<R> accept(visitor: Visitor<R>) = visitor.visitUnaryExpr(this)
    }

}

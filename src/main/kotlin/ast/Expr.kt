package ast

import core.scanner.Token

sealed class Expr {
    interface Visitor<R> {
        fun visitAssignExpr(expr: Assign): R
        fun visitBinaryExpr(expr: Binary): R
        fun visitCallExpr(expr: Call): R
        fun visitGetExpr(expr: Get): R
        fun visitGroupingExpr(expr: Grouping): R
        fun visitLiteralExpr(expr: Literal): R
        fun visitLogicalExpr(expr: Logical): R
        fun visitSetExpr(expr: Set): R
        fun visitSuperExpr(expr: Super): R
        fun visitThisExpr(expr: This): R
        fun visitUnaryExpr(expr: Unary): R
        fun visitVariableExpr(expr: Variable): R
        fun visitArrayLiteralExpr(expr: ArrayLiteral): R
    }

    abstract fun <R> accept(visitor: Visitor<R>): R

    class Assign(
        val name: Token,
        val value: Expr,
    ) : Expr() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitAssignExpr(this)
    }

    class Binary(
        val left: Expr,
        val operator: Token,
        val right: Expr,
    ) : Expr() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitBinaryExpr(this)
    }

    class Call(
        val callee: Expr,
        val paren: Token,
        val arguments: List<Expr>,
    ) : Expr() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitCallExpr(this)
    }

    class Get(
        val obj: Expr,
        val name: Token,
    ) : Expr() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitGetExpr(this)
    }

    class Grouping(
        val expression: Expr,
    ) : Expr() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitGroupingExpr(this)
    }

    class Literal(
        val value: Any?,
    ) : Expr() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitLiteralExpr(this)
    }

    class Logical(
        val left: Expr,
        val operator: Token,
        val right: Expr,
    ) : Expr() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitLogicalExpr(this)
    }

    class Set(
        val obj: Expr,
        val name: Token,
        val value: Expr,
    ) : Expr() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitSetExpr(this)
    }

    class Super(
        val keyword: Token,
        val method: Token,
    ) : Expr() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitSuperExpr(this)
    }

    class This(
        val keyword: Token,
    ) : Expr() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitThisExpr(this)
    }

    class Unary(
        val operator: Token,
        val right: Expr,
    ) : Expr() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitUnaryExpr(this)
    }

    class Variable(
        val name: Token,
    ) : Expr() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitVariableExpr(this)
    }

    class ArrayLiteral(
        val elements: List<Expr?>,
    ) : Expr() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitArrayLiteralExpr(this)
    }

}

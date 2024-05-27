package ast

import core.scanner.Token

sealed class Stmt {
    interface Visitor<R> {
        fun visitBlockStmt(stmt: Block) : R
        fun visitExpressionStmt(stmt: Expression) : R
        fun visitPrintStmt(stmt: Print) : R
        fun visitVariableDeclarationStmt(stmt: VariableDeclaration) : R
    }

    abstract fun <R> accept( visitor: Visitor<R>) : R

    class Block (
        val statements : List<Stmt>,
    ) : Stmt() {
        override fun<R> accept(visitor: Visitor<R>) = visitor.visitBlockStmt(this)
    }

    class Expression (
        val expression : Expr,
    ) : Stmt() {
        override fun<R> accept(visitor: Visitor<R>) = visitor.visitExpressionStmt(this)
    }

    class Print (
        val expression : Expr,
    ) : Stmt() {
        override fun<R> accept(visitor: Visitor<R>) = visitor.visitPrintStmt(this)
    }

    class VariableDeclaration (
        val name : Token,
        val initializer : Expr,
    ) : Stmt() {
        override fun<R> accept(visitor: Visitor<R>) = visitor.visitVariableDeclarationStmt(this)
    }

}

package ast

import core.scanner.Token

sealed class Stmt {
    interface Visitor<R> {
        fun visitBlockStmt(stmt: Block) : R
        fun visitExpressionStmt(stmt: Expression) : R
        fun visitIfStmt(stmt: If) : R
        fun visitPrintStmt(stmt: Print) : R
        fun visitVariableDeclarationStmt(stmt: VariableDeclaration) : R
        fun visitWhileStmt(stmt: While) : R
        fun visitBreakStmt(stmt: Break) : R
        fun visitContinueStmt(stmt: Continue) : R
    }

    abstract fun <R> accept(visitor: Visitor<R>) : R

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

    class If (
        val condition : Expr,
        val thenBranch : Stmt,
        val elseBranch : Stmt?,
    ) : Stmt() {
        override fun<R> accept(visitor: Visitor<R>) = visitor.visitIfStmt(this)
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

    class While (
        val condition : Expr,
        val body : Stmt,
    ) : Stmt() {
        override fun<R> accept(visitor: Visitor<R>) = visitor.visitWhileStmt(this)
    }

    class Break (
        val item : Token,
    ) : Stmt() {
        override fun<R> accept(visitor: Visitor<R>) = visitor.visitBreakStmt(this)
    }

    class Continue (
        val item : Token,
    ) : Stmt() {
        override fun<R> accept(visitor: Visitor<R>) = visitor.visitContinueStmt(this)
    }

}

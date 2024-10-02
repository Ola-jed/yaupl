package ast

import core.scanner.Token

sealed class Stmt {
    interface Visitor<R> {
        fun visitBlockStmt(stmt: Block): R
        fun visitClassStmt(stmt: Class): R
        fun visitExpressionStmt(stmt: Expression): R
        fun visitFunctionStmt(stmt: Function): R
        fun visitIfStmt(stmt: If): R
        fun visitPrintStmt(stmt: Print): R
        fun visitReturnStmt(stmt: Return): R
        fun visitVariableDeclarationStmt(stmt: VariableDeclaration): R
        fun visitConstantDeclarationStmt(stmt: ConstantDeclaration): R
        fun visitWhileStmt(stmt: While): R
        fun visitDoWhileStmt(stmt: DoWhile): R
        fun visitBreakStmt(stmt: Break): R
        fun visitContinueStmt(stmt: Continue): R
        fun visitImportStmt(stmt: Import): R
    }

    abstract fun <R> accept(visitor: Visitor<R>): R

    class Block(
        val statements: List<Stmt>,
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitBlockStmt(this)
    }

    class Class(
        val name: Token,
        val superclass: Expr.Variable?,
        val methods: List<Stmt.Function>,
        val staticMethods: List<Stmt.Function>,
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitClassStmt(this)
    }

    class Expression(
        val expression: Expr,
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitExpressionStmt(this)
    }

    class Function(
        val name: Token,
        val params: List<Token>,
        val body: List<Stmt>,
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitFunctionStmt(this)
    }

    class If(
        val condition: Expr,
        val thenBranch: Stmt,
        val elseBranch: Stmt?,
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitIfStmt(this)
    }

    class Print(
        val expression: Expr,
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitPrintStmt(this)
    }

    class Return(
        val keyword: Token,
        val value: Expr?,
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitReturnStmt(this)
    }

    class VariableDeclaration(
        val name: Token,
        val initializer: Expr,
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitVariableDeclarationStmt(this)
    }

    class ConstantDeclaration(
        val name: Token,
        val initializer: Expr,
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitConstantDeclarationStmt(this)
    }

    class While(
        val condition: Expr,
        val body: Stmt,
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitWhileStmt(this)
    }

    class DoWhile(
        val condition: Expr,
        val body: Stmt,
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitDoWhileStmt(this)
    }

    class Break(
        val item: Token,
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitBreakStmt(this)
    }

    class Continue(
        val item: Token,
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitContinueStmt(this)
    }

    class Import(
        val keyword: Token,
        val path: String,
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visitImportStmt(this)
    }

}

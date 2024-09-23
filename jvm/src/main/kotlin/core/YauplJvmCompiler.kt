package core

import ast.Expr
import ast.Stmt
import core.error.reporter.ErrorReporter
import io.FilePathResolver
import net.bytebuddy.ByteBuddy
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.implementation.MethodCall

class YauplJvmCompiler(
    private val pkg: String?,
    private val errorReporter: ErrorReporter,
    private val filePathResolver: FilePathResolver
) : Expr.Visitor<Any?>, Stmt.Visitor<Unit> {

    fun compileBytecode(statements: List<Stmt>): DynamicType {
        return ByteBuddy()
            .subclass(Any::class.java)
            .name("$pkg.Main")
            .defineMethod(
                "main",
                Void.TYPE,
                java.lang.reflect.Modifier.PUBLIC or java.lang.reflect.Modifier.STATIC
            )
            .withParameters(Array<String>::class.java)
            .intercept(
                MethodCall.invoke(System.out::class.java.getMethod("println", String::class.java))
                    .onField(System::class.java.getField("out"))
                    .with("Hello, World!")
            )
            .make()
    }

    override fun visitAssignExpr(expr: Expr.Assign): Any? {
        TODO("Not yet implemented")
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        TODO("Not yet implemented")
    }

    override fun visitCallExpr(expr: Expr.Call): Any? {
        TODO("Not yet implemented")
    }

    override fun visitGetExpr(expr: Expr.Get): Any? {
        TODO("Not yet implemented")
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any? {
        TODO("Not yet implemented")
    }

    override fun visitLiteralExpr(expr: Expr.Literal): Any? {
        TODO("Not yet implemented")
    }

    override fun visitStringLiteralExpr(expr: Expr.StringLiteral): Any? {
        TODO("Not yet implemented")
    }

    override fun visitLogicalExpr(expr: Expr.Logical): Any? {
        TODO("Not yet implemented")
    }

    override fun visitSetExpr(expr: Expr.Set): Any? {
        TODO("Not yet implemented")
    }

    override fun visitSuperExpr(expr: Expr.Super): Any? {
        TODO("Not yet implemented")
    }

    override fun visitThisExpr(expr: Expr.This): Any? {
        TODO("Not yet implemented")
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        TODO("Not yet implemented")
    }

    override fun visitVariableExpr(expr: Expr.Variable): Any? {
        TODO("Not yet implemented")
    }

    override fun visitArrayLiteralExpr(expr: Expr.ArrayLiteral): Any? {
        TODO("Not yet implemented")
    }

    override fun visitBlockStmt(stmt: Stmt.Block) {
        TODO("Not yet implemented")
    }

    override fun visitClassStmt(stmt: Stmt.Class) {
        TODO("Not yet implemented")
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression) {
        TODO("Not yet implemented")
    }

    override fun visitFunctionStmt(stmt: Stmt.Function) {
        TODO("Not yet implemented")
    }

    override fun visitIfStmt(stmt: Stmt.If) {
        TODO("Not yet implemented")
    }

    override fun visitPrintStmt(stmt: Stmt.Print) {
        TODO("Not yet implemented")
    }

    override fun visitReturnStmt(stmt: Stmt.Return) {
        TODO("Not yet implemented")
    }

    override fun visitVariableDeclarationStmt(stmt: Stmt.VariableDeclaration) {
        TODO("Not yet implemented")
    }

    override fun visitConstantDeclarationStmt(stmt: Stmt.ConstantDeclaration) {
        TODO("Not yet implemented")
    }

    override fun visitWhileStmt(stmt: Stmt.While) {
        TODO("Not yet implemented")
    }

    override fun visitDoWhileStmt(stmt: Stmt.DoWhile) {
        TODO("Not yet implemented")
    }

    override fun visitBreakStmt(stmt: Stmt.Break) {
        TODO("Not yet implemented")
    }

    override fun visitContinueStmt(stmt: Stmt.Continue) {
        TODO("Not yet implemented")
    }

    override fun visitImportStmt(stmt: Stmt.Import) {
        TODO("Not yet implemented")
    }
}
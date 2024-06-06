package core.interpreter

import ast.Expr
import ast.Stmt
import core.enum.FunctionType
import core.error.reporter.ErrorReporter
import core.scanner.Token
import java.util.*

class Resolver(
    private val interpreter: Interpreter,
    private val errorReporter: ErrorReporter,
    private val onRuntimeErrorReported: () -> Unit
) : Expr.Visitor<Unit>, Stmt.Visitor<Unit> {
    private val scopes: Stack<MutableMap<String, Boolean>> = Stack()
    private var currentFunction = FunctionType.NONE

    fun resolve(statements: List<Stmt>) {
        statements.forEach(::resolve)
    }

    override fun visitAssignExpr(expr: Expr.Assign) {
        resolve(expr.value)
        resolveLocal(expr, expr.name)
    }

    override fun visitBinaryExpr(expr: Expr.Binary) {
        resolve(expr.left)
        resolve(expr.right)
    }

    override fun visitCallExpr(expr: Expr.Call) {
        resolve(expr.callee)
        expr.arguments.forEach(::resolve)
    }

    override fun visitGetExpr(expr: Expr.Get) {
        resolve(expr.obj)
    }

    override fun visitGroupingExpr(expr: Expr.Grouping) {
        resolve(expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal) {
        // Nothing
    }

    override fun visitLogicalExpr(expr: Expr.Logical) {
        resolve(expr.left)
        resolve(expr.right)
    }

    override fun visitSetExpr(expr: Expr.Set) {
        resolve(expr.value)
        resolve(expr.obj)
    }

    override fun visitUnaryExpr(expr: Expr.Unary) {
        resolve(expr.right)
    }

    override fun visitVariableExpr(expr: Expr.Variable) {
        if (!scopes.isEmpty() && scopes.peek()[expr.name.lexeme] == false) {
            errorReporter.reportTokenError(expr.name, "Can't read local variable in its own initializer.")
            onRuntimeErrorReported()
        }

        resolveLocal(expr, expr.name)
    }

    override fun visitBlockStmt(stmt: Stmt.Block) {
        beginScope()
        resolve(stmt.statements)
        endScope()
    }

    override fun visitClassStmt(stmt: Stmt.Class) {
        declare(stmt.name)
        define(stmt.name)
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression) {
        resolve(stmt.expression)
    }

    override fun visitFunctionStmt(stmt: Stmt.Function) {
        declare(stmt.name)
        define(stmt.name)
        resolveFunction(stmt, FunctionType.FUNCTION)
    }

    override fun visitIfStmt(stmt: Stmt.If) {
        resolve(stmt.condition)
        resolve(stmt.thenBranch)
        if (stmt.elseBranch != null) {
            resolve(stmt.elseBranch)
        }
    }

    override fun visitPrintStmt(stmt: Stmt.Print) {
        resolve(stmt.expression)
    }

    override fun visitReturnStmt(stmt: Stmt.Return) {
        if (currentFunction == FunctionType.NONE) {
            errorReporter.reportTokenError(stmt.keyword, "Can't return outside a function.")
        }

        if (stmt.value != null) {
            resolve(stmt.value)
        }
    }

    override fun visitVariableDeclarationStmt(stmt: Stmt.VariableDeclaration) {
        declare(stmt.name)
        resolve(stmt.initializer)
        define(stmt.name)
    }

    override fun visitWhileStmt(stmt: Stmt.While) {
        resolve(stmt.condition)
        resolve(stmt.body)
    }

    override fun visitBreakStmt(stmt: Stmt.Break) {
        // Nothing
    }

    override fun visitContinueStmt(stmt: Stmt.Continue) {
        // Nothing
    }

    private fun beginScope() {
        scopes.push(mutableMapOf())
    }

    private fun endScope() {
        scopes.pop()
    }

    private fun resolve(statement: Stmt) {
        statement.accept(this)
    }

    private fun resolve(expression: Expr) {
        expression.accept(this)
    }

    private fun resolveLocal(expression: Expr, name: Token) {
        for (i in (scopes.size - 1) downTo 0) {
            if (scopes[i].contains(name.lexeme)) {
                interpreter.resolve(expression, scopes.size - i - 1)
            }
        }
    }

    private fun resolveFunction(function: Stmt.Function, functionType: FunctionType) {
        val enclosingFunction = currentFunction
        currentFunction = functionType
        beginScope()

        for (param in function.params) {
            declare(param)
            define(param)
        }

        resolve(function.body)
        endScope()
        currentFunction = enclosingFunction
    }

    private fun declare(name: Token) {
        if (scopes.empty()) {
            return
        }

        val scope = scopes.peek()
        if (scope.contains(name.lexeme)) {
            errorReporter.reportTokenError(token = name, message = "Already a variable with this name in this scope.")
            onRuntimeErrorReported()
        }

        scope[name.lexeme] = false
    }

    private fun define(name: Token) {
        if (scopes.empty()) {
            return
        }

        scopes.peek()[name.lexeme] = true
    }
}
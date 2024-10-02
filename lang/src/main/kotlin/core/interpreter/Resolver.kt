package core.interpreter

import ast.Expr
import ast.Stmt
import core.enum.ClassType
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
    private var currentClass = ClassType.NONE

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

    override fun visitStringLiteralExpr(expr: Expr.StringLiteral) {
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

    override fun visitSuperExpr(expr: Expr.Super) {
        if (currentClass == ClassType.NONE) {
            errorReporter.reportTokenError(expr.keyword, "Can't use 'super' outside of a class.")
            onRuntimeErrorReported()
        } else if (currentClass != ClassType.SUBCLASS) {
            errorReporter.reportTokenError(expr.keyword, "Can't use 'super' in a class with no superclass.")
            onRuntimeErrorReported()
        }

        resolveLocal(expr, expr.keyword)
    }

    override fun visitThisExpr(expr: Expr.This) {
        if (currentClass == ClassType.NONE) {
            errorReporter.reportTokenError(expr.keyword, "Can't use 'this' outside of a class.")
            onRuntimeErrorReported()
        }

        resolveLocal(expr, expr.keyword)
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

    override fun visitArrayLiteralExpr(expr: Expr.ArrayLiteral) {
        // Nothing
    }

    override fun visitBlockStmt(stmt: Stmt.Block) {
        beginScope()
        resolve(stmt.statements)
        endScope()
    }

    override fun visitClassStmt(stmt: Stmt.Class) {
        val enclosingClass = currentClass
        currentClass = ClassType.CLASS
        declare(stmt.name)
        define(stmt.name)

        if (stmt.superclass != null && stmt.name.lexeme == stmt.superclass.name.lexeme) {
            errorReporter.reportTokenError(stmt.name, "A class can't inherit from itself.")
            onRuntimeErrorReported()
        }

        if (stmt.superclass != null) {
            currentClass = ClassType.SUBCLASS
            resolve(stmt.superclass)
            beginScope()
            scopes.peek()["super"] = true
        }

        beginScope()
        scopes.peek()["this"] = true
        for (method in stmt.methods) {
            var declaration = FunctionType.METHOD
            if (method.name.lexeme == "init") {
                declaration = FunctionType.INITIALIZER
            }

            resolveFunction(method, declaration)
        }

        scopes.peek().remove("this")
        for (method in stmt.staticMethods) {
            if (method.name.lexeme == "init") {
                errorReporter.reportTokenError(method.name, "Class initializer can't be static.")
                onRuntimeErrorReported()
            }

            val declaration = FunctionType.METHOD
            resolveFunction(method, declaration)
        }

        endScope()
        if (stmt.superclass != null) {
            endScope()
        }

        currentClass = enclosingClass
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
            if (currentFunction == FunctionType.INITIALIZER) {
                errorReporter.reportTokenError(stmt.keyword, "Can't return a value from an initializer.")
            }

            resolve(stmt.value)
        }
    }

    override fun visitVariableDeclarationStmt(stmt: Stmt.VariableDeclaration) {
        declare(stmt.name)
        resolve(stmt.initializer)
        define(stmt.name)
    }

    override fun visitConstantDeclarationStmt(stmt: Stmt.ConstantDeclaration) {
        declare(stmt.name)
        resolve(stmt.initializer)
        define(stmt.name)
    }

    override fun visitWhileStmt(stmt: Stmt.While) {
        resolve(stmt.condition)
        resolve(stmt.body)
    }

    override fun visitDoWhileStmt(stmt: Stmt.DoWhile) {
        resolve(stmt.body)
        resolve(stmt.condition)
    }

    override fun visitBreakStmt(stmt: Stmt.Break) {
        // Nothing
    }

    override fun visitContinueStmt(stmt: Stmt.Continue) {
        // Nothing
    }

    override fun visitImportStmt(stmt: Stmt.Import) {
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
package interpreter

import ast.Expr
import ast.Stmt
import core.enum.TokenType
import core.scanner.Token
import error.reporter.ErrorReporter
import error.types.RuntimeError
import memory.Memory

class Interpreter(
    private val errorReporter: ErrorReporter,
    private val onRuntimeErrorReported: () -> Unit
) : Expr.Visitor<Any?>, Stmt.Visitor<Unit> {
    private val memory = Memory()

    fun interpret(statements: List<Stmt>) {
        try {
            statements.forEach(::executeStatement)
        } catch (error: RuntimeError) {
            errorReporter.reportRuntimeError(error)
            onRuntimeErrorReported()
        }
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression) {
        evaluate(stmt.expression)
    }

    override fun visitPrintStmt(stmt: Stmt.Print) {
        println(stringify(evaluate(stmt.expression)))
    }

    override fun visitVariableDeclarationStmt(stmt: Stmt.VariableDeclaration) {
        val value = evaluate(stmt.initializer)
        memory.define(stmt.name.lexeme, value)
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)
        when (expr.operator.type) {
            // Arithmetic operations
            TokenType.MINUS -> {
                checkNumbers(expr.operator, left, right)
                return (left as Double) - (right as Double)
            }

            TokenType.STAR -> {
                checkNumbers(expr.operator, left, right)
                return (left as Double) * (right as Double)
            }

            TokenType.SLASH -> {
                checkNumbers(expr.operator, left, right)
                return (left as Double) / (right as Double)
            }

            TokenType.PLUS -> {
                return if (left is Number && right is Number) {
                    (left as Double) + (right as Double)
                } else {
                    "${stringify(left)}${stringify(right)}"
                }
            }

            // Comparisons
            TokenType.GREATER -> {
                checkNumbers(expr.operator, left, right)
                return (left as Double) > (right as Double)
            }

            TokenType.GREATER_EQUAL -> {
                checkNumbers(expr.operator, left, right)
                return (left as Double) >= (right as Double)
            }

            TokenType.LESS -> {
                checkNumbers(expr.operator, left, right)
                return (left as Double) < (right as Double)
            }

            TokenType.LESS_EQUAL -> {
                checkNumbers(expr.operator, left, right)
                return (left as Double) <= (right as Double)
            }

            // Equality
            TokenType.BANG_EQUAL -> return !areEqual(left, right)
            TokenType.EQUAL_EQUAL -> return areEqual(left, right)
            else -> {}
        }

        return null
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any? {
        return expr.expression.accept(this)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): Any? {
        return expr.value
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)

        if (expr.operator.type == TokenType.MINUS) {
            checkNumber(expr.operator, right)
            return -1 * (right as Double)
        } else if (expr.operator.type == TokenType.BANG) {
            return !isTruthy(right)
        }

        return null
    }

    override fun visitVariableExpr(expr: Expr.Variable): Any? {
        return memory.get(expr.name)
    }

    private fun executeStatement(statement: Stmt) {
        statement.accept(this)
    }

    private fun evaluate(expr: Expr): Any? {
        return expr.accept(this)
    }

    private fun isTruthy(value: Any?): Boolean {
        if (value == null) {
            return false
        }

        if (value is Boolean) {
            return value
        }

        if (value is Number) {
            return value != 0
        }

        return true
    }

    private fun areEqual(x: Any?, y: Any?): Boolean {
        if (x == null && y == null) {
            return true
        }

        if (x == null || y == null) {
            return false
        }

        return x == y
    }

    private fun checkNumber(operator: Token, operand: Any?) {
        if (operand is Double) {
            return
        }

        throw RuntimeError(operator, "Operand must be a number")
    }

    private fun checkNumbers(operator: Token, x: Any?, y: Any?) {
        if ((x is Double) && (y is Double)) {
            return
        }

        throw RuntimeError(operator, "Operands must both be numbers")
    }

    private fun stringify(value: Any?): String {
        if (value == null) {
            return "null"
        }

        if (value is Double) {
            var text = value.toString()

            if (text.endsWith(".0")) {
                text = text.removeSuffix(".0")
            }

            return text
        }

        return value.toString()
    }
}
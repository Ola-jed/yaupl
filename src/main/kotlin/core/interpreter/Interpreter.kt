package core.interpreter

import ast.Expr
import ast.Stmt
import core.enum.TokenType
import core.scanner.Token
import core.types.YCallable
import core.types.native.Clock
import core.types.native.YFunction
import core.error.reporter.ErrorReporter
import core.error.types.RuntimeError
import core.runtime.Environment
import core.`object`.Return
import core.types.native.YClass
import core.types.native.YInstance
import kotlin.math.pow


class Interpreter(
    private val errorReporter: ErrorReporter,
    private val onRuntimeErrorReported: () -> Unit,
    private val replMode: Boolean
) : Expr.Visitor<Any?>, Stmt.Visitor<Unit> {
    private var globals = Environment()
    private var environment = globals
    private var isInLoop = false
    private var continueFound = false
    private val locals = mutableMapOf<Expr, Int>()

    init {
        globals.define(Clock.token, Clock)
    }

    fun interpret(statements: List<Stmt>) {
        try {
            statements.forEach(::executeStatement)
        } catch (error: RuntimeError) {
            errorReporter.reportRuntimeError(error)
            onRuntimeErrorReported()
        }
    }

    override fun visitBlockStmt(stmt: Stmt.Block) {
        executeBlock(stmt.statements, Environment(environment))
    }

    override fun visitClassStmt(stmt: Stmt.Class) {
        var superClass: Any? = null

        if (stmt.superclass != null) {
            superClass = evaluate(stmt.superclass)
            if (superClass !is YClass) {
                throw RuntimeError(stmt.superclass.name, "Superclass must be a class.")
            }
        }

        environment.define(stmt.name, null)
        if (stmt.superclass != null) {
            environment = Environment(environment)
            environment.define(Token(type = TokenType.SUPER, lexeme = "super"), superClass)
        }

        val methods = mutableMapOf<String, YFunction>()
        for (method in stmt.methods) {
            val function = YFunction(method, environment, method.name.lexeme == "init")
            methods[method.name.lexeme] = function
        }

        val clazz = YClass(stmt.name.lexeme, superClass as? YClass, methods)
        if (superClass != null) {
            environment = environment.outer!!
        }

        environment.assign(stmt.name, clazz)
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression) {
        val result = evaluate(stmt.expression)

        if (replMode) {
            println(stringify(result))
        }
    }

    override fun visitFunctionStmt(stmt: Stmt.Function) {
        val function = YFunction(stmt, environment, false)
        environment.define(stmt.name, function)
    }

    override fun visitIfStmt(stmt: Stmt.If) {
        if (isTruthy(evaluate(stmt.condition))) {
            executeStatement(stmt.thenBranch)
        } else if (stmt.elseBranch != null) {
            executeStatement(stmt.elseBranch)
        }
    }

    override fun visitPrintStmt(stmt: Stmt.Print) {
        println(stringify(evaluate(stmt.expression)))
    }

    override fun visitReturnStmt(stmt: Stmt.Return) {
        var value: Any? = null
        if (stmt.value != null) {
            value = evaluate(stmt.value)
        }

        throw Return(value)
    }

    override fun visitVariableDeclarationStmt(stmt: Stmt.VariableDeclaration) {
        val value = evaluate(stmt.initializer)
        environment.define(stmt.name, value)
    }

    override fun visitWhileStmt(stmt: Stmt.While) {
        isInLoop = true
        while (isInLoop && isTruthy(evaluate(stmt.condition))) {
            executeStatement(stmt.body)
        }
        isInLoop = false
    }

    override fun visitDoWhileStmt(stmt: Stmt.DoWhile) {
        isInLoop = true
        do {
            executeStatement(stmt.body)
        } while (isInLoop && isTruthy(evaluate(stmt.condition)))
        isInLoop = false
    }

    override fun visitBreakStmt(stmt: Stmt.Break) {
        if (!isInLoop) {
            throw RuntimeError(stmt.item, "'break' statement only allowed in a loop.")
        }

        isInLoop = false
    }

    override fun visitContinueStmt(stmt: Stmt.Continue) {
        if (!isInLoop) {
            throw RuntimeError(stmt.item, "'continue' statement only allowed in a loop.")
        }

        continueFound = true
    }

    override fun visitAssignExpr(expr: Expr.Assign): Any? {
        val value = evaluate(expr.value)
        val distance = locals[expr]

        if (distance != null) {
            environment.assign(expr.name, value, distance)
        } else {
            globals.assign(expr.name, value)
        }

        return value
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

            TokenType.EXPONENT -> {
                checkNumbers(expr.operator, left, right)
                return (left as Double).pow(right as Double)
            }

            TokenType.STAR -> {
                checkNumbers(expr.operator, left, right)
                return (left as Double) * (right as Double)
            }

            TokenType.SLASH -> {
                checkNumbers(expr.operator, left, right)
                return (left as Double) / (right as Double)
            }

            TokenType.MODULO -> {
                checkNumbers(expr.operator, left, right)
                return (left as Double).toInt() % (right as Double).toInt()
            }

            TokenType.PLUS -> {
                return if (left is Number && right is Number) {
                    (left as Double) + (right as Double)
                } else {
                    "${stringify(left)}${stringify(right)}"
                }
            }

            // Bitwise operations
            TokenType.LSHIFT -> {
                checkNumbers(expr.operator, left, right)
                return (left as Double).toInt() shl (right as Double).toInt()
            }

            TokenType.RSHIFT -> {
                checkNumbers(expr.operator, left, right)
                return (left as Double).toInt() shr (right as Double).toInt()
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

    override fun visitCallExpr(expr: Expr.Call): Any? {
        val callee = evaluate(expr.callee)

        val arguments = mutableListOf<Any?>()
        for (argument in expr.arguments) {
            arguments.add(evaluate(argument))
        }

        if (callee !is YCallable) {
            throw RuntimeError(expr.paren, "Code block not callable")
        }

        if (callee.arity != arguments.size) {
            throw RuntimeError(expr.paren, "Expected ${callee.arity} arguments but got ${arguments.size} arguments")
        }

        return callee.call(this, arguments)
    }

    override fun visitGetExpr(expr: Expr.Get): Any? {
        val value = evaluate(expr.obj)
        if (value is YInstance) {
            return value.get(expr.name)
        }

        throw RuntimeError(expr.name, "Only instances have properties.")
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any? {
        return expr.expression.accept(this)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): Any? {
        return expr.value
    }

    override fun visitLogicalExpr(expr: Expr.Logical): Any? {
        when (expr.operator.type) {
            TokenType.OR -> {
                val left = evaluate(expr.left)

                if (isTruthy(left)) {
                    return left
                }

                return evaluate(expr.right)
            }

            TokenType.AND -> {
                val left = evaluate(expr.left)

                if (!isTruthy(left)) {
                    return left
                }

                return evaluate(expr.right)
            }

            TokenType.XOR -> {
                // A xor B = (A and NOT B) OR (NOT A and B)

                val xorExpression = Expr.Logical(
                    left = Expr.Logical(
                        left = expr.left,
                        operator = expr.operator.copy(type = TokenType.AND, lexeme = "and"),
                        right = Expr.Unary(
                            operator = expr.operator.copy(type = TokenType.BANG, lexeme = "!"),
                            right = expr.right
                        )
                    ),
                    operator = expr.operator.copy(type = TokenType.OR, lexeme = "or"),
                    right = Expr.Logical(
                        left = Expr.Unary(
                            operator = expr.operator.copy(type = TokenType.BANG, lexeme = "!"),
                            right = expr.left
                        ),
                        operator = expr.operator.copy(type = TokenType.AND, lexeme = "and"),
                        right = expr.right
                    ),
                )

                return xorExpression.accept(this)
            }

            TokenType.NOR -> {
                // A nor B =  not (A or B)

                val xorExpression = Expr.Unary(
                    operator = expr.operator.copy(type = TokenType.BANG, lexeme = "!"),
                    right = Expr.Logical(
                        left = expr.left,
                        operator = expr.operator.copy(type = TokenType.OR, lexeme = "or"),
                        right = expr.right
                    ),
                )

                return xorExpression.accept(this)
            }

            TokenType.NAND -> {
                // A nand B =  not (A and B)

                val xorExpression = Expr.Unary(
                    operator = expr.operator.copy(type = TokenType.BANG, lexeme = "!"),
                    right = Expr.Logical(
                        left = expr.left,
                        operator = expr.operator.copy(type = TokenType.AND, lexeme = "and"),
                        right = expr.right
                    ),
                )

                return xorExpression.accept(this)
            }


            else -> throw RuntimeError(expr.operator, "Unsupported logical operator")
        }
    }

    override fun visitSetExpr(expr: Expr.Set): Any? {
        val obj = evaluate(expr.obj)
        if (obj !is YInstance) {
            throw RuntimeError(expr.name, "Only class instances have fields.")
        }

        val value = evaluate(expr.value)
        obj.set(expr.name, value)
        return value
    }

    override fun visitSuperExpr(expr: Expr.Super): Any? {
        val distance = locals[expr]!!
        val superClass = environment.get(Token(lexeme = "super", type = TokenType.SUPER), distance) as YClass
        val instance = environment.get(Token(lexeme = "this", type = TokenType.THIS), distance - 1) as YInstance
        val method = superClass.findMethod(expr.method.lexeme)
            ?: throw RuntimeError(expr.method, "Undefined property '${expr.method.lexeme}'.")

        return method.bind(instance)
    }

    override fun visitThisExpr(expr: Expr.This): Any? {
        return lookUpVariable(expr.keyword, expr)
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
        return lookUpVariable(expr.name, expr)
    }

    fun resolve(expression: Expr, depth: Int) {
        locals[expression] = depth
    }

    private fun lookUpVariable(name: Token, expression: Expr): Any? {
        val distance = locals[expression]
        return if (distance != null) {
            environment.get(name, distance)
        } else {
            globals.get(name)
        }
    }

    private fun executeStatement(statement: Stmt) {
        statement.accept(this)
    }

    fun executeBlock(statements: List<Stmt>, environment: Environment) {
        val previous = this.environment
        val isLoop = isInLoop

        try {
            this.environment = environment
            for (statement in statements) {
                if (isLoop && !isInLoop) {
                    break
                }

                if (isLoop && continueFound) {
                    continueFound = false
                    break
                }

                executeStatement(statement)
            }
        } finally {
            this.environment = previous
        }
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
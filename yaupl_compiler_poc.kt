package core

import ast.Expr
import ast.Stmt
import core.error.reporter.ErrorReporter
import io.FilePathResolver
import net.bytebuddy.ByteBuddy
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.implementation.*
import net.bytebuddy.implementation.bytecode.StackManipulation
import net.bytebuddy.matcher.ElementMatchers
import java.util.*

class YauplJvmCompiler(
    private val pkg: String?,
    private val errorReporter: ErrorReporter,
    private val filePathResolver: FilePathResolver
) : Expr.Visitor<Implementation>, Stmt.Visitor<Implementation> {

    private val symbolTable = SymbolTable()
    private val methods = mutableListOf<MethodInfo>()
    private var currentMethodBuilder: DynamicType.Builder<*>? = null
    
    data class MethodInfo(
        val name: String,
        val paramTypes: Array<Class<*>>,
        val returnType: Class<*>,
        val isStatic: Boolean,
        val implementation: Implementation
    )

    fun compileBytecode(statements: List<Stmt>): DynamicType.Unloaded<*> {
        var builder = ByteBuddy()
            .subclass(Any::class.java)
            .name("$pkg.Main")

        // Process all statements to collect methods and generate implementations
        statements.forEach { stmt ->
            when (stmt) {
                is Stmt.Function -> {
                    val impl = stmt.accept(this)
                    methods.add(MethodInfo(
                        stmt.name.lexeme,
                        Array(stmt.params.size) { Any::class.java }, // Simplified type system
                        Any::class.java,
                        false,
                        impl
                    ))
                }
                else -> {
                    // Add to main method body
                }
            }
        }

        // Create main method with all non-function statements
        val mainStatements = statements.filterNot { it is Stmt.Function }
        if (mainStatements.isNotEmpty()) {
            val mainImpl = createMainMethodImplementation(mainStatements)
            builder = builder
                .defineMethod(
                    "main",
                    Void.TYPE,
                    java.lang.reflect.Modifier.PUBLIC or java.lang.reflect.Modifier.STATIC
                )
                .withParameters(Array<String>::class.java)
                .intercept(mainImpl)
        }

        // Add all collected methods
        methods.forEach { method ->
            builder = builder
                .defineMethod(method.name, method.returnType, java.lang.reflect.Modifier.PUBLIC)
                .withParameters(*method.paramTypes)
                .intercept(method.implementation)
        }

        return builder.make()
    }

    private fun createMainMethodImplementation(statements: List<Stmt>): Implementation {
        // Combine all statement implementations
        val implementations = statements.map { it.accept(this) }
        return when {
            implementations.isEmpty() -> MethodCall.invoke(System.out::class.java.getMethod("println", String::class.java))
                .onField(System::class.java.getField("out"))
                .with("Empty program")
            implementations.size == 1 -> implementations.first()
            else -> Implementation.Compound(*implementations.toTypedArray())
        }
    }

    // ==================== Expression Visitors ====================

    override fun visitLiteralExpr(expr: Expr.Literal): Implementation {
        return when (val value = expr.value) {
            is String -> FixedValue.value(value)
            is Int -> FixedValue.value(value)
            is Double -> FixedValue.value(value)
            is Boolean -> FixedValue.value(value)
            null -> FixedValue.nullValue()
            else -> throw RuntimeException("Unsupported literal type: ${value::class}")
        }
    }

    override fun visitStringLiteralExpr(expr: Expr.StringLiteral): Implementation {
        return FixedValue.value(expr.value)
    }

    override fun visitVariableExpr(expr: Expr.Variable): Implementation {
        // For now, return a placeholder - you'd need to track variable access
        return FixedValue.value("variable_${expr.name.lexeme}")
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Implementation {
        val left = expr.left.accept(this)
        val right = expr.right.accept(this)
        
        // For arithmetic operations, we can use MethodCall to invoke operations
        return when (expr.operator.lexeme) {
            "+" -> createArithmeticOperation(left, right, "add")
            "-" -> createArithmeticOperation(left, right, "subtract")
            "*" -> createArithmeticOperation(left, right, "multiply")
            "/" -> createArithmeticOperation(left, right, "divide")
            ">" -> createComparisonOperation(left, right, "greater")
            "<" -> createComparisonOperation(left, right, "less")
            "==" -> createComparisonOperation(left, right, "equals")
            "!=" -> createComparisonOperation(left, right, "notEquals")
            else -> throw RuntimeException("Unsupported binary operator: ${expr.operator.lexeme}")
        }
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Implementation {
        val operand = expr.right.accept(this)
        
        return when (expr.operator.lexeme) {
            "-" -> createUnaryOperation(operand, "negate")
            "!" -> createUnaryOperation(operand, "not")
            else -> throw RuntimeException("Unsupported unary operator: ${expr.operator.lexeme}")
        }
    }

    override fun visitAssignExpr(expr: Expr.Assign): Implementation {
        val value = expr.value.accept(this)
        symbolTable.assign(expr.name.lexeme, value)
        return value // Return the assigned value
    }

    override fun visitCallExpr(expr: Expr.Call): Implementation {
        // Handle function calls
        val calleeName = when (expr.callee) {
            is Expr.Variable -> expr.callee.name.lexeme
            else -> throw RuntimeException("Complex callees not yet supported")
        }
        
        val arguments = expr.arguments.map { it.accept(this) }
        
        return when (calleeName) {
            "print", "println" -> createPrintCall(arguments.firstOrNull())
            else -> createUserDefinedMethodCall(calleeName, arguments)
        }
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Implementation {
        return expr.expression.accept(this)
    }

    // Placeholder implementations - you'd expand these based on your language features
    override fun visitGetExpr(expr: Expr.Get): Implementation = TODO("Field access")
    override fun visitLogicalExpr(expr: Expr.Logical): Implementation = TODO("Logical operators")
    override fun visitSetExpr(expr: Expr.Set): Implementation = TODO("Field assignment")
    override fun visitSuperExpr(expr: Expr.Super): Implementation = TODO("Super calls")
    override fun visitThisExpr(expr: Expr.This): Implementation = TODO("This reference")
    override fun visitArrayLiteralExpr(expr: Expr.ArrayLiteral): Implementation = TODO("Arrays")

    // ==================== Statement Visitors ====================

    override fun visitExpressionStmt(stmt: Stmt.Expression): Implementation {
        // Execute expression and discard result
        return stmt.expression.accept(this)
    }

    override fun visitPrintStmt(stmt: Stmt.Print): Implementation {
        val value = stmt.expression.accept(this)
        return createPrintCall(value)
    }

    override fun visitVariableDeclarationStmt(stmt: Stmt.VariableDeclaration): Implementation {
        val initializer = stmt.initializer?.accept(this) ?: FixedValue.nullValue()
        symbolTable.declare(stmt.name.lexeme, initializer)
        return initializer
    }

    override fun visitBlockStmt(stmt: Stmt.Block): Implementation {
        symbolTable.enterScope()
        val implementations = stmt.statements.map { it.accept(this) }
        symbolTable.exitScope()
        
        return when {
            implementations.isEmpty() -> NoOp.INSTANCE
            implementations.size == 1 -> implementations.first()
            else -> Implementation.Compound(*implementations.toTypedArray())
        }
    }

    override fun visitIfStmt(stmt: Stmt.If): Implementation {
        val condition = stmt.condition.accept(this)
        val thenBranch = stmt.thenBranch.accept(this)
        val elseBranch = stmt.elseBranch?.accept(this) ?: NoOp.INSTANCE
        
        // For complex control flow, we might need custom implementations
        return createConditionalImplementation(condition, thenBranch, elseBranch)
    }

    override fun visitWhileStmt(stmt: Stmt.While): Implementation {
        val condition = stmt.condition.accept(this)
        val body = stmt.body.accept(this)
        
        return createWhileLoopImplementation(condition, body)
    }

    override fun visitFunctionStmt(stmt: Stmt.Function): Implementation {
        symbolTable.enterScope()
        
        // Add parameters to symbol table
        stmt.params.forEach { param ->
            symbolTable.declareParameter(param.lexeme)
        }
        
        val bodyImplementations = stmt.body.map { it.accept(this) }
        val combinedBody = if (bodyImplementations.size == 1) {
            bodyImplementations.first()
        } else {
            Implementation.Compound(*bodyImplementations.toTypedArray())
        }
        
        symbolTable.exitScope()
        return combinedBody
    }

    override fun visitReturnStmt(stmt: Stmt.Return): Implementation {
        return if (stmt.value != null) {
            stmt.value.accept(this)
        } else {
            FixedValue.nullValue()
        }
    }

    // Placeholder implementations
    override fun visitClassStmt(stmt: Stmt.Class): Implementation = TODO("Class declarations")
    override fun visitConstantDeclarationStmt(stmt: Stmt.ConstantDeclaration): Implementation = TODO("Constants")
    override fun visitDoWhileStmt(stmt: Stmt.DoWhile): Implementation = TODO("Do-while loops")
    override fun visitBreakStmt(stmt: Stmt.Break): Implementation = TODO("Break statements")
    override fun visitContinueStmt(stmt: Stmt.Continue): Implementation = TODO("Continue statements")
    override fun visitImportStmt(stmt: Stmt.Import): Implementation = TODO("Import statements")

    // ==================== Helper Methods ====================

    private fun createPrintCall(value: Implementation?): Implementation {
        return if (value != null) {
            MethodCall.invoke(System.out::class.java.getMethod("println", Any::class.java))
                .onField(System::class.java.getField("out"))
                .with { methodCall ->
                    methodCall.withArgument(0) // Use the computed value
                }
                .andThen(value)
        } else {
            MethodCall.invoke(System.out::class.java.getMethod("println"))
                .onField(System::class.java.getField("out"))
        }
    }

    private fun createArithmeticOperation(
        left: Implementation,
        right: Implementation,
        operation: String
    ): Implementation {
        // For now, using string concatenation as an example
        // You'd implement actual arithmetic based on types
        return when (operation) {
            "add" -> MethodCall.invoke(String::class.java.getMethod("concat", String::class.java))
                .onMethodCall(MethodCall.invoke(Any::class.java.getMethod("toString")).onArgument(0))
                .withMethodCall(MethodCall.invoke(Any::class.java.getMethod("toString")).onArgument(1))
            else -> left // Simplified - return left operand for now
        }
    }

    private fun createComparisonOperation(
        left: Implementation,
        right: Implementation,
        operation: String
    ): Implementation {
        // Simplified comparison - returns boolean result
        return when (operation) {
            "equals" -> MethodCall.invoke(Any::class.java.getMethod("equals", Any::class.java))
                .onArgument(0)
                .withArgument(1)
            else -> FixedValue.value(true) // Placeholder
        }
    }

    private fun createUnaryOperation(operand: Implementation, operation: String): Implementation {
        return when (operation) {
            "negate" -> operand // Placeholder - implement numeric negation
            "not" -> FixedValue.value(false) // Placeholder - implement boolean negation
            else -> operand
        }
    }

    private fun createUserDefinedMethodCall(methodName: String, arguments: List<Implementation>): Implementation {
        // For user-defined methods, you'd need to resolve the method signature
        // This is a simplified version
        return MethodCall.invoke(ElementMatchers.named(methodName))
            .onThis()
            .withArguments(*Array(arguments.size) { it })
    }

    private fun createConditionalImplementation(
        condition: Implementation,
        thenBranch: Implementation,
        elseBranch: Implementation
    ): Implementation {
        // For complex control flow, you might need to create custom Implementation
        // This is a simplified approach using method delegation
        return object : Implementation {
            override fun prepare(instrumentedMethod: net.bytebuddy.description.method.MethodDescription): Implementation.ByteCodeAppender {
                return Implementation.ByteCodeAppender.Compound(
                    condition.prepare(instrumentedMethod),
                    // Custom bytecode for conditional logic would go here
                    thenBranch.prepare(instrumentedMethod)
                )
            }
            
            override fun appliesToMethod(method: net.bytebuddy.description.method.MethodDescription): Boolean = true
        }
    }

    private fun createWhileLoopImplementation(
        condition: Implementation,
        body: Implementation
    ): Implementation {
        // Similar to conditional, you'd need custom bytecode for loops
        return object : Implementation {
            override fun prepare(instrumentedMethod: net.bytebuddy.description.method.MethodDescription): Implementation.ByteCodeAppender {
                return Implementation.ByteCodeAppender.Compound(
                    condition.prepare(instrumentedMethod),
                    body.prepare(instrumentedMethod)
                )
            }
            
            override fun appliesToMethod(method: net.bytebuddy.description.method.MethodDescription): Boolean = true
        }
    }

    // ==================== Symbol Table ====================

    inner class SymbolTable {
        private val scopes = Stack<MutableMap<String, VariableInfo>>()
        
        init {
            enterScope() // Global scope
        }
        
        fun enterScope() {
            scopes.push(mutableMapOf())
        }
        
        fun exitScope() {
            if (scopes.size > 1) {
                scopes.pop()
            }
        }
        
        fun declare(name: String, implementation: Implementation) {
            val varInfo = VariableInfo(name, implementation, Any::class.java)
            scopes.peek()[name] = varInfo
        }
        
        fun declareParameter(name: String) {
            val varInfo = VariableInfo(name, MethodCall.invoke(ElementMatchers.named("getParameter"))
                .onArgument(scopes.peek().size), Any::class.java)
            scopes.peek()[name] = varInfo
        }
        
        fun assign(name: String, implementation: Implementation) {
            val varInfo = get(name) ?: throw RuntimeException("Undefined variable: $name")
            scopes.peek()[name] = varInfo.copy(implementation = implementation)
        }
        
        fun get(name: String): VariableInfo? {
            for (scope in scopes.reversed()) {
                scope[name]?.let { return it }
            }
            return null
        }
        
        data class VariableInfo(
            val name: String,
            val implementation: Implementation,
            val type: Class<*>
        )
    }

    // ==================== Enhanced Implementation Examples ====================

    /**
     * Example of how you might implement a more complex expression
     * This shows the pattern for building up complex implementations
     */
    fun exampleComplexExpression(): Implementation {
        // This would represent: System.out.println("Result: " + (5 + 3))
        return MethodCall.invoke(System.out::class.java.getMethod("println", String::class.java))
            .onField(System::class.java.getField("out"))
            .with(
                MethodCall.invoke(String::class.java.getMethod("concat", String::class.java))
                    .on(FixedValue.value("Result: "))
                    .withMethodCall(
                        MethodCall.invoke(Integer::class.java.getMethod("toString", Int::class.java))
                            .withArgument(
                                // This would be your binary addition implementation
                                FixedValue.value(8) // 5 + 3
                            )
                    )
            )
    }

    /**
     * Helper to create field access implementations
     */
    private fun createFieldAccess(objectImpl: Implementation, fieldName: String): Implementation {
        return MethodCall.invoke(ElementMatchers.named("get$fieldName"))
            .onMethodCall(objectImpl)
    }

    /**
     * Helper to create method calls with dynamic dispatch
     */
    private fun createDynamicMethodCall(
        receiver: Implementation,
        methodName: String,
        args: List<Implementation>
    ): Implementation {
        var call = MethodCall.invoke(ElementMatchers.named(methodName))
            .onMethodCall(receiver)
        
        args.forEachIndexed { index, arg ->
            call = call.withMethodCall(arg)
        }
        
        return call
    }

    /**
     * Create implementations for built-in operations
     */
    private fun createBuiltinOperation(operation: String, args: List<Implementation>): Implementation {
        return when (operation) {
            "toString" -> MethodCall.invoke(Any::class.java.getMethod("toString"))
                .onMethodCall(args.firstOrNull() ?: FixedValue.nullValue())
            
            "length" -> MethodCall.invoke(String::class.java.getMethod("length"))
                .onMethodCall(args.firstOrNull() ?: FixedValue.value(""))
            
            "substring" -> MethodCall.invoke(String::class.java.getMethod("substring", Int::class.java, Int::class.java))
                .onMethodCall(args[0])
                .withMethodCall(args[1])
                .withMethodCall(args[2])
            
            else -> throw RuntimeException("Unknown builtin operation: $operation")
        }
    }
}

/**
 * Usage example showing how to extend this for your specific language features
 */
/*
class ExtendedYauplCompiler : YauplJvmCompiler(...) {
    
    override fun visitCustomExpr(expr: CustomExpr): Implementation {
        // Implement your custom expression types here
        return when (expr.type) {
            "lambda" -> createLambdaImplementation(expr)
            "closure" -> createClosureImplementation(expr)
            else -> super.visitCustomExpr(expr)
        }
    }
    
    private fun createLambdaImplementation(expr: CustomExpr): Implementation {
        // Use Byte Buddy to create lambda-like functionality
        return MethodCall.invoke(ElementMatchers.named("apply"))
            .onMethodCall(
                // Create functional interface implementation
                MethodCall.construct(FunctionalInterface::class.java.getDeclaredConstructor())
            )
    }
}
*/
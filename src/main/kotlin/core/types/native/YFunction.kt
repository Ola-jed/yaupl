package core.types.native

import ast.Stmt
import core.enum.TokenType
import core.types.YCallable
import core.interpreter.Interpreter
import core.`object`.Return
import core.runtime.Environment
import core.scanner.Token


class YFunction(
    private val declaration: Stmt.Function,
    private val closure: Environment,
    private val isInitializer: Boolean
) : YCallable {
    override val arity: Int
        get() = declaration.params.size

    fun bind(instance: YInstance): YFunction {
        val env = Environment(closure)
        env.define(Token(lexeme = "this", type = TokenType.THIS), instance)
        return YFunction(declaration, env, isInitializer)
    }

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
        val environment = Environment(closure)

        for (i in declaration.params.indices) {
            environment.define(declaration.params[i], arguments[i])
        }

        try {
            interpreter.executeBlock(declaration.body, environment)
        } catch (returnValue: Return) {
            if (isInitializer) return closure.get(Token(lexeme = "this", type = TokenType.THIS), 0)
            return returnValue.value
        }

        if (isInitializer) {
            return closure.get(Token(lexeme = "this", type = TokenType.THIS), 0)
        }

        return null
    }

    override fun toString(): String {
        return "<fun ${declaration.name.lexeme}>"
    }
}
package core.types.native

import ast.Stmt
import core.types.YCallable
import core.interpreter.Interpreter
import core.`object`.Return
import core.runtime.Environment


class YFunction(private val declaration: Stmt.Function, private val closure: Environment) : YCallable {
    override val arity: Int
        get() = declaration.params.size

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
        val environment = Environment(closure)

        for (i in declaration.params.indices) {
            environment.define(declaration.params[i], arguments[i])
        }

        try {
            interpreter.executeBlock(declaration.body, environment)
        } catch (returnValue: Return) {
            return returnValue.value
        }

        return null
    }

    override fun toString(): String {
        return "<fun ${declaration.name.lexeme}>"
    }
}
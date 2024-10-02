package core.types.classes

import core.error.types.RuntimeError
import core.interpreter.Interpreter
import core.scanner.Token
import core.types.YCallable
import core.types.function.YFunction
import utils.FunctionArities

interface YClassInterface {
    val name: String
    fun findMethod(name: String): YFunction?
    fun call(interpreter: Interpreter, arguments: List<Any?>): Any
    fun get(name: Token): Any
    fun set(name: Token, value: Any?)
}

object Clazz : YClassInterface {
    override val name: String = "Class"

    override fun findMethod(name: String): YFunction? = null

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any = Unit

    override fun get(name: Token): Any = Unit

    override fun set(name: Token, value: Any?) = Unit
}

open class YClass(
    override val name: String,
    private val superclass: YClass? = null,
    private val methods: Map<String, YFunction> = mapOf(),
    private val staticMethods: Map<String, YFunction> = mapOf()
) : YCallable, YInstance(Clazz), YClassInterface {
    override val arity: Set<Int>
        get() = findMethod("init")?.arity ?: FunctionArities.ZERO_PARAMETERS

    override fun findMethod(name: String): YFunction? {
        return methods[name] ?: superclass?.findMethod(name)
    }

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        val instance = YInstance(this)
        val initializer = findMethod("init")
        initializer?.bind(instance)?.call(interpreter, arguments)
        return instance
    }

    override fun get(name: Token): Any {
        for ((methodName, method) in staticMethods) {
            if (methodName == name.lexeme) {
                return method
            }
        }

        throw RuntimeError(name, "Undefined static method ${name.lexeme}.")
    }

    override fun set(name: Token, value: Any?) {
        throw RuntimeError(name, "Cannot add static properties to class.")
    }

    override fun toString(): String {
        return name
    }
}
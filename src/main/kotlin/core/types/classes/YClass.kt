package core.types.classes

import core.interpreter.Interpreter
import core.types.YCallable
import core.types.function.YFunction

class YClass(val name: String, private val superclass: YClass?, private val methods: Map<String, YFunction>) :
    YCallable {
    override val arity: Int
        get() = findMethod("init")?.arity ?: 0

    fun findMethod(name: String): YFunction? {
        return methods[name] ?: superclass?.findMethod(name)
    }

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        val instance = YInstance(this)
        val initializer = findMethod("init")
        initializer?.bind(instance)?.call(interpreter, arguments)
        return instance
    }

    override fun toString(): String {
        return name
    }
}
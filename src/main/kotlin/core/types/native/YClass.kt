package core.types.native

import core.interpreter.Interpreter
import core.types.YCallable

class YClass(val name: String): YCallable {
    override val arity: Int
        get() = 0

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
        val instance = YInstance(this)
        return instance
    }

    override fun toString(): String {
        return name
    }
}
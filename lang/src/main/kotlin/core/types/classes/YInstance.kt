package core.types.classes

import core.error.types.RuntimeError
import core.scanner.Token

open class YInstance(private val clazz: YClassInterface) {
    private val fields = mutableMapOf<String, Any?>()

    open fun get(name: Token): Any? {
        if (fields.contains(name.lexeme)) {
            return fields[name.lexeme]
        }

        val method = clazz.findMethod(name.lexeme)
        if(method != null) {
            return method.bind(this)
        }

        throw RuntimeError(name, "Undefined property ${name.lexeme}.")
    }

    open fun set(name: Token, value: Any?) {
        fields[name.lexeme] = value
    }

    override fun toString(): String {
        return "${clazz.name} instance"
    }
}
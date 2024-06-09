package core.types.native

import core.error.types.RuntimeError
import core.scanner.Token

class YInstance(private val clazz: YClass) {
    private val fields = mutableMapOf<String, Any?>()

    fun get(name: Token): Any? {
        if (fields.contains(name.lexeme)) {
            return fields[name.lexeme]
        }

        val method = clazz.findMethod(name.lexeme)
        if(method != null) {
            return method.bind(this)
        }

        throw RuntimeError(name, "Undefined property ${name.lexeme}.")
    }

    fun set(name: Token, value: Any?) {
        fields[name.lexeme] = value
    }

    override fun toString(): String {
        return "${clazz.name} instance"
    }
}
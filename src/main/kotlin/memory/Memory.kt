package memory

import core.scanner.Token
import error.types.RuntimeError

class Memory {
    companion object {
        private val bindings = mutableMapOf<String, Any?>()
    }

    fun get(name: Token): Any? {
        if (bindings.containsKey(name.lexeme)) {
            return bindings[name.lexeme]
        }

        throw RuntimeError(name, "Undefined variable ${name.lexeme}.")
    }

    fun define(name: String, value: Any?) {
        bindings[name] = value
    }

    fun clear() {
        bindings.clear()
    }
}
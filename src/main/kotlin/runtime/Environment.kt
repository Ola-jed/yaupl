package runtime

import core.scanner.Token
import error.types.RuntimeError

class Environment(private val outer: Environment? = null) {
    companion object {
        private val bindings = mutableMapOf<String, Any?>()
    }

    fun get(name: Token): Any? {
        if (bindings.containsKey(name.lexeme)) {
            return bindings[name.lexeme]
        }

        if (outer != null) {
            return outer.get(name)
        }

        throw RuntimeError(name, "Undefined variable ${name.lexeme}.")
    }

    fun define(name: String, value: Any?) {
        bindings[name] = value
    }

    fun assign(name: Token, value: Any?) {
        if (bindings.containsKey(name.lexeme)) {
            bindings[name.lexeme] = value
        } else if (outer != null) {
            outer.assign(name, value)
        } else {
            throw RuntimeError(name, "Undefined variable ${name.lexeme}.")
        }
    }

    fun clear() {
        bindings.clear()
    }
}
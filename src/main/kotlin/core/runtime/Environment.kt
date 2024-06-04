package core.runtime

import core.`object`.Undefined
import core.scanner.Token
import core.error.types.RuntimeError

data class Environment(
    private val outer: Environment? = null,
    val bindings: MutableMap<String, Any?> = mutableMapOf()
) {
    fun get(name: Token): Any? {
        if (bindings.containsKey(name.lexeme)) {
            if (bindings[name.lexeme] == Undefined) {
                throw RuntimeError(name, "Variable ${name.lexeme} used before initialization.")
            }

            return bindings[name.lexeme]
        }

        if (outer != null) {
            return outer.get(name)
        }

        throw RuntimeError(name, "Undefined variable ${name.lexeme}.")
    }

    fun define(name: Token, value: Any?) {
        if (bindings.containsKey(name.lexeme)) {
            throw RuntimeError(name, "Cannot redeclare variable ${name.lexeme}.")
        }

        bindings[name.lexeme] = value
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
}
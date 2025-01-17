package core.runtime

import core.`object`.Undefined
import core.scanner.Token
import core.error.types.RuntimeError
import utils.TypeDeducer

class Environment(
    val outer: Environment? = null,
    private val bindings: MutableMap<String, Any?> = mutableMapOf(),
    private val constants: MutableSet<String> = mutableSetOf()
) {
    private val typeMappings: MutableMap<String, Any?> = mutableMapOf()

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

    fun get(name: Token, distance: Int): Any? {
        return ancestor(distance).get(name)
    }

    fun define(name: Token, value: Any?, constant: Boolean = false) {
        if (bindings.containsKey(name.lexeme)) {
            throw RuntimeError(name, "Cannot redeclare variable ${name.lexeme}.")
        }

        bindings[name.lexeme] = value
        if (value != Undefined && value != null) {
            typeMappings[name.lexeme] = TypeDeducer.inferTypeName(value)
        }

        if (constant) {
            constants.add(name.lexeme)
        }
    }

    fun undef(name: Token) {
        bindings.remove(name.lexeme)
        constants.remove(name.lexeme)
    }

    fun assign(name: Token, value: Any?) {
        if (constants.contains(name.lexeme)) {
            throw RuntimeError(name, "Cannot reassign constant ${name.lexeme}.")
        } else if (bindings.containsKey(name.lexeme)) {
            if (typeMappings.containsKey(name.lexeme)) {
                val initialType = typeMappings[name.lexeme]
                val actualTypeStr = if (value == null) "" else TypeDeducer.inferTypeName(value)

                if (value != null && actualTypeStr != initialType) {
                    throw RuntimeError(
                        name,
                        "Cannot assign value of type $actualTypeStr to a variable of type $initialType"
                    )
                }
            } else if (value != null) {
                typeMappings[name.lexeme] = TypeDeducer.inferTypeName(value)
            }

            bindings[name.lexeme] = value
        } else if (outer != null) {
            outer.assign(name, value)
        } else {
            throw RuntimeError(name, "Undefined variable ${name.lexeme}.")
        }
    }

    fun assign(name: Token, value: Any?, distance: Int) {
        ancestor(distance).assign(name, value)
    }

    private fun ancestor(distance: Int): Environment {
        var env = this
        for (i in 0..<distance) {
            env = env.outer!!
        }

        return env
    }
}
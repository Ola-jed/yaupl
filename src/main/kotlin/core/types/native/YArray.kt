package core.types.native

import core.error.types.RuntimeError
import core.interpreter.Interpreter
import core.scanner.Token
import core.types.YCallable
import utils.Stringifier
import kotlin.Array

class YArray(size: Int) : YInstance(YClass("Array", null, mapOf())) {
    private val elements: Array<Any?> = Array(size) { null }

    override fun get(name: Token): Any {
        when (name.lexeme) {
            "get" -> return object : YCallable {
                override val arity: Int
                    get() = 1

                override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
                    val index = (arguments[0] as Double).toInt()
                    if (index >= elements.size) {
                        throw RuntimeError(name, "Array index $index is out of bounds.")
                    }

                    return elements[index]
                }
            }

            "set" -> return object : YCallable {
                override val arity: Int
                    get() = 2

                override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
                    val index = (arguments[0] as Double).toInt()
                    if (index >= elements.size) {
                        throw RuntimeError(name, "Array index $index is out of bounds.")
                    }

                    val value = arguments[1]
                    elements[index] = value

                    return elements[index]
                }
            }

            "size" -> return elements.size
            else -> throw RuntimeError(name, "Undefined Array property ${name.lexeme}.")
        }
    }

    override fun set(name: Token, value: Any?) {
        throw RuntimeError(name, "Cannot add properties to arrays.")
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append('[')

        for (i in elements.indices) {
            stringBuilder.append(Stringifier.stringify(elements[i]) + " ")

        }

        stringBuilder.append(']')
        return stringBuilder.toString()
    }
}
package core.types.classes

import core.error.types.RuntimeError
import core.interpreter.Interpreter
import core.scanner.Token
import core.types.YCallable
import utils.Stringifier
import kotlin.Array

class YArray(size: Int) : YInstance(YClass("Array", null, mapOf())) {
    private val elements: Array<Any?> = Array(size) { null }

    constructor(elements: Array<Any?>) : this(elements.size) {
        elements.copyInto(this.elements)
    }

    override fun get(name: Token): Any {
        return when (name.lexeme) {
            "get" -> getIndex(name)
            "set" -> setIndex(name)
            "concat" -> concat(name)
            "fill" -> fill(name)
            "find" -> find(name)
            "reverse" -> reverse(name)
            "contains" -> contains(name)
            "length" -> length
            else -> throw RuntimeError(name, "Undefined Array property ${name.lexeme}.")
        }
    }

    override fun set(name: Token, value: Any?) {
        throw RuntimeError(name, "Cannot add properties to arrays.")
    }

    private val getIndex = fun(name: Token): YCallable {
        return object : YCallable {
            override val arity: Int
                get() = 1

            override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
                val index = (arguments[0] as Double).toInt()
                if (index >= elements.size) {
                    throw RuntimeError(name, "Array index $index is out of bounds.")
                }

                return if (index < 0) {
                    elements[elements.size + index]
                } else {
                    elements[index]
                }
            }
        }
    }

    private val setIndex = fun(name: Token): YCallable {
        return object : YCallable {
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
    }

    private val concat = fun(name: Token): YCallable {
        return object : YCallable {
            override val arity: Int
                get() = 1

            override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
                val arg = arguments[0] as? YArray
                    ?: throw RuntimeError(name, "Argument for Array.concat should be an Array.")

                val resultArray = arrayOfNulls<Any?>(elements.size + arg.elements.size)
                System.arraycopy(elements, 0, resultArray, 0, elements.size)
                System.arraycopy(arg.elements, 0, resultArray, elements.size, arg.elements.size)
                return YArray(resultArray)
            }
        }
    }

    private val fill = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Int
                get() = 1

            override fun call(interpreter: Interpreter, arguments: List<Any?>) {
                val value = arguments[0]
                elements.fill(value)
            }
        }
    }

    private val find = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Int
                get() = 1

            override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
                val value = arguments[0]
                return elements.find { it -> it == value }
            }
        }
    }

    private val reverse = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Int
                get() = 0

            override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
                return YArray(elements.reversedArray())
            }
        }
    }

    private val contains = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Int
                get() = 1

            override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
                val value = arguments[0]
                return elements.contains(value)
            }
        }
    }

    private val length = elements.size

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("Array [")
        for ((index, element) in elements.withIndex()) {
            stringBuilder.append(Stringifier.stringify(element))

            if (index != elements.lastIndex) {
                stringBuilder.append(", ")
            }
        }

        stringBuilder.append(']')
        return stringBuilder.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is YArray) {
            return false
        }

        if (elements.size != other.elements.size) {
            return false
        }

        for (i in elements.indices) {
            if (elements[i] != other.elements[i]) {
                return false
            }
        }

        return true
    }

    override fun hashCode(): Int {
        return elements.contentHashCode()
    }
}
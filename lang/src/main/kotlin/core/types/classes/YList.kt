package core.types.classes

import core.error.types.RuntimeError
import core.interpreter.Interpreter
import core.scanner.Token
import core.types.YCallable
import utils.Stringifier

class YList() : YInstance(YClass("List", null, mapOf())) {
    private var elements: MutableList<Any?> = mutableListOf()

    constructor(elements: List<Any?>) : this() {
        this.elements = elements.toMutableList()
    }

    override fun get(name: Token): Any {
        return when (name.lexeme) {
            "add" -> add(name)
            "get" -> getIndex(name)
            "set" -> setIndex(name)
            "concat" -> concat(name)
            "fill" -> fill(name)
            "find" -> find(name)
            "reverse" -> reverse(name)
            "contains" -> contains(name)
            "clear" -> clear(name)
            "remove" -> remove(name)
            "length" -> length
            else -> throw RuntimeError(name, "Undefined List property ${name.lexeme}.")
        }
    }

    override fun set(name: Token, value: Any?) {
        throw RuntimeError(name, "Cannot add properties to arrays.")
    }

    private val add = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Int
                get() = 1

            override fun call(interpreter: Interpreter, arguments: List<Any?>) {
                val element = arguments[0]
                elements.add(element)
            }
        }
    }

    private val getIndex = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Int
                get() = 1

            override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
                val index = (arguments[0] as Double).toInt()
                return elements[index]
            }
        }
    }

    private val setIndex = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Int
                get() = 2

            override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
                val index = (arguments[0] as Double).toInt()
                val value = arguments[1]

                elements[index] = value
                return elements[index]
            }
        }
    }

    private val remove = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Int
                get() = 1

            override fun call(interpreter: Interpreter, arguments: List<Any?>) {
                val index = (arguments[0] as Double).toInt()
                elements.removeAt(index)
            }
        }
    }

    private val concat = fun(name: Token): YCallable {
        return object : YCallable {
            override val arity: Int
                get() = 1

            override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
                val arg = arguments[0] as? YList
                    ?: throw RuntimeError(name, "Argument for List.concat should be a List.")

                val copy = elements.toMutableList()
                copy.addAll(arg.elements)
                return YList(copy)
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
                return elements.find { it == value }
            }
        }
    }

    private val reverse = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Int
                get() = 0

            override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
                return YList(elements.reversed())
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

    private val clear = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Int
                get() = 0

            override fun call(interpreter: Interpreter, arguments: List<Any?>) {
                elements.clear()
            }
        }
    }

    private val length = elements.size

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("List [")
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
        if (other !is YList) {
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
        return elements.hashCode()
    }
}
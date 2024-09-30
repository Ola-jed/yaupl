package core.types.classes

import core.error.types.RuntimeError
import core.interpreter.Interpreter
import core.scanner.Token
import core.types.YCallable
import utils.FunctionArities
import utils.Stringifier

class YSet() : YInstance(YClass("Set", null, mapOf())) {
    private var elements: MutableSet<Any?> = mutableSetOf()

    constructor(elements: Set<Any?>) : this() {
        this.elements = elements.toMutableSet()
    }

    override fun get(name: Token): Any {
        return when (name.lexeme) {
            "add" -> add(name)
            "remove" -> remove(name)
            "contains" -> contains(name)
            "size" -> size(name)
            "clear" -> clear(name)
            "toArray" -> toArray(name)
            "toList" -> toList(name)
            "union" -> union(name)
            "intersection" -> intersection(name)
            "difference" -> difference(name)
            else -> throw RuntimeError(name, "Undefined Set property ${name.lexeme}.")
        }
    }

    private val add = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Set<Int>
                get() = FunctionArities.UNARY

            override fun call(interpreter: Interpreter, arguments: List<Any?>) {
                val value = arguments[0]
                elements.add(value)
            }
        }
    }

    private val remove = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Set<Int>
                get() = FunctionArities.UNARY

            override fun call(interpreter: Interpreter, arguments: List<Any?>) {
                val value = arguments[0]
                elements.remove(value)
            }
        }
    }

    private val contains = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Set<Int>
                get() = FunctionArities.UNARY

            override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
                val value = arguments[0]
                return elements.contains(value)
            }
        }
    }

    private val size = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Set<Int>
                get() = FunctionArities.ZERO_PARAMETERS

            override fun call(interpreter: Interpreter, arguments: List<Any?>): Int {
                return elements.size
            }
        }
    }


    private val clear = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Set<Int>
                get() = FunctionArities.ZERO_PARAMETERS

            override fun call(interpreter: Interpreter, arguments: List<Any?>) {
                elements.clear()
            }
        }
    }

    private val toArray = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Set<Int>
                get() = FunctionArities.ZERO_PARAMETERS

            override fun call(interpreter: Interpreter, arguments: List<Any?>): YArray {
                return YArray(elements.toTypedArray())
            }
        }
    }

    private val toList = fun(_: Token): YCallable {
        return object : YCallable {
            override val arity: Set<Int>
                get() = FunctionArities.ZERO_PARAMETERS

            override fun call(interpreter: Interpreter, arguments: List<Any?>): YList {
                return YList(elements.toList())
            }
        }
    }

    private val union = fun(name: Token): YCallable {
        return object : YCallable {
            override val arity: Set<Int>
                get() = FunctionArities.UNARY

            override fun call(interpreter: Interpreter, arguments: List<Any?>): YSet {
                val rhs = arguments.first()

                if (rhs !is YSet) {
                    throw RuntimeError(name, "Cannot call 'difference' with non Set.")
                }

                return YSet(elements.union(rhs.elements))
            }
        }
    }

    private val intersection = fun(name: Token): YCallable {
        return object : YCallable {
            override val arity: Set<Int>
                get() = FunctionArities.UNARY

            override fun call(interpreter: Interpreter, arguments: List<Any?>): YSet {
                val rhs = arguments.first()

                if (rhs !is YSet) {
                    throw RuntimeError(name, "Cannot call 'difference' with non Set.")
                }

                return YSet(elements.intersect(rhs.elements))
            }
        }
    }

    private val difference = fun(name: Token): YCallable {
        return object : YCallable {
            override val arity: Set<Int>
                get() = FunctionArities.UNARY

            override fun call(interpreter: Interpreter, arguments: List<Any?>): YSet {
                val diff = mutableSetOf<Any?>()
                val rhs = arguments.first()

                if (rhs !is YSet) {
                    throw RuntimeError(name, "Cannot call 'difference' with non Set.")
                }

                for (element in elements) {
                    if (!rhs.elements.contains(diff)) {
                        diff.add(element)
                    }
                }

                return YSet(diff)
            }
        }
    }

    override fun set(name: Token, value: Any?) {
        throw RuntimeError(name, "Cannot add properties to sets.")
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("Set {")
        for ((index, element) in elements.withIndex()) {
            stringBuilder.append(Stringifier.stringify(element))

            if (index != elements.size - 1) {
                stringBuilder.append(", ")
            }
        }

        stringBuilder.append('}')
        return stringBuilder.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is YSet) {
            return false
        }

        if (elements.size != other.elements.size) {
            return false
        }

        for (element in other.elements) {
            if (!elements.contains(element)) {
                return false
            }
        }

        return true
    }

    override fun hashCode(): Int {
        return elements.hashCode()
    }
}
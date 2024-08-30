package core.types.classes

import core.error.types.RuntimeError
import core.interpreter.Interpreter
import core.scanner.Token
import core.types.YCallable

class YString(val str: String) : YInstance(YClass("String", null, mapOf())) {
    constructor(yString: YString) : this(yString.str)

    override fun get(name: Token): Any {
        return when (name.lexeme) {
            "length" -> length
            "isEmpty" -> isEmpty
            "isNotEmpty" -> isNotEmpty
            "isBlank" -> isBlank
            "isNotBlank" -> isNotBlank
            "lowercase" -> lowercase
            "uppercase" -> uppercase
            "trim" -> trim
            "trimStart" -> trimStart
            "trimEnd" -> trimEnd
            "split" -> split
            "startsWith" -> startsWith
            "endsWith" -> endsWith
            "chars" -> chars
            "substring" -> substring
            "repeat" -> repeat
            else -> throw RuntimeError(name, "Undefined String property ${name.lexeme}.")
        }
    }

    override fun set(name: Token, value: Any?) {
        throw RuntimeError(name, "Cannot add properties to strings.")
    }

    override fun toString(): String = str

    private val length = object : YCallable {
        override val arity: Int
            get() = 0

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return str.length
        }
    }

    private val isEmpty = object : YCallable {
        override val arity: Int
            get() = 0

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return str.isEmpty()
        }
    }

    private val isNotEmpty = object : YCallable {
        override val arity: Int
            get() = 0

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return str.isNotEmpty()
        }
    }

    private val isBlank = object : YCallable {
        override val arity: Int
            get() = 0

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return str.isBlank()
        }
    }

    private val isNotBlank = object : YCallable {
        override val arity: Int
            get() = 0

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return str.isNotBlank()
        }
    }

    private val lowercase = object : YCallable {
        override val arity: Int
            get() = 0

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return str.lowercase()
        }
    }

    private val uppercase = object : YCallable {
        override val arity: Int
            get() = 0

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return str.uppercase()
        }
    }

    private val trim = object : YCallable {
        override val arity: Int
            get() = 0

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return str.trim()
        }
    }

    private val trimStart = object : YCallable {
        override val arity: Int
            get() = 0

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return str.trimStart()
        }
    }

    private val trimEnd = object : YCallable {
        override val arity: Int
            get() = 0

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return str.trimEnd()
        }
    }

    private val split = object : YCallable {
        override val arity: Int
            get() = 1

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            val separator = arguments.first() as String
            val splitResult = str.split(separator)
            return YArray(splitResult.toTypedArray())
        }
    }

    private val startsWith = object : YCallable {
        override val arity: Int
            get() = 1

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            val prefix = arguments.first() as String
            return str.startsWith(prefix)
        }
    }

    private val endsWith = object : YCallable {
        override val arity: Int
            get() = 1

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            val suffix = arguments.first() as String
            return str.endsWith(suffix)
        }
    }

    private val chars = object : YCallable {
        override val arity: Int
            get() = 0

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            val charsExtracted = str.toCharArray().map { YString("$it") as Any? }.toTypedArray()
            return YArray(elements = charsExtracted)
        }
    }

    private val substring = object : YCallable {
        override val arity: Int
            get() = 2

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            val result = str.substring(arguments[0] as Int, arguments[1] as Int)
            return YString(result)
        }
    }

    private val repeat = object : YCallable {
        override val arity: Int
            get() = 1

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return YString(str.repeat(arguments[0] as Int))
        }
    }
}
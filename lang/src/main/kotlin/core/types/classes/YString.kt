package core.types.classes

import core.enum.TokenType
import core.error.types.RuntimeError
import core.interpreter.Interpreter
import core.scanner.Token
import core.types.YCallable
import utils.FunctionArities
import utils.yToInt

class YString(val str: String) : YInstance(YClass("String", null, mapOf())) {
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
            "padStart" -> padStart
            "padEnd" -> padEnd
            "contains" -> contains
            "charAt" -> charAt
            else -> throw RuntimeError(name, "Undefined String property ${name.lexeme}.")
        }
    }

    override fun set(name: Token, value: Any?) {
        throw RuntimeError(name, "Cannot add properties to strings.")
    }

    private val length = object : YCallable {
        override val arity: Set<Int>
            get() = FunctionArities.ZERO_PARAMETERS

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return str.length
        }
    }

    private val isEmpty = object : YCallable {
        override val arity: Set<Int>
            get() = FunctionArities.ZERO_PARAMETERS

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return str.isEmpty()
        }
    }

    private val isNotEmpty = object : YCallable {
        override val arity: Set<Int>
            get() = FunctionArities.ZERO_PARAMETERS

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return str.isNotEmpty()
        }
    }

    private val isBlank = object : YCallable {
        override val arity: Set<Int>
            get() = FunctionArities.ZERO_PARAMETERS

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return str.isBlank()
        }
    }

    private val isNotBlank = object : YCallable {
        override val arity: Set<Int>
            get() = FunctionArities.ZERO_PARAMETERS

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return str.isNotBlank()
        }
    }

    private val lowercase = object : YCallable {
        override val arity: Set<Int>
            get() = FunctionArities.ZERO_PARAMETERS

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return str.lowercase()
        }
    }

    private val uppercase = object : YCallable {
        override val arity: Set<Int>
            get() = FunctionArities.ZERO_PARAMETERS

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return str.uppercase()
        }
    }

    private val trim = object : YCallable {
        override val arity: Set<Int>
            get() = FunctionArities.ZERO_PARAMETERS

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return str.trim()
        }
    }

    private val trimStart = object : YCallable {
        override val arity: Set<Int>
            get() = FunctionArities.ZERO_PARAMETERS

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return str.trimStart()
        }
    }

    private val trimEnd = object : YCallable {
        override val arity: Set<Int>
            get() = FunctionArities.ZERO_PARAMETERS

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return str.trimEnd()
        }
    }

    private val split = object : YCallable {
        override val arity: Set<Int>
            get() = FunctionArities.UNARY

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            val separator = arguments.first().toString()
            val splitResult = str.split(separator)
            return YArray(splitResult.toTypedArray())
        }
    }

    private val startsWith = object : YCallable {
        override val arity: Set<Int>
            get() = FunctionArities.UNARY

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            val prefix = arguments.first().toString()
            return str.startsWith(prefix)
        }
    }

    private val endsWith = object : YCallable {
        override val arity: Set<Int>
            get() = FunctionArities.UNARY

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            val suffix = arguments.first().toString()
            return str.endsWith(suffix)
        }
    }

    private val chars = object : YCallable {
        override val arity: Set<Int>
            get() = FunctionArities.ZERO_PARAMETERS

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            val charsExtracted = str.toCharArray().map { YString("$it") as Any? }.toTypedArray()
            return YArray(elements = charsExtracted)
        }
    }

    private val substring = object : YCallable {
        override val arity: Set<Int>
            get() = FunctionArities.BINARY

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            val result = str.substring(arguments[0]!!.yToInt(), arguments[1]!!.yToInt())
            return YString(result)
        }
    }

    private val repeat = object : YCallable {
        override val arity: Set<Int>
            get() = FunctionArities.UNARY

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            val times = arguments[0]!!.yToInt()
            if (times < 0) {
                throw RuntimeError(
                    Token(TokenType.STRING, times.toString()),
                    "Invalid value for String.repeat ."
                )
            }

            return YString(str.repeat(arguments[0]!!.yToInt()))
        }
    }

    private val padStart = object : YCallable {
        override val arity: Set<Int>
            get() = FunctionArities.BINARY

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            val len = arguments[0]!!.yToInt()
            val paddingChar = arguments[1].toString()

            if (paddingChar.length > 1) {
                throw RuntimeError(
                    Token(TokenType.STRING, paddingChar),
                    "The value used for the padding should be a character."
                )
            }

            return YString(str.padStart(len, paddingChar[0]))
        }
    }

    private val padEnd = object : YCallable {
        override val arity: Set<Int>
            get() = FunctionArities.BINARY

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            val len = arguments[0]!!.yToInt()
            val paddingChar = arguments[1].toString()

            if (paddingChar.length > 1) {
                throw RuntimeError(
                    Token(TokenType.STRING, paddingChar),
                    "The value used for the padding should be a character."
                )
            }

            return YString(str.padEnd(len, paddingChar[0]))
        }
    }

    private val contains = object : YCallable {
        override val arity: Set<Int>
            get() = FunctionArities.UNARY

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            val test = arguments[0].toString()
            return str.contains(test)
        }
    }

    private val charAt = object : YCallable {
        override val arity: Set<Int>
            get() = FunctionArities.UNARY

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            val position = arguments[0]!!.yToInt()
            val len = str.length

            if (position >= len || position < -1 * len) {
                throw RuntimeError(
                    Token(TokenType.STRING, position.toString()),
                    "Position for charAt out of the string length range."
                )
            }

            return if (position >= 0) YString("${str[position]}") else YString("${str[position + len]}")
        }
    }

    override fun toString(): String = str

    override fun equals(other: Any?): Boolean {
        if (other !is YString) {
            return false
        }

        return str == other.str
    }

    override fun hashCode(): Int {
        return str.hashCode()
    }
}
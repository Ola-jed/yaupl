package core.types.native

import core.enum.TokenType
import core.scanner.Token
import core.types.YCallable
import core.interpreter.Interpreter

object Clock : YCallable {
    val token = Token(type = TokenType.FUN, line = 0, lexeme = "Clock", literal = null)

    override val arity: Int
        get() = 0

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        return System.currentTimeMillis().toDouble() / 1000.0
    }

    override fun toString(): String {
        return "fun(Clock)"
    }
}
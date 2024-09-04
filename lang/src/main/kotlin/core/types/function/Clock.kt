package core.types.function

import core.enum.TokenType
import core.scanner.Token
import core.interpreter.Interpreter
import core.types.YCallable
import utils.FunctionArities

object Clock : YCallable {
    val token = Token(type = TokenType.FUN, line = 0, lexeme = "Clock", literal = null)

    override val arity: Set<Int>
        get() = FunctionArities.ZERO_PARAMETERS

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        return System.currentTimeMillis().toDouble() / 1000.0
    }

    override fun toString(): String {
        return "fun(Clock)"
    }
}
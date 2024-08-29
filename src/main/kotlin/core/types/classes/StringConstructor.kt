package core.types.classes

import core.enum.TokenType
import core.interpreter.Interpreter
import core.scanner.Token
import core.types.YCallable
import utils.Stringifier

object StringConstructor : YCallable {
    val token = Token(type = TokenType.FUN, line = 0, lexeme = "String", literal = null)

    override val arity: Int
        get() = 1

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        val arg = arguments[0]
        return if (arg is YString) arg else YString(Stringifier.stringify(arguments[0]))
    }
}
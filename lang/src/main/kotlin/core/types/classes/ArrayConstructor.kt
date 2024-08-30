package core.types.classes

import core.enum.TokenType
import core.interpreter.Interpreter
import core.scanner.Token
import core.types.YCallable

object ArrayConstructor : YCallable {
    val token = Token(type = TokenType.FUN, line = 0, lexeme = "Array", literal = null)

    override val arity: Int
        get() = 1

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        val size = (arguments[0] as Double).toInt()
        return YArray(size)
    }
}
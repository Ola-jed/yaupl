package core.types.classes

import core.enum.TokenType
import core.interpreter.Interpreter
import core.scanner.Token
import core.types.YCallable

object ListConstructor : YCallable {
    val token = Token(type = TokenType.FUN, line = 0, lexeme = "List", literal = null)

    override val arity: Int
        get() = 0

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
       return YList()
    }
}
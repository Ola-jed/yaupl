package core.types.classes

import core.enum.TokenType
import core.interpreter.Interpreter
import core.scanner.Token
import core.types.YCallable
import utils.FunctionArities

object ArrayConstructor : YCallable {
    val token = Token(type = TokenType.FUN, line = 0, lexeme = "Array", literal = null)

    override val arity: Set<Int>
        get() = FunctionArities.UNARY

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        val size = (arguments[0] as Double).toInt()
        return YArray(size)
    }
}
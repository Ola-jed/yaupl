package core.types.classes

import core.enum.TokenType
import core.interpreter.Interpreter
import core.scanner.Token
import core.types.YCallable
import utils.FunctionArities

object SetConstructor : YCallable {
    val token = Token(type = TokenType.FUN, line = 0, lexeme = "Set", literal = null)

    override val arity: Set<Int>
        get() = FunctionArities.MAX_VARIADIC_PARAMETERS

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        return if (arguments.isEmpty()) YSet() else YSet(arguments.toSet())
    }
}
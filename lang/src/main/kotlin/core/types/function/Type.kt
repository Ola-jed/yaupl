package core.types.function

import core.enum.TokenType
import core.interpreter.Interpreter
import core.scanner.Token
import core.types.YCallable
import utils.FunctionArities
import utils.TypeFormatter

object Type : YCallable {
    val token = Token(type = TokenType.FUN, line = 0, lexeme = "Type", literal = null)

    override val arity: Set<Int>
        get() = FunctionArities.UNARY

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
        val argument = arguments.first()!!
        return TypeFormatter.formatToReadable(argument)
    }

    override fun toString(): String {
        return "fun(Type)"
    }
}
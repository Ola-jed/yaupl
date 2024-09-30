package core.types.classes

import core.enum.TokenType
import core.interpreter.Interpreter
import core.scanner.Token
import core.types.YCallable
import utils.FunctionArities
import utils.Stringifier

object FileConstructor : YCallable {
    val token = Token(type = TokenType.FUN, line = 0, lexeme = "File", literal = null)

    override val arity: Set<Int>
        get() = FunctionArities.UNARY

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        return YFile(Stringifier.stringify(arguments[0]))
    }
}
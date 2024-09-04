package core.types.classes

import core.enum.TokenType
import core.interpreter.Interpreter
import core.scanner.Token
import core.types.YCallable
import utils.FunctionArities

object ListConstructor : YCallable {
    val token = Token(type = TokenType.FUN, line = 0, lexeme = "List", literal = null)

    override val arity: Set<Int>
        get() = FunctionArities.MAX_VARIADIC_PARAMETERS

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
       if(arguments.isEmpty()) {
           return YList()
       } else if(arguments.size == 1) {
           val arg = arguments[0]
           return if(arg is YArray) YList(arg.elements.toList()) else YList(listOf(arg))
       } else {
           return YList(arguments)
       }
    }
}
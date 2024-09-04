package core.types

import core.interpreter.Interpreter

interface YCallable {
    val arity: Set<Int>
    fun call(interpreter: Interpreter, arguments: List<Any?>): Any?
}
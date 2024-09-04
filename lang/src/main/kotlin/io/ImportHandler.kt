package io

import ast.Stmt
import core.error.reporter.ErrorReporter
import core.error.types.RuntimeError
import core.interpreter.Interpreter
import core.interpreter.Resolver
import core.parser.Parser
import core.scanner.Scanner
import core.scanner.Token
import java.io.File

class ImportHandler(
    private val filePath: String,
    private val importToken: Token,
    private val errorReporter: ErrorReporter,
    private val interpreter: Interpreter
) {
    fun handle() {
        val file = File(filePath)

        if (!file.exists()) {
            throw RuntimeError(importToken, "Imported file \"$filePath\" does not exists")
        }

        if (!file.canRead()) {
            throw RuntimeError(importToken, "Imported file \"$filePath\" cannot be read")
        }

        val content = file.readText()
        val scanner = Scanner(source = content, errorReporter = errorReporter)
        var hadRuntimeError = false
        var hadError = false

        val resolver = Resolver(
            interpreter = interpreter,
            errorReporter = errorReporter,
            onRuntimeErrorReported = { hadRuntimeError = true }
        )

        try {
            val tokens = scanner.scanTokens()
            val parser = Parser(tokens, errorReporter)
            // Filter out all statements except definitions (variables, constants, functions and classes)
            val statements = parser.parse()
                .filter {
                    it is Stmt.VariableDeclaration
                            || it is Stmt.ConstantDeclaration
                            || it is Stmt.Function
                            || it is Stmt.Class
                            || it is Stmt.Import
                }
            resolver.resolve(statements)
            interpreter.interpret(statements)
        } catch (ex: Exception) {
            hadError = true
        }

        if (hadError || hadRuntimeError) {
            throw RuntimeError(importToken, "Error while interpreting file $filePath's content")
        }
    }
}
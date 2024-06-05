package core.error.reporter

import core.enum.TokenType
import core.scanner.Token
import core.error.types.RuntimeError

interface ErrorReporter {
    fun report(
        line: Int? = null,
        message: String,
        where: String = ""
    )

    fun reportTokenError(
        token: Token,
        message: String
    ) {
        if (token.type == TokenType.EOF) {
            report(token.line, message, "at end")
        } else {
            report(token.line, message, " at '${token.lexeme}'")
        }
    }

    fun reportRuntimeError(error: RuntimeError) {
        report(error.token.line, error.message, "")
    }
}
package error.reporter

import core.enum.TokenType
import core.scanner.Token
import error.types.RuntimeError

class ConsoleErrorReporter : ErrorReporter {
    override fun report(line: Int, message: String, where: String) {
        System.err.println(
            "[line ${line + 1}] $where Error : $message"
        )
    }

    override fun reportTokenError(token: Token, message: String) {
        if (token.type == TokenType.EOF) {
            report(token.line, message, "at end")
        } else {
            report(token.line, message, " at '${token.lexeme}'")
        }
    }

    override fun reportRuntimeError(error: RuntimeError) {
        report(error.token.line, error.message, "")
    }
}
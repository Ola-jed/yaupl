package error.reporter

import core.scanner.Token
import error.types.RuntimeError

interface ErrorReporter {
    fun report(
        // We assume that the lines indexes start at 0
        // But the user should not be hindered by this information so increment before printing
        line: Int,
        message: String,
        where: String = ""
    )

    fun reportTokenError(
        token: Token,
        message: String
    )

    fun reportRuntimeError(
        error: RuntimeError
    )
}

// TODO : Maybe a file error reporter ?
// When running lox we can pass the argument to the file where errors will be reported
// ex : run --error-log=error.log
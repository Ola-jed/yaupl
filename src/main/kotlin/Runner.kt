import core.parser.Parser
import core.scanner.Scanner
import core.error.reporter.ErrorReporter
import core.interpreter.Interpreter
import core.interpreter.Resolver
import java.io.File
import kotlin.system.exitProcess

class Runner(private val errorReporter: ErrorReporter) {
    private var hadError: Boolean = false
    private var hadRuntimeError: Boolean = false

    fun runFile(path: String) {
        val content = File(path).readText()
        val scanner = Scanner(source = content, errorReporter = errorReporter)
        val interpreter = Interpreter(
            errorReporter = errorReporter,
            onRuntimeErrorReported = { hadRuntimeError = true },
            replMode = false
        )

        val resolver = Resolver(
            interpreter = interpreter,
            errorReporter = errorReporter,
            onRuntimeErrorReported = { hadRuntimeError = true }
        )

        try {
            val tokens = scanner.scanTokens()
            val parser = Parser(tokens, errorReporter)
            val statements = parser.parse()
            resolver.resolve(statements)
            interpreter.interpret(statements)
        } catch (ex: Exception) {
            println("${ANSI_RED}Fatal error : ${ex.message}$ANSI_RESET")
            hadError = true
        }

        if (hadError) {
            exitProcess(65)
        }

        if (hadRuntimeError) {
            exitProcess(70)
        }
    }

    fun runPrompt() {
        val interpreter = Interpreter(
            errorReporter = errorReporter,
            onRuntimeErrorReported = { hadRuntimeError = true },
            replMode = true
        )

        val resolver = Resolver(
            interpreter = interpreter,
            errorReporter = errorReporter,
            onRuntimeErrorReported = { hadRuntimeError = true }
        )

        while (true) {
            print("ypl : ")
            val input = readlnOrNull() ?: break
            val scanner = Scanner(source = input, errorReporter = errorReporter)
            try {
                val tokens = scanner.scanTokens()
                val parser = Parser(tokens, errorReporter)
                val statements = parser.parse()
                resolver.resolve(statements)
                interpreter.interpret(statements)
            } catch (ex: Exception) {
                hadError = true
            }

            hadError = false
        }
    }

    fun error(line: Int, message: String) {
        report(line, message)
    }

    private fun report(line: Int, message: String) {
        errorReporter.report(line, message)
        hadError = true
    }
}

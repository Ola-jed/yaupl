import core.parser.Parser
import core.scanner.Scanner
import error.reporter.ErrorReporter
import interpreter.Interpreter
import java.io.File
import kotlin.system.exitProcess

class Runner(private val errorReporter: ErrorReporter) {
    private var hadError: Boolean = false
    private var hadRuntimeError: Boolean = false

    fun runFile(path: String) {
        run(File(path).readText(), replMode = false)

        if (hadError) {
            exitProcess(65)
        }

        if (hadRuntimeError) {
            exitProcess(70)
        }
    }

    fun runPrompt() {
        while (true) {
            print("ypl : ")
            val input = readlnOrNull() ?: break
            run(input, replMode = true)
            hadError = false
        }
    }

    private fun run(source: String, replMode: Boolean) {
        val scanner = Scanner(source = source, errorReporter = errorReporter)
        val interpreter = Interpreter(
            errorReporter = errorReporter,
            onRuntimeErrorReported = { hadRuntimeError = true },
            replMode = replMode
        )

        try {
            val tokens = scanner.scanTokens()
            val parser = Parser(tokens, errorReporter)
            val statements = parser.parse()
            interpreter.interpret(statements)
        } catch (ex: Exception) {
            hadError = true
            return
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

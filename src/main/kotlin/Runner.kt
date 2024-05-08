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
        run(File(path).readText())

        if (hadError) {
            exitProcess(65)
        }

        if (hadRuntimeError) {
            exitProcess(70)
        }
    }

    fun runPrompt() {
        while (true) {
            print("> ")
            val input = readlnOrNull() ?: break
            run(input)
            hadError = false
        }
    }

    private fun run(source: String) {
        val scanner = Scanner(source = source, errorReporter = errorReporter)
        val interpreter = Interpreter(
            errorReporter = errorReporter,
            onRuntimeErrorReported = { hadRuntimeError = true }
        )
        val tokens = scanner.scanTokens()

        println("Tokens :")
        tokens.forEach(::println)

        val parser = Parser(tokens, errorReporter)
        val expression = parser.parse()
        if (expression == null) {
            println("No expression generated")
        } else {
            interpreter.interpret(expression)
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


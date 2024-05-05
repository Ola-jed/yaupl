import ast.AstPrinter
import core.parser.Parser
import core.scanner.Scanner
import error.ErrorReporter
import java.io.File

class Runner(private val errorReporter: ErrorReporter, private var hadError: Boolean = false) {
    fun runFile(path: String) {
        run(File(path).readText())
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
        val tokens = scanner.scanTokens()
        println("Tokens :")
        tokens.forEach(::println)

        val parser = Parser(tokens, errorReporter)
        val expression = parser.parse()
        println("Expression")
        if (expression == null) {
            println("No expression generated")
        } else {
            println(AstPrinter().print(expression))
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


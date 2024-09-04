import core.parser.Parser
import core.scanner.Scanner
import core.error.reporter.ErrorReporter
import core.interpreter.Interpreter
import core.interpreter.Resolver
import io.FilePathResolver
import utils.AstPrinter
import java.io.File
import kotlin.system.exitProcess

class Runner(private val errorReporter: ErrorReporter, private val printAstOnly: Boolean) {
    private var hadError: Boolean = false
    private var hadRuntimeError: Boolean = false

    fun runFile(path: String) {
        val file = File(path)

        if (!file.exists()) {
            throw Exception("The file at the path $path does not exists.")
        }

        if (!file.canRead()) {
            throw Exception("The file at the path $path cannot be read.")
        }

        val content = file.readText()
        val scanner = Scanner(source = content, errorReporter = errorReporter)

        try {
            val tokens = scanner.scanTokens()
            val parser = Parser(tokens, errorReporter)
            val statements = parser.parse()

            if (printAstOnly) {
                val astPrinter = AstPrinter()
                statements.forEach { println(astPrinter.print(it)) }
            } else {
                val interpreter = Interpreter(
                    errorReporter = errorReporter,
                    onRuntimeErrorReported = { hadRuntimeError = true },
                    replMode = false,
                    filePathResolver = FilePathResolver(path)
                )

                val resolver = Resolver(
                    interpreter = interpreter,
                    errorReporter = errorReporter,
                    onRuntimeErrorReported = { hadRuntimeError = true }
                )

                resolver.resolve(statements)
                interpreter.interpret(statements)
            }
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
        while (true) {
            print("ypl : ")
            val input = readlnOrNull() ?: break
            val scanner = Scanner(source = input, errorReporter = errorReporter)
            try {
                val tokens = scanner.scanTokens()
                val parser = Parser(tokens, errorReporter)
                val statements = parser.parse()

                if (printAstOnly) {
                    val astPrinter = AstPrinter()
                    statements.forEach { println(astPrinter.print(it)) }
                } else {
                    val interpreter = Interpreter(
                        errorReporter = errorReporter,
                        onRuntimeErrorReported = { hadRuntimeError = true },
                        replMode = true,
                        filePathResolver = FilePathResolver("")
                    )

                    val resolver = Resolver(
                        interpreter = interpreter,
                        errorReporter = errorReporter,
                        onRuntimeErrorReported = { hadRuntimeError = true }
                    )

                    resolver.resolve(statements)
                    interpreter.interpret(statements)
                }
            } catch (ex: Exception) {
                hadError = true
            }

            hadError = false
        }
    }
}

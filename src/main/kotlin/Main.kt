import error.ConsoleErrorReporter
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val runner = Runner(ConsoleErrorReporter())
    when {
        args.isEmpty() -> runner.runPrompt()
        args.size == 1 -> runner.runFile(args.first())
        else -> {
            println("Usage : yaupl [script]")
            exitProcess(64)
        }
    }
}

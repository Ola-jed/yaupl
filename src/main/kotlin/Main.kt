import cmd.ArgsParser
import core.error.reporter.ConsoleErrorReporter
import core.error.reporter.FileErrorReporter
import kotlin.system.exitProcess

const val ANSI_RESET = "\u001B[0m"
const val ANSI_RED = "\u001B[31m"

fun main(args: Array<String>) {
    try {
        val argParser = ArgsParser(args)
        if (argParser.hasOption(ArgsParser.OPTION_HELP)) {
            // To Improve
            println("Usage : yaupl [script] [--help] [--error-log=logfile.log]")
            exitProcess(0)
        }

        val errorLogFile = argParser.getOptionValue(ArgsParser.OPTION_ERROR_LOG)
        val errorReporter = if (errorLogFile != null) {
            FileErrorReporter(errorLogFile)
        } else {
            ConsoleErrorReporter()
        }

        val runner = Runner(errorReporter)
        val fileToRun = argParser.getFileToRun()

        if (fileToRun == null) {
            runner.runPrompt()
        } else {
            runner.runFile(fileToRun)
        }
    } catch (e: Exception) {
        println("${ANSI_RED}Fatal error : ${e.message}$ANSI_RESET")
    }
}

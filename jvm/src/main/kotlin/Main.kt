import cmd.ArgsParser
import core.error.reporter.ConsoleErrorReporter
import core.error.reporter.FileErrorReporter
import kotlin.system.exitProcess


const val ANSI_RESET = "\u001B[0m"
const val ANSI_RED = "\u001B[31m"

fun main(args: Array<String>) {
    val reservedPackages = setOf("java", "javax", "kotlin", "kotlinx", "scala", "groovy", "clojure")

    try {
        val argParser = ArgsParser(args)
        if (argParser.hasOption(ArgsParser.OPTION_HELP)) {
            println("Usage : yaupl2jar script.ypl [--help] [--package=com.foo.bar] [--output-jar=name] [--error-log=logfile.log]")
            exitProcess(0)
        }

        val errorLogFile = argParser.getOptionValue(ArgsParser.OPTION_ERROR_LOG)
        val errorReporter = if (errorLogFile != null) FileErrorReporter(errorLogFile) else ConsoleErrorReporter()
        val fileToRun = argParser.getFileToRun()
        if (fileToRun == null) {
            println("${ANSI_RED}No file given$ANSI_RESET")
            exitProcess(65)
        }

        val pkg = argParser.getOptionValue(ArgsParser.PACKAGE) ?: "com.yaupl.generated"
        val outputJar = argParser.getOptionValue(ArgsParser.OUTPUT_JAR) ?: "yaupl-generated"

        val pkgInvalid = reservedPackages.any { pkg.startsWith(it) }
        if (pkgInvalid) {
            println("${ANSI_RED}$pkg cannot be used as package name because it could create conflicts with reserved packages for JVM based languages$ANSI_RESET")
            exitProcess(65)
        }

        val converter = JarConverter(errorReporter, pkg, outputJar)
        converter.generateFileJar(fileToRun)
    } catch (e: Exception) {
        println("${ANSI_RED}Fatal error : ${e.message}$ANSI_RESET")
    }
}

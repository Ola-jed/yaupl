package core.error.reporter

import utils.Context

class ConsoleErrorReporter : ErrorReporter {
    override fun report(line: Int?, message: String, where: String) {
        val fileInfo = if (Context.currentFile.isNotEmpty()) "Error in file '${Context.currentFile}'" else "Error"
        val lineInfo = line?.let { "at line ${it + 1} :" } ?: ":"
        System.err.println("$fileInfo $lineInfo $message")
    }
}
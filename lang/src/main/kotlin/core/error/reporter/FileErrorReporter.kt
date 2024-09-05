package core.error.reporter

import utils.Context
import java.io.File

class FileErrorReporter(filepath: String) : ErrorReporter {
    private val file: File = File(filepath)

    init {
        if (!file.exists()) {
            file.createNewFile()
        }
    }

    override fun report(line: Int?, message: String, where: String) {
        val fileInfo = if (Context.currentFile.isNotEmpty()) "Error in file '${Context.currentFile}'" else "Error"
        val lineInfo = line?.let { "at line ${it + 1} :" } ?: ":"
        file.appendText("$fileInfo $lineInfo $message")
    }
}
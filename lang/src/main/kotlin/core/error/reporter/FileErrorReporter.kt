package core.error.reporter

import java.io.File

class FileErrorReporter(filepath: String) : ErrorReporter {
    private val file: File = File(filepath)

    init {
        if(!file.exists()) {
            file.createNewFile()
        }
    }

    override fun report(line: Int?, message: String, where: String) {
        var out = ""
        if (line != null) {
            out += "[line ${line + 1}] "
        }

        out += "$where Error : $message"

        file.appendText(out)
    }
}
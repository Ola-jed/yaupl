package core.error.reporter

class ConsoleErrorReporter : ErrorReporter {
    override fun report(line: Int?, message: String, where: String) {
        var out = ""
        if (line != null) {
            out += "[line ${line + 1}] "
        }

        out += "$where Error : $message"

        System.err.println(out)
    }
}
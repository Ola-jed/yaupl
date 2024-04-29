package error

class ConsoleErrorReporter: ErrorReporter {
    override fun report(line: Int, message: String) {
        System.err.println(
            "[line ${line + 1}] Error : $message"
        )
    }
}
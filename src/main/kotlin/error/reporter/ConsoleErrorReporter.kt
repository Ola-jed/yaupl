package error.reporter

class ConsoleErrorReporter : ErrorReporter {
    override fun report(line: Int, message: String, where: String) {
        System.err.println("[line ${line + 1}] $where Error : $message")
    }
}
package core.error.reporter

class EmptyErrorReporter: ErrorReporter {
    override fun report(line: Int?, message: String, where: String) {
        // Do nothing on purpose
    }
}
package error

interface ErrorReporter {
    fun report(
        // We assume that the lines indexes start at 0
        // But the user should not be hindered by this information so increment before printing
        line: Int,
        message: String
    )
}
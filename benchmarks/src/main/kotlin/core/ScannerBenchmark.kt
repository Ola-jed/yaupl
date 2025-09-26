package core

import core.error.reporter.EmptyErrorReporter
import core.scanner.Scanner
import core.scanner.Token
import kotlinx.benchmark.*
import org.openjdk.jmh.annotations.Fork
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
class ScannerBenchmark {
    @Param(
        "print \\\"Hello World!\\!\\\";",
        "import \\\"import.second.ypl\\\"; print fib(5); Bacon().eat();"
    )
    lateinit var input: String

    @Benchmark
    fun benchmarkScan(): List<Token> {
        return Scanner(input, EmptyErrorReporter()).scanTokens()
    }
}
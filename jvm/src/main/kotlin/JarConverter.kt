import core.YauplJvmCompiler
import core.error.reporter.ErrorReporter
import core.parser.Parser
import core.scanner.Scanner
import io.FilePathResolver
import java.io.File
import java.util.jar.Manifest

class JarConverter(private val errorReporter: ErrorReporter, private val pkg: String, private val output: String) {
    fun generateFileJar(path: String) {
        val file = File(path)

        if (!file.exists()) {
            throw Exception("The file at the path $path does not exists.")
        }

        if (!file.canRead()) {
            throw Exception("The file at the path $path cannot be read.")
        }

        val content = file.readText()
        val scanner = Scanner(source = content, errorReporter = errorReporter)
        try {
            val tokens = scanner.scanTokens()
            val parser = Parser(tokens, errorReporter)
            val statements = parser.parse()
            val compiler = YauplJvmCompiler(
                pkg = pkg,
                errorReporter = errorReporter,
                filePathResolver = FilePathResolver(path)
            )

            val mainClazz = compiler.compileBytecode(statements)
            val manifest = Manifest().apply {
                mainAttributes.putValue("Manifest-Version", "1.0")
                mainAttributes.putValue("Main-Class", "$pkg.Main")
            }
            mainClazz.toJar(File("$output.jar"), manifest)
        } catch (ex: Exception) {
            println("${ANSI_RED}Fatal error : ${ex.message}$ANSI_RESET")
        }
    }
}
import core.CompilationError
import core.YauplJavaTranspiler
import core.error.reporter.ErrorReporter
import core.parser.Parser
import core.scanner.Scanner
import io.FilePathResolver
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.jar.Attributes
import java.util.jar.Manifest
import javax.tools.ToolProvider

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
            val transpiler = YauplJavaTranspiler(
                pkg = pkg,
                errorReporter = errorReporter,
                filePathResolver = FilePathResolver(path)
            )

            val javaSource = transpiler.transpile(statements)
            val javaFilePath = Paths.get("Main.java")
            Files.write(javaFilePath, javaSource.toByteArray())
            val compiler = ToolProvider.getSystemJavaCompiler()
            val compilationResult = compiler.run(null, null, null, javaFilePath.toString())

            if (compilationResult != 0) {
                throw CompilationError("Compilation to java failed.")
            }

            val jarFile = File("$output.jar")
            val fos = jarFile.outputStream()
            val manifest = Manifest()
            val attributes = manifest.mainAttributes
            attributes[Attributes.Name("Manifest-Version")] = "1.0"
            attributes[Attributes.Name("Main-Class")] = "Main"
            attributes[Attributes.Name("Created-By")] = "JarConverter"

            val jarOut = java.util.jar.JarOutputStream(fos, manifest)
            val classFile = File("Main.class")
            if (classFile.exists()) {
                jarOut.putNextEntry(java.util.jar.JarEntry("Main.class"))
                Files.copy(classFile.toPath(), jarOut)
                jarOut.closeEntry()
            }

            jarOut.close()
            classFile.delete()
            javaFilePath.toFile().delete()

            println("JAR file created successfully: $output.jar")
        } catch (ex: Exception) {
            ex.printStackTrace()
            println("${ANSI_RED}Fatal error : ${ex.message}$ANSI_RESET")
        }
    }
}
import cmd.ArgsParser
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull


class ArgsParserTest {
    @Test
    fun getFileWithSingleFileArgument() {
        val argsParser = ArgsParser(arrayOf("main.ypl"))
        assertEquals("main.ypl", argsParser.getFileToRun())
    }

    @Test
    fun getFileWithNoArguments() {
        val argsParser = ArgsParser(arrayOf())
        assertNull(argsParser.getFileToRun())
    }

    @Test
    fun getFileWithMultipleArguments() {
        val argsParser = ArgsParser(arrayOf("main.ypl", "--print-ast-only"))
        assertEquals("main.ypl", argsParser.getFileToRun())
    }

    @Test
    fun checkHasOptionWithSingleExistingOption() {
        val argsParser = ArgsParser(arrayOf("--print-ast-only"))
        assertEquals(true, argsParser.hasOption("print-ast-only"))
    }

    @Test
    fun checkHasOptionWithMultipleExistingOption() {
        val argsParser = ArgsParser(arrayOf("--print-ast-only", "--error-log=logfile.log"))
        assertEquals(true, argsParser.hasOption("print-ast-only"))
    }

    @Test
    fun checkHasOptionWithNonExistingOption() {
        val argsParser = ArgsParser(arrayOf())
        assertEquals(false, argsParser.hasOption("print-ast-only"))
    }

    @Test
    fun getOptionValueWithSingleOption() {
        val argsParser = ArgsParser(arrayOf("--error-log=logfile.log"))
        assertEquals("logfile.log", argsParser.getOptionValue("error-log"))
    }

    @Test
    fun getOptionValueWithMultipleOption() {
        val argsParser = ArgsParser(arrayOf("--error-log=logfile.log", "--print-ast-only"))
        assertEquals("logfile.log", argsParser.getOptionValue("error-log"))
    }

    @Test
    fun getOptionValueWithNoOption() {
        val argsParser = ArgsParser(arrayOf())
        assertNull(argsParser.getOptionValue("error-log"))
    }

    @Test
    fun getNonExistingOptionValueWithSingleOption() {
        val argsParser = ArgsParser(arrayOf("--print-ast-only"))
        assertNull(argsParser.getOptionValue("error-log"))
    }

    @Test
    fun getNonExistingOptionValueWithMultipleOption() {
        val argsParser = ArgsParser(arrayOf("--print-ast-only", "--help"))
        assertNull(argsParser.getOptionValue("error-log"))
    }
}
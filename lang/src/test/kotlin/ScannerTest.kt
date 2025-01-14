import core.enum.TokenType
import core.scanner.Scanner
import core.error.reporter.EmptyErrorReporter
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ScannerTest {
    @Test
    fun shouldHaveEofAtEnd() {
        val scanner = Scanner("", EmptyErrorReporter())
        val tokens = scanner.scanTokens()
        assertEquals(1, tokens.size)
        assertEquals(TokenType.EOF, tokens[0].type)
    }

    @Test
    fun shouldReturnTokensEquality() {
        val scanner = Scanner("=", EmptyErrorReporter())
        val tokens = scanner.scanTokens()
        assertEquals(2, tokens.size)
        assertEquals(TokenType.EQUAL, tokens[0].type)
        assertEquals("=", tokens[0].lexeme)
    }

    @Test
    fun shouldScanModuloOperation() {
        val scanner = Scanner("4 % 2", EmptyErrorReporter())
        val tokens = scanner.scanTokens()
        assertEquals(4, tokens.size)
        assertEquals(TokenType.MODULO, tokens[1].type)
    }

    @Test
    fun shouldScanEmptyArrayLiteral() {
        val scanner = Scanner("[]", EmptyErrorReporter())
        val tokens = scanner.scanTokens()
        assertEquals(3, tokens.size)
        assertEquals(TokenType.LEFT_SQUARE_BRACKET, tokens[0].type)
        assertEquals(TokenType.RIGHT_SQUARE_BRACKET, tokens[1].type)
    }

    @Test
    fun shouldScanNothingFromSingleLineComment() {
        val scanner = Scanner("// Foo bar qux", EmptyErrorReporter())
        val tokens = scanner.scanTokens()
        assertEquals(1, tokens.size)
        assertEquals(TokenType.EOF, tokens[0].type)
    }

    @Test
    fun shouldScanNothingFromMultilineLineComment() {
        val scanner = Scanner("/* Foo bar qux\n lorem ipsum */", EmptyErrorReporter())
        val tokens = scanner.scanTokens()
        assertEquals(1, tokens.size)
        assertEquals(TokenType.EOF, tokens[0].type)
    }
}
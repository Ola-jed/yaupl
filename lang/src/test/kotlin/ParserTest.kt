import ast.Stmt
import core.enum.TokenType
import core.parser.Parser
import core.scanner.Token
import core.error.reporter.EmptyErrorReporter
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ParserTest {
    @Test
    fun shouldReturnBreakStatement() {
        val tokens = listOf(Token(TokenType.BREAK, "break", null, 1), (Token(TokenType.EOF, "", null, 1)))
        val parser = Parser(tokens, EmptyErrorReporter())
        val statements = parser.parse()
        assertEquals(1, statements.size)
        val statement = statements[0]
        assertIs<Stmt.Break>(statement)
    }
}
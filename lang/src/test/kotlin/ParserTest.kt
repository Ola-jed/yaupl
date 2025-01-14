import ast.Expr
import ast.Stmt
import core.enum.TokenType
import core.error.reporter.EmptyErrorReporter
import core.parser.Parser
import core.scanner.Token
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ParserTest {
    @Test
    fun shouldReturnBreakStatement() {
        val tokens = listOf(Token(TokenType.BREAK, "break"), Token(TokenType.EOF, ""))
        val parser = Parser(tokens, EmptyErrorReporter())
        val statements = parser.parse()
        assertEquals(1, statements.size)
        val statement = statements[0]
        assertIs<Stmt.Break>(statement)
    }

    @Test
    fun literalExpressionParsing() {
        val tokens = listOf(
            Token(TokenType.NUMBER, "0", 0),
            Token(TokenType.SEMICOLON, ";"),
            Token(TokenType.EOF, "")
        )
        val parser = Parser(tokens, EmptyErrorReporter())
        val statements = parser.parse()
        print(statements)
        assertEquals(1, statements.size)
        val statement = statements[0]
        assertIs<Stmt.Expression>(statement)
        assertIs<Expr.Literal>(statement.expression)
    }

    @Test
    fun arrayLiteralExpressionParsing() {
        val tokens = listOf(
            Token(TokenType.LEFT_SQUARE_BRACKET, "["),
            Token(TokenType.NUMBER, "0", 0),
            Token(TokenType.RIGHT_SQUARE_BRACKET, "]"),
            Token(TokenType.SEMICOLON, ";"),
            Token(TokenType.EOF, "")
        )
        val parser = Parser(tokens, EmptyErrorReporter())
        val statements = parser.parse()
        print(statements)
        assertEquals(1, statements.size)
        val statement = statements[0]
        assertIs<Stmt.Expression>(statement)
        assertIs<Expr.ArrayLiteral>(statement.expression)
    }

    @Test
    fun stringLiteralExpressionParsing() {
        val tokens = listOf(
            Token(TokenType.STRING, "Hello world"),
            Token(TokenType.SEMICOLON, ";"),
            Token(TokenType.EOF, "")
        )
        val parser = Parser(tokens, EmptyErrorReporter())
        val statements = parser.parse()
        print(statements)
        assertEquals(1, statements.size)
        val statement = statements[0]
        assertIs<Stmt.Expression>(statement)
        assertIs<Expr.StringLiteral>(statement.expression)
    }

    @Test
    fun unaryExpressionParsing() {
        val tokens = listOf(
            Token(TokenType.BANG, "!", 2),
            Token(TokenType.IDENTIFIER, "foo"),
            Token(TokenType.SEMICOLON, ";"),
            Token(TokenType.EOF, "")
        )
        val parser = Parser(tokens, EmptyErrorReporter())
        val statements = parser.parse()
        print(statements)
        assertEquals(1, statements.size)
        val statement = statements[0]
        assertIs<Stmt.Expression>(statement)
        assertIs<Expr.Unary>(statement.expression)
    }

    @Test
    fun binaryExpressionParsing() {
        val tokens = listOf(
            Token(TokenType.NUMBER, "2", 2),
            Token(TokenType.PLUS, "+"),
            Token(TokenType.NUMBER, "2", 2),
            Token(TokenType.SEMICOLON, ";"),
            Token(TokenType.EOF, "")
        )
        val parser = Parser(tokens, EmptyErrorReporter())
        val statements = parser.parse()
        print(statements)
        assertEquals(1, statements.size)
        val statement = statements[0]
        assertIs<Stmt.Expression>(statement)
        assertIs<Expr.Binary>(statement.expression)
    }

    @Test
    fun logicalExpressionParsing() {
        val tokens = listOf(
            Token(TokenType.TRUE, "true", true),
            Token(TokenType.OR, "or"),
            Token(TokenType.FALSE, "false", false),
            Token(TokenType.SEMICOLON, ";"),
            Token(TokenType.EOF, "")
        )
        val parser = Parser(tokens, EmptyErrorReporter())
        val statements = parser.parse()
        print(statements)
        assertEquals(1, statements.size)
        val statement = statements[0]
        assertIs<Stmt.Expression>(statement)
        assertIs<Expr.Logical>(statement.expression)
    }

    @Test
    fun groupingExpressionParsing() {
        val tokens = listOf(
            Token(TokenType.LEFT_PAREN, "("),
            Token(TokenType.IDENTIFIER, "foo"),
            Token(TokenType.RIGHT_PAREN, ")"),
            Token(TokenType.SEMICOLON, ";"),
            Token(TokenType.EOF, "")
        )
        val parser = Parser(tokens, EmptyErrorReporter())
        val statements = parser.parse()
        print(statements)
        assertEquals(1, statements.size)
        val statement = statements[0]
        assertIs<Stmt.Expression>(statement)
        assertIs<Expr.Grouping>(statement.expression)
    }

    @Test
    fun thisExpressionParsing() {
        val tokens = listOf(
            Token(TokenType.THIS, "this"),
            Token(TokenType.SEMICOLON, ";"),
            Token(TokenType.EOF, "")
        )
        val parser = Parser(tokens, EmptyErrorReporter())
        val statements = parser.parse()
        print(statements)
        assertEquals(1, statements.size)
        val statement = statements[0]
        assertIs<Stmt.Expression>(statement)
        assertIs<Expr.This>(statement.expression)
    }

    @Test
    fun superExpressionParsing() {
        val tokens = listOf(
            Token(TokenType.SUPER, "super"),
            Token(TokenType.DOT, "."),
            Token(TokenType.IDENTIFIER, "foo"),
            Token(TokenType.SEMICOLON, ";"),
            Token(TokenType.EOF, "")
        )
        val parser = Parser(tokens, EmptyErrorReporter())
        val statements = parser.parse()
        print(statements)
        assertEquals(1, statements.size)
        val statement = statements[0]
        assertIs<Stmt.Expression>(statement)
        assertIs<Expr.Super>(statement.expression)
    }

    @Test
    fun callExpressionParsing() {
        val tokens = listOf(
            Token(TokenType.IDENTIFIER, "foo"),
            Token(TokenType.LEFT_PAREN, "("),
            Token(TokenType.RIGHT_PAREN, ")"),
            Token(TokenType.SEMICOLON, ";"),
            Token(TokenType.EOF, "")
        )
        val parser = Parser(tokens, EmptyErrorReporter())
        val statements = parser.parse()
        print(statements)
        assertEquals(1, statements.size)
        val statement = statements[0]
        assertIs<Stmt.Expression>(statement)
        assertIs<Expr.Call>(statement.expression)
    }

    @Test
    fun assignmentExpressionParsing() {
        val tokens = listOf(
            Token(TokenType.IDENTIFIER, "foo"),
            Token(TokenType.EQUAL, "="),
            Token(TokenType.NUMBER, "2"),
            Token(TokenType.SEMICOLON, ";"),
            Token(TokenType.EOF, "")
        )
        val parser = Parser(tokens, EmptyErrorReporter())
        val statements = parser.parse()
        print(statements)
        assertEquals(1, statements.size)
        val statement = statements[0]
        assertIs<Stmt.Expression>(statement)
        assertIs<Expr.Assign>(statement.expression)
    }

    @Test
    fun variableExpressionParsing() {
        val tokens = listOf(
            Token(TokenType.IDENTIFIER, "foo"),
            Token(TokenType.SEMICOLON, ";"),
            Token(TokenType.EOF, "")
        )
        val parser = Parser(tokens, EmptyErrorReporter())
        val statements = parser.parse()
        print(statements)
        assertEquals(1, statements.size)
        val statement = statements[0]
        assertIs<Stmt.Expression>(statement)
        assertIs<Expr.Variable>(statement.expression)
    }

    @Test
    fun setExpressionParsing() {
        val tokens = listOf(
            Token(TokenType.IDENTIFIER, "foo"),
            Token(TokenType.DOT, "."),
            Token(TokenType.IDENTIFIER, "method"),
            Token(TokenType.EQUAL, "="),
            Token(TokenType.NUMBER, "0", 0),
            Token(TokenType.SEMICOLON, ";"),
            Token(TokenType.EOF, "")
        )
        val parser = Parser(tokens, EmptyErrorReporter())
        val statements = parser.parse()
        print(statements)
        assertEquals(1, statements.size)
        val statement = statements[0]
        assertIs<Stmt.Expression>(statement)
        assertIs<Expr.Set>(statement.expression)
    }

    @Test
    fun getExpressionParsing() {
        val tokens = listOf(
            Token(TokenType.IDENTIFIER, "foo"),
            Token(TokenType.DOT, "."),
            Token(TokenType.IDENTIFIER, "method"),
            Token(TokenType.SEMICOLON, ";"),
            Token(TokenType.EOF, "")
        )
        val parser = Parser(tokens, EmptyErrorReporter())
        val statements = parser.parse()
        print(statements)
        assertEquals(1, statements.size)
        val statement = statements[0]
        assertIs<Stmt.Expression>(statement)
        assertIs<Expr.Get>(statement.expression)
    }
}
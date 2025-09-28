package utils

import ast.Expr
import core.enum.TokenType
import core.scanner.Token
import core.types.classes.YSet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ReversePolishConverterTest {
    private val converter = ReversePolishConverter()

    private fun token(type: TokenType, lexeme: String, literal: Any? = null) = Token(type, lexeme, literal, 1)

    @Test
    fun `visitLiteralExpr should return number as string`() {
        val expr = Expr.Literal(42)
        val result = expr.accept(converter)
        assertEquals("42", result)
    }

    @Test
    fun `visitLiteralExpr should return null for null value`() {
        val expr = Expr.Literal(null)
        val result = expr.accept(converter)
        assertEquals("null", result)
    }

    @Test
    fun `visitStringLiteralExpr should return raw string`() {
        val expr = Expr.StringLiteral("hello")
        val result = expr.accept(converter)
        assertEquals("hello", result)
    }

    @Test
    fun `visitLiteralExpr should return string representation of a custom yaupl object`() {
        val expr = Expr.Literal(YSet())
        val result = expr.accept(converter)
        assertEquals("Set {}", result)
    }

    @Test
    fun `visitBinaryExpr should format in RPN`() {
        val expr = Expr.Binary(
            Expr.Literal(2),
            token(TokenType.PLUS, "+"),
            Expr.Literal(3)
        )
        val result = expr.accept(converter)
        assertEquals("2 3 +", result)
    }

    @Test
    fun `visitUnaryExpr should use NEGATE for minus`() {
        val expr = Expr.Unary(
            token(TokenType.MINUS, "-"),
            Expr.Literal(5)
        )
        val result = expr.accept(converter)
        assertEquals("5 NEGATE", result)
    }

    @Test
    fun `visitUnaryExpr should use NOT for bang`() {
        val expr = Expr.Unary(
            token(TokenType.BANG, "!"),
            Expr.Literal(true)
        )
        val result = expr.accept(converter)
        assertEquals("true NOT", result)
    }

    @Test
    fun `visitGroupingExpr should unwrap`() {
        val expr = Expr.Grouping(
            Expr.Literal(10)
        )
        val result = expr.accept(converter)
        assertEquals("10", result)
    }

    @Test
    fun `visitAssignExpr should format correctly`() {
        val expr = Expr.Assign(
            token(TokenType.IDENTIFIER, "x"),
            Expr.Literal(99)
        )
        val result = expr.accept(converter)
        assertEquals("assign(x, 99)", result)
    }

    @Test
    fun `visitCallExpr should format function calls`() {
        val expr = Expr.Call(
            Expr.Variable(token(TokenType.IDENTIFIER, "foo")),
            token(TokenType.RIGHT_PAREN, ")"),
            listOf(Expr.Literal(1), Expr.Literal(2))
        )
        val result = expr.accept(converter)
        assertEquals("function call var(foo) (1,2)", result.replace("x", "foo"))
    }

    @Test
    fun `visitArrayLiteralExpr should format arrays`() {
        val expr = Expr.ArrayLiteral(
            listOf(Expr.Literal(1), Expr.Literal(2), null, Expr.Literal(4))
        )
        val result = expr.accept(converter)
        assertEquals("Array [1, 2, null, 4]", result)
    }

    @Test
    fun `visitGetExpr should format property access`() {
        val expr = Expr.Get(
            Expr.Variable(token(TokenType.IDENTIFIER, "obj")),
            token(TokenType.IDENTIFIER, "prop")
        )
        val result = expr.accept(converter)
        assertEquals("(var(obj).prop)", result.replace("x", "obj"))
    }

    @Test
    fun `visitSetExpr should format set property`() {
        val expr = Expr.Set(
            Expr.Variable(token(TokenType.IDENTIFIER, "obj")),
            token(TokenType.IDENTIFIER, "prop"),
            Expr.Literal(123)
        )
        val result = expr.accept(converter)
        assertEquals("set (var(obj).prop) 123", result.replace("x", "obj"))
    }

    @Test
    fun `visitSuperExpr should return super`() {
        val expr = Expr.Super(
            token(TokenType.SUPER, "super"),
            token(TokenType.IDENTIFIER, "method")
        )
        val result = expr.accept(converter)
        assertEquals("super", result)
    }

    @Test
    fun `visitThisExpr should return this`() {
        val expr = Expr.This(
            token(TokenType.THIS, "this")
        )
        val result = expr.accept(converter)
        assertEquals("this", result)
    }
}

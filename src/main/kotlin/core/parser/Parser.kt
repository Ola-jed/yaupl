package core.parser

import ast.Expr
import core.enum.TokenType
import core.scanner.Token
import error.ErrorReporter
import error.ParseError


class Parser(private val tokens: List<Token>, private val errorReporter: ErrorReporter, private var current: Int = 0) {
    fun parse(): Expr? {
        return try {
            expression()
        } catch (error: ParseError) {
            null
        }
    }

    private fun expression(): Expr {
        return equality()
    }

    private fun equality(): Expr {
        var expr = comparison()
        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            val operator = previous()
            val right = comparison()
            expr = Expr.Binary(expr, operator, right)
        }

        return expr
    }

    private fun comparison(): Expr {
        var expr = term()
        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            val operator = previous()
            val right = term()
            expr = Expr.Binary(expr, operator, right)
        }

        return expr
    }

    private fun term(): Expr {
        var expr = unary()
        while (match(TokenType.SLASH, TokenType.STAR)) {
            val operator = previous()
            val right = unary()
            expr = Expr.Binary(expr, operator, right)
        }

        return expr
    }

    private fun unary(): Expr {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            val operator = previous()
            val right = unary()
            return Expr.Unary(operator, right)
        }

        return primary()
    }

    private fun primary(): Expr {
        if (match(TokenType.FALSE)) {
            return Expr.Literal(false)
        }

        if (match(TokenType.TRUE)) {
            return Expr.Literal(true)
        }

        if (match(TokenType.NULL)) {
            return Expr.Literal(null)
        }

        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return Expr.Literal(previous().literal)
        }

        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression()
            consume(TokenType.RIGHT_PAREN, "Expected ')' after expression")
            return Expr.Grouping(expr)
        }

        throw error(peek(), "Unexpected token")
    }

    private fun consume(tokenType: TokenType, message: String): Token {
        if (checkType(tokenType)) {
            return advance()
        }

        throw error(peek(), message)
    }

    private fun synchronize() {
        advance()

        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) {
                return
            }

            if (peek().type == TokenType.CLASS
                || peek().type == TokenType.FUN
                || peek().type == TokenType.LET
                || peek().type == TokenType.FOR
                || peek().type == TokenType.IF
                || peek().type == TokenType.WHILE
                || peek().type == TokenType.PRINT
                || peek().type == TokenType.RETURN
            ) {
                return
            }

            advance()
        }
    }

    private fun match(vararg tokenTypes: TokenType): Boolean {
        for (tokenType in tokenTypes) {
            if (checkType(tokenType)) {
                advance()
                return true
            }
        }

        return false
    }

    private fun checkType(type: TokenType): Boolean {
        if (isAtEnd()) {
            return false
        }
        return peek().type == type
    }

    private fun advance(): Token {
        if (!isAtEnd()) {
            current++
        }
        return previous()
    }

    private fun isAtEnd(): Boolean {
        return peek().type === TokenType.EOF
    }

    private fun peek(): Token {
        return tokens[current]
    }

    private fun previous(): Token {
        return tokens[current - 1]
    }

    private fun error(token: Token, message: String): ParseError {
        errorReporter.reportTokenError(token, message)
        return ParseError()
    }
}
package core.scanner

import core.`object`.Keywords
import core.enum.TokenType
import error.reporter.ErrorReporter


class Scanner(
    val source: String,
    val errorReporter: ErrorReporter,
    private val tokens: MutableList<Token> = mutableListOf(),
    private var start: Int = 0,
    private var current: Int = 0,
    private var line: Int = 0,
) {
    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }

        tokens.add(Token(TokenType.EOF, "", null, line))
        return tokens
    }

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    private fun scanToken() {
        when (val char = advance()) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.LEFT_PAREN)
            '.' -> addToken(TokenType.DOT)
            '+' -> addToken(TokenType.PLUS)
            '-' -> addToken(TokenType.MINUS)
            '%' -> addToken(TokenType.MODULO)
            ';' -> addToken(TokenType.SEMICOLON)
            '!' -> addToken(if (match('=')) TokenType.BANG_EQUAL else TokenType.BANG)
            '=' -> addToken(if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
            '>' -> addToken(if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)
            '<' -> addToken(if (match('=')) TokenType.LESS_EQUAL else TokenType.LESS)
            '*' -> {
                if (isInMultilineComment && match('/')) {
                    isInMultilineComment = false
                } else {
                    addToken(TokenType.STAR)
                }
            }

            '/' -> {
                if (match('*')) {
                    // Multiline comments : /**/
                    isInMultilineComment = true
                    while ((peek() != '*' || peekNext() != '/') && !isAtEnd()) {
                        advance()
                    }

                    if (!isAtEnd()) {
                        advance()
                        advance()
                        isInMultilineComment = false
                    }
                } else if (match('/')) {
                    // Single line comments : //
                    while (peek() != '\n' && !isAtEnd()) advance()
                } else {
                    addToken(TokenType.SLASH)
                }
            }

            '"' -> string()
            ' ', '\r', '\t', '\n' -> {}
            'o' -> {
                if (match('r')) {
                    addToken(TokenType.OR)
                }
            }

            else -> {
                if (isDigit(char)) {
                    number()
                } else if (isAlpha(char)) {
                    identifier()
                } else if (!isInMultilineComment) {
                    errorReporter.report(line, "Unexpected character : $char")
                }
            }
        }
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) {
            return false
        }

        if (source[current] != expected) {
            return false
        }

        current++
        return true
    }

    private fun isDigit(char: Char): Boolean {
        return char in '0'..'9'
    }

    private fun isAlpha(char: Char): Boolean {
        return (char in 'a'..'z') || (char in 'A'..'Z') || char == '_'
    }

    private fun isAlphaNumeric(char: Char): Boolean {
        return isAlpha(char) || isDigit(char)
    }

    private fun advance(): Char {
        current++
        return source[current - 1]
    }

    private fun peek(): Char {
        return if (isAtEnd()) 0.toChar() else source[current]
    }

    private fun peekNext(): Char {
        return peekNextNth(1)
    }

    private fun peekNextNth(nth: Int): Char {
        if (current + nth >= source.length) return 0.toChar()
        return source[current + nth]
    }

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++
            advance()
        }

        if (isAtEnd()) {
            errorReporter.report(line, "Unterminated string")
        }

        advance()

        val strValue = source.substring(start + 1, current - 1)
        addToken(TokenType.STRING, strValue)
    }

    private fun number() {
        while (isDigit(peek())) advance()

        if (peek() == '.' && isDigit(peekNext())) {
            advance()
            while (isDigit(peek())) advance()
        }

        addToken(TokenType.NUMBER, source.substring(start, current).toDouble())
    }

    private fun identifier() {
        while (isAlphaNumeric(peek())) advance()
        val text = source.substring(start, current)
        val tokenType = Keywords.all[text] ?: TokenType.IDENTIFIER
        addToken(tokenType)
    }

    private fun addToken(type: TokenType, literal: Any? = null) {
        if (!isInMultilineComment) {
            val text = source.substring(start, current)
            tokens.add(Token(type, text, literal, line))
        }

        // Just ignore everything
    }

    companion object {
        private var isInMultilineComment: Boolean = false
    }
}
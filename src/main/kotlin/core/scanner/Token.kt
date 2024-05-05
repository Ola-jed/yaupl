package core.scanner

import core.enum.TokenType

data class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any?,
    val line: Int
)
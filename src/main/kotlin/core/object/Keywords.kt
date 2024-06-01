package core.`object`

import core.enum.TokenType

object Keywords {
    val all = mapOf(
        "and" to TokenType.AND,
        "or" to TokenType.OR,
        "nand" to TokenType.NAND,
        "nor" to TokenType.NOR,
        "xor" to TokenType.XOR,
        "class" to TokenType.CLASS,
        "else" to TokenType.ELSE,
        "false" to TokenType.FALSE,
        "for" to TokenType.FOR,
        "fun" to TokenType.FUN,
        "if" to TokenType.IF,
        "let" to TokenType.LET,
        "null" to TokenType.NULL,
        "print" to TokenType.PRINT,
        "return" to TokenType.RETURN,
        "super" to TokenType.SUPER,
        "this" to TokenType.THIS,
        "true" to TokenType.TRUE,
        "while" to TokenType.WHILE,
        "break" to TokenType.BREAK,
        "continue" to TokenType.CONTINUE
    )
}
package core.parser

import core.scanner.Token

sealed class Expression {
    class BinaryExpression(val left: Expression, val operator: Token, val right: Expression) : Expression()
}
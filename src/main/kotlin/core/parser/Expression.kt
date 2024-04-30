package core.parser

import core.Token

sealed class Expression {
    class BinaryExpression(val left: Expression, val operator: Token, val right: Expression) : Expression()
}
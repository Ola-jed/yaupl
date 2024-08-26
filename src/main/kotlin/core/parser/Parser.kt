package core.parser

import ast.Expr
import ast.Stmt
import core.enum.TokenType
import core.`object`.Undefined
import core.scanner.Token
import core.error.reporter.ErrorReporter
import core.error.types.ParseError


class Parser(private val tokens: List<Token>, private val errorReporter: ErrorReporter, private var current: Int = 0) {
    fun parse(): List<Stmt> {
        val statements = mutableListOf<Stmt>()
        while (!isAtEnd()) {
            declaration()?.let { statements.add(it) }
        }

        return statements
    }

    private fun declaration(): Stmt? {
        try {
            if (match(TokenType.CLASS)) {
                return classDeclaration()
            }

            if (match(TokenType.FUN)) {
                return function("function")
            }

            if (match(TokenType.LET)) {
                return variableDeclaration()
            }

            return statement()
        } catch (error: ParseError) {
            synchronize()
            return null
        }
    }

    private fun classDeclaration(): Stmt {
        val name = consume(TokenType.IDENTIFIER, "Expect class name.")
        var superclass: Expr.Variable? = null

        if (match(TokenType.COLON)) {
            consume(TokenType.IDENTIFIER, "Expect superclass name.")
            superclass = Expr.Variable(previous())
        }

        consume(TokenType.LEFT_BRACE, "Expect '{' before class body.")
        val methods = mutableListOf<Stmt.Function>()
        while (!checkType(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            methods.add(function("method"))
        }

        consume(TokenType.RIGHT_BRACE, "'Expect ']' after class body.")
        return Stmt.Class(name, superclass, methods)
    }

    private fun function(kind: String): Stmt.Function {
        val name = consume(TokenType.IDENTIFIER, "Expect $kind name.")
        consume(TokenType.LEFT_PAREN, "Expect '(' after $kind name.")
        val parameters = mutableListOf<Token>()
        if (!checkType(TokenType.RIGHT_PAREN)) {
            do {
                if (parameters.size >= 255) {
                    error(peek(), "Can't have more than 255 parameters.")
                }

                parameters.add(consume(TokenType.IDENTIFIER, "Expect parameter name."))
            } while (match(TokenType.COMMA))
        }

        consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.")
        consume(TokenType.LEFT_BRACE, "Expect '{' before $kind body.")
        val body = statementsBlock() as Stmt.Block
        return Stmt.Function(name, parameters, body.statements)
    }

    private fun variableDeclaration(): Stmt {
        val name = consume(TokenType.IDENTIFIER, "Expect variable name.")

        var initializer: Expr = Expr.Literal(Undefined)
        if (match(TokenType.EQUAL)) {
            initializer = expression()
        }

        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.")
        return Stmt.VariableDeclaration(name, initializer)
    }

    private fun statement(): Stmt {
        return when {
            peek().type == TokenType.BREAK -> breakStatement()
            peek().type == TokenType.CONTINUE -> continueStatement()
            match(TokenType.FOR) -> forStatement()
            match(TokenType.IF) -> ifStatement()
            match(TokenType.WHILE) -> whileStatement()
            match(TokenType.DO) -> doWhileStatement()
            match(TokenType.PRINT) -> printStatement()
            match(TokenType.RETURN) -> returnStatement()
            match(TokenType.LEFT_BRACE) -> statementsBlock()
            else -> expressionStatement()
        }
    }

    private fun forStatement(): Stmt {
        consume(TokenType.LEFT_PAREN, "Expect '(' after for.")

        val initializer: Stmt? = if (match(TokenType.SEMICOLON)) {
            null
        } else if (match(TokenType.LET)) {
            variableDeclaration()
        } else {
            expressionStatement()
        }

        val condition: Expr = if (!checkType(TokenType.SEMICOLON)) expression() else Expr.Literal(true)
        consume(TokenType.SEMICOLON, "Expect ';' after loop's condition.")
        val increment: Expr? = if (!checkType(TokenType.RIGHT_PAREN)) expression() else null
        consume(TokenType.RIGHT_PAREN, "Expect ')' after for clauses.")
        var body = statement()
        if (increment != null) {
            body = Stmt.Block(listOf(body, Stmt.Expression(increment)))
        }

        body = Stmt.While(condition, body)

        if (initializer != null) {
            body = Stmt.Block(listOf(initializer, body))
        }

        return body
    }

    private fun ifStatement(): Stmt {
        consume(TokenType.LEFT_PAREN, "Expect '(' after if.")
        val condition = expression()
        consume(TokenType.RIGHT_PAREN, "Expect ')' after if's condition.")

        val thenBranch = statement()
        var elseBranch: Stmt? = null

        if (match(TokenType.ELSE)) {
            elseBranch = statement()
        }

        return Stmt.If(condition, thenBranch, elseBranch)
    }

    private fun whileStatement(): Stmt {
        consume(TokenType.LEFT_PAREN, "Expect '(' after while.")
        val condition = expression()
        consume(TokenType.RIGHT_PAREN, "Expect ')' after while's condition.")
        val body = statement()
        return Stmt.While(condition, body)
    }

    private fun doWhileStatement(): Stmt {
        val body = statement()
        consume(TokenType.WHILE, "Expect 'while' after do while's statements block.")
        consume(TokenType.LEFT_PAREN, "Expect '(' after while.")
        val condition = expression()
        consume(TokenType.RIGHT_PAREN, "Expect ')' after while's condition.")
        consume(TokenType.SEMICOLON, "Expect ';' after do while.")
        return Stmt.DoWhile(condition, body)
    }

    private fun breakStatement(): Stmt {
        val breakStatement = Stmt.Break(consume(TokenType.BREAK, "Expect 'break';"))
        advance()
        return breakStatement
    }

    private fun continueStatement(): Stmt {
        val breakStatement = Stmt.Continue(consume(TokenType.CONTINUE, "Expect 'continue';"))
        advance()
        return breakStatement
    }

    private fun printStatement(): Stmt {
        val value = expression()
        consume(TokenType.SEMICOLON, "Expect ; after the value.")
        return Stmt.Print(value)
    }

    private fun returnStatement(): Stmt {
        val keyword = previous()
        var value: Expr? = null
        if (!checkType(TokenType.SEMICOLON)) {
            value = expression()
        }

        consume(TokenType.SEMICOLON, "Expect ';' after return value.")
        return Stmt.Return(keyword, value)
    }

    private fun statementsBlock(): Stmt {
        val statements = mutableListOf<Stmt>()

        while (!checkType(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            declaration()?.let { statements.add(it) }
        }

        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.")
        return Stmt.Block(statements)
    }

    private fun expressionStatement(): Stmt {
        val value = expression()
        consume(TokenType.SEMICOLON, "Expect ; after the value.")
        return Stmt.Expression(value)
    }

    private fun expression(): Expr {
        return assignment()
    }

    private fun assignment(): Expr {
        val expression = or()

        if (match(TokenType.EQUAL)) {
            val equals = previous()
            val value = assignment()

            if (expression is Expr.Variable) {
                val name = expression.name
                return Expr.Assign(name, value)
            } else if (expression is Expr.Get) {
                return Expr.Set(expression.obj, expression.name, value)
            }

            error(equals, "Invalid assignment target.")
        }

        return expression
    }

    private fun or(): Expr {
        var expr = and()

        while (match(TokenType.OR)) {
            val operator = previous()
            val right = and()
            expr = Expr.Logical(expr, operator, right)
        }

        return expr
    }

    private fun and(): Expr {
        var expr = xor()

        while (match(TokenType.AND)) {
            val operator = previous()
            val right = equality()
            expr = Expr.Logical(expr, operator, right)
        }

        return expr
    }

    private fun xor(): Expr {
        var expr = nor()

        while (match(TokenType.XOR)) {
            val operator = previous()
            val right = equality()
            expr = Expr.Logical(expr, operator, right)
        }

        return expr
    }

    private fun nor(): Expr {
        var expr = nand()

        while (match(TokenType.NOR)) {
            val operator = previous()
            val right = equality()
            expr = Expr.Logical(expr, operator, right)
        }

        return expr
    }

    private fun nand(): Expr {
        var expr = equality()

        while (match(TokenType.NAND)) {
            val operator = previous()
            val right = equality()
            expr = Expr.Logical(expr, operator, right)
        }

        return expr
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
        var expr = bitwise()
        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            val operator = previous()
            val right = bitwise()
            expr = Expr.Binary(expr, operator, right)
        }

        return expr
    }

    private fun bitwise(): Expr {
        var expr = term()
        while (match(TokenType.LSHIFT, TokenType.RSHIFT)) {
            val operator = previous()
            val right = term()
            expr = Expr.Binary(expr, operator, right)
        }

        return expr
    }

    private fun term(): Expr {
        var expr = factor()
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            val operator = previous()
            val right = factor()
            expr = Expr.Binary(expr, operator, right)
        }

        return expr
    }

    private fun factor(): Expr {
        var expr = exponent()
        while (match(TokenType.SLASH, TokenType.STAR, TokenType.MODULO)) {
            val operator = previous()
            val right = exponent()
            expr = Expr.Binary(expr, operator, right)
        }

        return expr
    }

    private fun exponent(): Expr {
        var expr = unary()
        while (match(TokenType.EXPONENT)) {
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

        return call()
    }

    private fun call(): Expr {
        var expr = primary()

        while (true) {
            if (match(TokenType.LEFT_PAREN)) {
                expr = finishCall(expr)
            } else if (match(TokenType.DOT)) {
                val name = consume(TokenType.IDENTIFIER, "Expect property name after '.'.")
                expr = Expr.Get(expr, name)
            } else {
                break
            }
        }

        return expr
    }

    private fun finishCall(callee: Expr): Expr {
        val arguments = mutableListOf<Expr>()

        if (!checkType(TokenType.RIGHT_PAREN)) {
            do {
                if (arguments.size >= 255) {
                    error(peek(), "Can't have more than 255 arguments.")
                }

                arguments.add(expression())
            } while (match(TokenType.COMMA))
        }

        val paren = consume(TokenType.RIGHT_PAREN, "Expect ')' after function arguments.")
        return Expr.Call(callee, paren, arguments)
    }

    private fun primary(): Expr {
        when {
            match(TokenType.FALSE) -> return Expr.Literal(false)
            match(TokenType.TRUE) -> return Expr.Literal(true)
            match(TokenType.NULL) -> return Expr.Literal(null)
            match(TokenType.NUMBER, TokenType.STRING) -> return Expr.Literal(previous().literal)
            match(TokenType.THIS) -> return Expr.This(previous())
            match(TokenType.IDENTIFIER) -> return Expr.Variable(previous())
            match(TokenType.SUPER) -> {
                val keyword = previous()
                consume(TokenType.DOT, "Expect '.' after 'super'.")
                val method = consume(TokenType.IDENTIFIER, "Expect superclass method name")
                return Expr.Super(keyword, method)
            }

            match(TokenType.LEFT_PAREN) -> {
                val expr = expression()
                consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.")
                return Expr.Grouping(expr)
            }

            match(TokenType.LEFT_SQUARE_BRACKET) -> {
                val elements = mutableListOf<Expr>()
                if (peek().type != TokenType.RIGHT_SQUARE_BRACKET) {
                    do {
                        if (peek().type == TokenType.RIGHT_SQUARE_BRACKET) {
                            break
                        }

                        elements.add(expression())
                    } while (match(TokenType.COMMA))
                }

                consume(TokenType.RIGHT_SQUARE_BRACKET, "Expect ']' after array literal.")
                return Expr.ArrayLiteral(elements)
            }

            else -> throw error(peek(), "Unexpected token.")
        }
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
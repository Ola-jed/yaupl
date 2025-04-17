#include "../include/compiler.h"

#include <iomanip>
#include <iostream>

#include "../include/opcode.h"
#include "../include/precedence.h"
#include "../include/scanner.h"
#include "../include/token.h"

bool Compiler::compile(const std::string &source, Chunk *chunk)
{
    scanner = Scanner{source};
    compilingChunk = chunk;
    parser.panicMode = false;
    parser.hadError = false;
    advance();
    while (!match(TokenType::FILE_EOF))
    {
        declaration();
    }

    endCompiler();
    return !parser.hadError;
}

void Compiler::advance()
{
    parser.previous = parser.current;
    for (;;)
    {
        parser.current = scanner.scanToken();
        if (parser.current.type != TokenType::ERROR)
        {
            break;
        }

        errorAtCurrent(std::string{parser.current.lexeme});
    }
}

void Compiler::declaration()
{
    if (match(TokenType::LET))
    {
        variableDeclaration();
    }
    else if (match(TokenType::CONST))
    {
        constantDeclaration();
    }
    else
    {
        statement();
    }

    if (parser.panicMode)
    {
        synchronize();
    }
}

void Compiler::statement()
{
    if (match(TokenType::PRINT))
    {
        printStatement();
    }
    else
    {
        expressionStatement();
    }
}

void Compiler::variableDeclaration()
{
    auto const variableName = parseVariable("Expect variable name.");
    if (match(TokenType::EQUAL))
    {
        expression();
    }
    else
    {
        emitByte(static_cast<uint8_t>(OpCode::OP_NULL));
    }

    consume(TokenType::SEMICOLON, "Expect ';' after variable declaration");
    defineVariable(variableName);
}

void Compiler::constantDeclaration()
{
    auto const variableName = parseVariable("Expect variable name.");
    consume(TokenType::EQUAL, "Expected '=' after constant");
    expression();
    consume(TokenType::SEMICOLON, "Expect ';' after constant declaration");
    defineConstant(variableName);
}

void Compiler::printStatement()
{
    expression();
    consume(TokenType::SEMICOLON, "Expect ';' after value.");
    emitByte(static_cast<uint8_t>(OpCode::OP_PRINT));
}

void Compiler::expressionStatement()
{
    expression();
    consume(TokenType::SEMICOLON, "Expect ';' after expression.");
    emitByte(static_cast<uint8_t>(OpCode::OP_POP));
}

void Compiler::expression()
{
    parsePrecedence(Precedence::Assignment);
}

void Compiler::number()
{
    const auto value = std::strtod({parser.previous.lexeme.data()}, nullptr);
    emitConstant(value);
}

void Compiler::grouping()
{
    expression();
    consume(TokenType::RIGHT_PAREN, "Expected ')' after expression.");
}

void Compiler::unary()
{
    const auto operatorType = parser.previous.type;
    expression();
    parsePrecedence(Precedence::Unary);
    switch (operatorType)
    {
        case TokenType::BANG:
            emitByte(static_cast<uint8_t>(OpCode::OP_NOT));
            break;
        case TokenType::MINUS:
            emitByte(static_cast<uint8_t>(OpCode::OP_NEGATE));
            break;
        default:
            break;
    }
}

void Compiler::binary()
{
    const auto operatorType = parser.previous.type;
    const auto rule = getRule(operatorType);
    parsePrecedence(static_cast<Precedence>(static_cast<int>(rule.precedence) + 1));

    switch (operatorType)
    {
        case TokenType::PLUS:
            emitByte(static_cast<uint8_t>(OpCode::OP_ADD));
            break;
        case TokenType::MINUS:
            emitByte(static_cast<uint8_t>(OpCode::OP_SUBTRACT));
            break;
        case TokenType::STAR:
            emitByte(static_cast<uint8_t>(OpCode::OP_MULTIPLY));
            break;
        case TokenType::SLASH:
            emitByte(static_cast<uint8_t>(OpCode::OP_DIVIDE));
            break;
        case TokenType::MODULO:
            emitByte(static_cast<uint8_t>(OpCode::OP_MODULO));
            break;
        case TokenType::EXPONENT:
            emitByte(static_cast<uint8_t>(OpCode::OP_EXPONENT));
            break;
        case TokenType::BANG_EQUAL:
            emitByte(static_cast<uint8_t>(OpCode::OP_EQUAL), static_cast<uint8_t>(OpCode::OP_NOT));
            break;
        case TokenType::EQUAL_EQUAL:
            emitByte(static_cast<uint8_t>(OpCode::OP_EQUAL));
            break;
        case TokenType::GREATER:
            emitByte(static_cast<uint8_t>(OpCode::OP_GREATER));
            break;
        case TokenType::GREATER_EQUAL:
            emitByte(static_cast<uint8_t>(OpCode::OP_LESS), static_cast<uint8_t>(OpCode::OP_NOT));
            break;
        case TokenType::LESS:
            emitByte(static_cast<uint8_t>(OpCode::OP_LESS));
            break;
        case TokenType::LESS_EQUAL:
            emitByte(static_cast<uint8_t>(OpCode::OP_GREATER), static_cast<uint8_t>(OpCode::OP_NOT));
            break;
        default: break;
    }
}

void Compiler::literal()
{
    switch (parser.previous.type)
    {
        case TokenType::FALSE:
            emitByte(static_cast<uint8_t>(OpCode::OP_FALSE));
            break;
        case TokenType::TRUE:
            emitByte(static_cast<uint8_t>(OpCode::OP_TRUE));
            break;
        case TokenType::NIL:
            emitByte(static_cast<uint8_t>(OpCode::OP_NULL));
            break;
        default: break;
    }
}

void Compiler::string()
{
    const auto contentString = std::string(parser.previous.lexeme.data() + 1, parser.previous.lexeme.length() - 2);
    emitConstant(contentString);
}

void Compiler::variable()
{
    namedVariable(parser.previous);
}

void Compiler::namedVariable(const Token &name)
{
    const auto argument = identifierConstant(name);
    emitByte(static_cast<uint8_t>(OpCode::OP_GET_GLOBAL), argument);
}

void Compiler::consume(TokenType type, const std::string &message)
{
    if (parser.current.type == type)
    {
        advance();
        return;
    }

    errorAtCurrent(message);
}

bool Compiler::match(const TokenType type)
{
    if (!check(type))
    {
        return false;
    }

    advance();
    return true;
}

bool Compiler::check(const TokenType type) const
{
    return parser.current.type == type;
}

void Compiler::errorAtCurrent(const std::string &message)
{
    errorAt(parser.current, message);
}

void Compiler::error(const std::string &message)
{
    errorAt(parser.current, message);
}

void Compiler::errorAt(const Token &token, const std::string &message)
{
    parser.panicMode = true;
    std::cerr << "[line " << token.line << "] Error";
    if (token.type == TokenType::FILE_EOF)
    {
        std::cerr << " at end ";
    }
    else if (token.type != TokenType::ERROR)
    {
        std::cerr << " at token " << token.type << " ";
    }

    std::cerr << message << "\n";
    parser.hadError = true;
}

void Compiler::emitByte(const uint8_t byte) const
{
    compilingChunk->write(byte, parser.previous.line);
}

void Compiler::emitByte(const uint8_t byte1, const uint8_t byte2) const
{
    emitByte(byte1);
    emitByte(byte2);
}

void Compiler::emitConstant(const Value &value)
{
    emitByte(static_cast<uint8_t>(OpCode::OP_CONSTANT), makeConstant(value));
}

uint8_t Compiler::makeConstant(const Value &value)
{
    auto const constant = compilingChunk->addConstant(value);
    if (constant > UINT8_MAX)
    {
        error("Too many constants in one chunk.");
        return 0;
    }

    return constant;
}

void Compiler::endCompiler() const
{
    emitReturn();

#ifdef DEBUG_PRINT_CODE
    if (!parser.hadError)
    {
        compilingChunk->disassemble("code");
    }
#endif
}

void Compiler::synchronize()
{
    parser.panicMode = false;
    while (parser.current.type != TokenType::FILE_EOF)
    {
        if (parser.previous.type == TokenType::SEMICOLON)
        {
            return;
        }

        switch (parser.previous.type)
        {
            case TokenType::CLASS:
            case TokenType::FUN:
            case TokenType::LET:
            case TokenType::CONST:
            case TokenType::FOR:
            case TokenType::IF:
            case TokenType::WHILE:
            case TokenType::PRINT:
            case TokenType::RETURN:
                return;

            default: break;
        }

        advance();
    }
}

void Compiler::emitReturn() const
{
    emitByte(static_cast<uint8_t>(OpCode::OP_RETURN));
}

void Compiler::parsePrecedence(Precedence precedence)
{
    advance();
    const auto prefixRule = getRule(parser.previous.type).prefix;
    if (prefixRule == nullptr)
    {
        error("Expected expression.");
        return;
    }

    (this->*prefixRule)();
    while (precedence <= getRule(parser.current.type).precedence)
    {
        advance();
        const auto infixRule = getRule(parser.previous.type).infix;
        (this->*infixRule)();
    }
}

void Compiler::defineVariable(const uint8_t global) const
{
    emitByte(static_cast<uint8_t>(OpCode::OP_DEFINE_GLOBAL), global);
}

void Compiler::defineConstant(const uint8_t global) const
{
    emitByte(static_cast<uint8_t>(OpCode::OP_DEFINE_CONSTANT), global);
}

uint8_t Compiler::parseVariable(const std::string &errorMessage)
{
    consume(TokenType::IDENTIFIER, errorMessage);
    return identifierConstant(parser.previous);
}

uint8_t Compiler::identifierConstant(const Token &token)
{
    const auto content = std::string{token.lexeme};
    return makeConstant(content);
}

ParseRule Compiler::getRule(const TokenType type) const
{
    return rules[static_cast<uint8_t>(type)];
}

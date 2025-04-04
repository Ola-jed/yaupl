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
    expression();
    consume(TokenType::FILE_EOF, "Expect end of expression.");
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

        errorAtCurrent(parser.current.lexeme.data());
    }
}

void Compiler::expression()
{
    parsePrecedence(Precedence::Assignment);
}

void Compiler::number()
{
    const auto value = std::strtod(parser.previous.lexeme.data(), nullptr);
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
    switch (operatorType)
    {
        case TokenType::MINUS:
            parsePrecedence(Precedence::Unary);
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
        default: break;
    }
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

void Compiler::emitConstant(Value value)
{
    emitByte(static_cast<uint8_t>(OpCode::OP_CONSTANT), makeConstant(value));
}

uint8_t Compiler::makeConstant(const Value value)
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

ParseRule Compiler::getRule(const TokenType type) const
{
    return rules[static_cast<uint8_t>(type)];
}

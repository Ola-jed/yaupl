#ifndef COMPILER_H
#define COMPILER_H
#include <array>
#include <string>

#include "chunk.h"
#include "parser.h"
#include "parse_rule.h"
#include "precedence.h"
#include "scanner.h"
#include "util.h"

class Compiler
{
    Parser parser;

    Chunk *compilingChunk = nullptr;

    Scanner scanner{""};

    void advance();

    void expression();

    void number();

    void grouping();

    void unary();

    void binary();

    void consume(TokenType, const std::string &);

    void errorAtCurrent(const std::string &);

    void error(const std::string &);

    void errorAt(const Token &, const std::string &);

    void emitByte(uint8_t byte) const;

    void emitByte(uint8_t byte1, uint8_t byte2) const;

    void emitConstant(Value value);

    [[ nodiscard]] uint8_t makeConstant(Value value);

    void endCompiler() const;

    void emitReturn() const;

    void parsePrecedence(Precedence);

    [[ nodiscard]] ParseRule getRule(TokenType) const;

    std::array<ParseRule, static_cast<std::underlying_type_t<TokenType>>(TokenType::COUNT)> rules = {
        ParseRule{grouping, nullptr, Precedence::None}, // Left paren
        ParseRule{nullptr, nullptr, Precedence::None}, // Right paren
        ParseRule{nullptr, nullptr, Precedence::None}, // Left brace
        ParseRule{nullptr, nullptr, Precedence::None}, // Right brace
        ParseRule{nullptr, nullptr, Precedence::None}, // Comma
        ParseRule{nullptr, nullptr, Precedence::None}, // Dot
        ParseRule{unary, binary, Precedence::Term}, // Minus
        ParseRule{nullptr, binary, Precedence::Term}, // Plus
        ParseRule{nullptr, nullptr, Precedence::None}, // Semicolon
        ParseRule{nullptr, binary, Precedence::Factor}, // Slash
        ParseRule{nullptr, binary, Precedence::Factor}, // Star
        ParseRule{nullptr, binary, Precedence::Exponent}, // Exponent
        ParseRule{nullptr, binary, Precedence::Factor}, // Modulo
        ParseRule{nullptr, nullptr, Precedence::None}, // Colon
        ParseRule{nullptr, nullptr, Precedence::None}, // Bang
        ParseRule{nullptr, nullptr, Precedence::None}, // Bang equal
        ParseRule{nullptr, nullptr, Precedence::None}, // Equal
        ParseRule{nullptr, nullptr, Precedence::None}, // Equal equal
        ParseRule{nullptr, nullptr, Precedence::None}, // Greater
        ParseRule{nullptr, nullptr, Precedence::None}, // Greater equal
        ParseRule{nullptr, nullptr, Precedence::None}, // Less
        ParseRule{nullptr, nullptr, Precedence::None}, // Less equal
        ParseRule{nullptr, nullptr, Precedence::None}, // Left shift
        ParseRule{nullptr, nullptr, Precedence::None}, // Right shift
        ParseRule{nullptr, nullptr, Precedence::None}, // Identifier
        ParseRule{nullptr, nullptr, Precedence::None}, // String
        ParseRule{number, nullptr, Precedence::None}, // Number
        ParseRule{nullptr, nullptr, Precedence::None}, // And
        ParseRule{nullptr, nullptr, Precedence::None}, // Class
        ParseRule{nullptr, nullptr, Precedence::None}, // Else
        ParseRule{nullptr, nullptr, Precedence::None}, // False
        ParseRule{nullptr, nullptr, Precedence::None}, // For
        ParseRule{nullptr, nullptr, Precedence::None}, // Fun
        ParseRule{nullptr, nullptr, Precedence::None}, // If
        ParseRule{nullptr, nullptr, Precedence::None}, // Nil
        ParseRule{nullptr, nullptr, Precedence::None}, // Or
        ParseRule{nullptr, nullptr, Precedence::None}, // Nand
        ParseRule{nullptr, nullptr, Precedence::None}, // Nor
        ParseRule{nullptr, nullptr, Precedence::None}, // Xor
        ParseRule{nullptr, nullptr, Precedence::None}, // Print
        ParseRule{nullptr, nullptr, Precedence::None}, // Return
        ParseRule{nullptr, nullptr, Precedence::None}, // Super
        ParseRule{nullptr, nullptr, Precedence::None}, // This
        ParseRule{nullptr, nullptr, Precedence::None}, // True
        ParseRule{nullptr, nullptr, Precedence::None}, // Let
        ParseRule{nullptr, nullptr, Precedence::None}, // While
        ParseRule{nullptr, nullptr, Precedence::None}, // Break
        ParseRule{nullptr, nullptr, Precedence::None}, // Continue
        ParseRule{nullptr, nullptr, Precedence::None}, // Do
        ParseRule{nullptr, nullptr, Precedence::None}, // Const
        ParseRule{nullptr, nullptr, Precedence::None}, // Import
        ParseRule{nullptr, nullptr, Precedence::None}, // Static
        ParseRule{nullptr, nullptr, Precedence::None}, // EOF
        ParseRule{nullptr, nullptr, Precedence::None}, // Error
    };

public:
    bool compile(const std::string &source, Chunk *chunk);
};

#endif //COMPILER_H

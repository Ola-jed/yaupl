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

    void declaration();

    void statement();

    void variableDeclaration();

    void constantDeclaration();

    void printStatement();

    void expressionStatement();

    void expression();

    void number(bool);

    void grouping(bool);

    void unary(bool);

    void binary(bool);

    void literal(bool);

    void string(bool);

    void variable(bool);

    void namedVariable(const Token &, bool);

    void consume(TokenType, const std::string &);

    [[nodiscard]] bool match(TokenType);

    [[nodiscard]] bool check(TokenType) const;

    void errorAtCurrent(const std::string &);

    void error(const std::string &);

    void errorAt(const Token &, const std::string &);

    void emitByte(uint8_t) const;

    void emitByte(uint8_t, uint8_t) const;

    void emitConstant(const Value&);

    [[ nodiscard]] uint8_t makeConstant(const Value &);

    void endCompiler() const;

    void synchronize();

    void emitReturn() const;

    void parsePrecedence(Precedence);

    void defineVariable(uint8_t) const;

    void defineConstant(uint8_t) const;

    uint8_t parseVariable(const std::string &);

    uint8_t identifierConstant(const Token &);

    [[ nodiscard]] ParseRule getRule(TokenType) const;

    std::array<ParseRule, static_cast<std::underlying_type_t<TokenType>>(TokenType::COUNT)> rules = {
        ParseRule{&Compiler::grouping, nullptr, Precedence::None}, // Left paren
        ParseRule{nullptr, nullptr, Precedence::None}, // Right paren
        ParseRule{nullptr, nullptr, Precedence::None}, // Left brace
        ParseRule{nullptr, nullptr, Precedence::None}, // Right brace
        ParseRule{nullptr, nullptr, Precedence::None}, // Comma
        ParseRule{nullptr, nullptr, Precedence::None}, // Dot
        ParseRule{&Compiler::unary, &Compiler::binary, Precedence::Term}, // Minus
        ParseRule{nullptr, &Compiler::binary, Precedence::Term}, // Plus
        ParseRule{nullptr, nullptr, Precedence::None}, // Semicolon
        ParseRule{nullptr, &Compiler::binary, Precedence::Factor}, // Slash
        ParseRule{nullptr, &Compiler::binary, Precedence::Factor}, // Star
        ParseRule{nullptr, &Compiler::binary, Precedence::Exponent}, // Exponent
        ParseRule{nullptr, &Compiler::binary, Precedence::Factor}, // Modulo
        ParseRule{nullptr, nullptr, Precedence::None}, // Colon
        ParseRule{&Compiler::unary, nullptr, Precedence::None}, // Bang
        ParseRule{nullptr, &Compiler::binary, Precedence::Equality}, // Bang equal
        ParseRule{nullptr, nullptr, Precedence::None}, // Equal
        ParseRule{nullptr, &Compiler::binary, Precedence::Equality}, // Equal equal
        ParseRule{nullptr, &Compiler::binary, Precedence::Comparison}, // Greater
        ParseRule{nullptr, &Compiler::binary, Precedence::Comparison}, // Greater equal
        ParseRule{nullptr, &Compiler::binary, Precedence::Comparison}, // Less
        ParseRule{nullptr, &Compiler::binary, Precedence::Comparison}, // Less equal
        ParseRule{nullptr, nullptr, Precedence::None}, // Left shift
        ParseRule{nullptr, nullptr, Precedence::None}, // Right shift
        ParseRule{&Compiler::variable, nullptr, Precedence::None}, // Identifier
        ParseRule{&Compiler::string, nullptr, Precedence::None}, // String
        ParseRule{&Compiler::number, nullptr, Precedence::None}, // Number
        ParseRule{nullptr, nullptr, Precedence::None}, // And
        ParseRule{nullptr, nullptr, Precedence::None}, // Class
        ParseRule{nullptr, nullptr, Precedence::None}, // Else
        ParseRule{&Compiler::literal, nullptr, Precedence::None}, // False
        ParseRule{nullptr, nullptr, Precedence::None}, // For
        ParseRule{nullptr, nullptr, Precedence::None}, // Fun
        ParseRule{nullptr, nullptr, Precedence::None}, // If
        ParseRule{&Compiler::literal, nullptr, Precedence::None}, // Nil
        ParseRule{nullptr, nullptr, Precedence::None}, // Or
        ParseRule{nullptr, nullptr, Precedence::None}, // Nand
        ParseRule{nullptr, nullptr, Precedence::None}, // Nor
        ParseRule{nullptr, nullptr, Precedence::None}, // Xor
        ParseRule{nullptr, nullptr, Precedence::None}, // Print
        ParseRule{nullptr, nullptr, Precedence::None}, // Return
        ParseRule{nullptr, nullptr, Precedence::None}, // Super
        ParseRule{nullptr, nullptr, Precedence::None}, // This
        ParseRule{&Compiler::literal, nullptr, Precedence::None}, // True
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

#ifndef TOKEN_TYPE_H
#define TOKEN_TYPE_H

enum class TokenType
{
    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR, EXPONENT,
    MODULO, COLON,

    // One or two character tokens.
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,
    LSHIFT, RSHIFT,

    // Literals.
    IDENTIFIER, STRING, NUMBER,

    // Keywords.
    // NULL became NIL because NULL is reserved in c++
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
    NAND, NOR, XOR, PRINT, RETURN, SUPER, THIS, TRUE,
    LET, WHILE, BREAK, CONTINUE, DO,

    // A special one
    // EOF became FILE_EOF because EOF is reserved
    FILE_EOF,

    // Used to signify that an error occurred
    ERROR
};

inline std::ostream &operator<<(std::ostream &os, const TokenType type)
{
    switch (type)
    {
        case TokenType::LEFT_PAREN: return os << "LEFT_PAREN";
        case TokenType::RIGHT_PAREN: return os << "RIGHT_PAREN";
        case TokenType::LEFT_BRACE: return os << "LEFT_BRACE";
        case TokenType::RIGHT_BRACE: return os << "RIGHT_BRACE";
        case TokenType::COMMA: return os << "COMMA";
        case TokenType::DOT: return os << "DOT";
        case TokenType::MINUS: return os << "MINUS";
        case TokenType::PLUS: return os << "PLUS";
        case TokenType::SEMICOLON: return os << "SEMICOLON";
        case TokenType::SLASH: return os << "SLASH";
        case TokenType::STAR: return os << "STAR";
        case TokenType::EXPONENT: return os << "EXPONENT";
        case TokenType::MODULO: return os << "MODULO";
        case TokenType::COLON: return os << "COLON";
        case TokenType::BANG: return os << "BANG";
        case TokenType::BANG_EQUAL: return os << "BANG_EQUAL";
        case TokenType::EQUAL: return os << "EQUAL";
        case TokenType::EQUAL_EQUAL: return os << "EQUAL_EQUAL";
        case TokenType::GREATER: return os << "GREATER";
        case TokenType::GREATER_EQUAL: return os << "GREATER_EQUAL";
        case TokenType::LESS: return os << "LESS";
        case TokenType::LESS_EQUAL: return os << "LESS_EQUAL";
        case TokenType::LSHIFT: return os << "LSHIFT";
        case TokenType::RSHIFT: return os << "RSHIFT";
        case TokenType::IDENTIFIER: return os << "IDENTIFIER";
        case TokenType::STRING: return os << "STRING";
        case TokenType::NUMBER: return os << "NUMBER";
        case TokenType::AND: return os << "AND";
        case TokenType::CLASS: return os << "CLASS";
        case TokenType::ELSE: return os << "ELSE";
        case TokenType::FALSE: return os << "FALSE";
        case TokenType::FUN: return os << "FUN";
        case TokenType::FOR: return os << "FOR";
        case TokenType::IF: return os << "IF";
        case TokenType::NIL: return os << "NULL";
        case TokenType::OR: return os << "OR";
        case TokenType::NAND: return os << "NAND";
        case TokenType::NOR: return os << "NOR";
        case TokenType::XOR: return os << "XOR";
        case TokenType::PRINT: return os << "PRINT";
        case TokenType::RETURN: return os << "RETURN";
        case TokenType::SUPER: return os << "SUPER";
        case TokenType::THIS: return os << "THIS";
        case TokenType::TRUE: return os << "TRUE";
        case TokenType::LET: return os << "LET";
        case TokenType::WHILE: return os << "WHILE";
        case TokenType::BREAK: return os << "BREAK";
        case TokenType::CONTINUE: return os << "CONTINUE";
        case TokenType::DO: return os << "DO";
        case TokenType::FILE_EOF: return os << "EOF";
        default: return os << "UNKNOWN";
    }
}


#endif //TOKEN_TYPE_H

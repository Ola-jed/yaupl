#ifndef TOKEN_H
#define TOKEN_H
#include <string>
#include <utility>

#include "token_type.h"

struct Token
{
    std::string lexeme;
    int line;
    TokenType type;

    Token(const TokenType type, std::string lexeme, const int line)
        : lexeme(std::move(lexeme)),
          line(line),
          type(type)
    {
    }

    Token(): line(0), type(TokenType::FILE_EOF)
    {
    }
};

inline bool operator==(const Token &lhs, const Token &rhs)
{
    return lhs.lexeme == rhs.lexeme;
}
#endif //TOKEN_H

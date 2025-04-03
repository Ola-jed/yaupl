#ifndef TOKEN_H
#define TOKEN_H
#include <string>

#include "token_type.h"

struct Token
{
    std::string_view lexeme;
    int line;
    TokenType type;

    Token(const TokenType type, const std::string_view &lexeme, const int line)
        : lexeme(lexeme),
          line(line),
          type(type)
    {
    }

    Token(): line(0), type(TokenType::FILE_EOF)
    {
    }
};
#endif //TOKEN_H

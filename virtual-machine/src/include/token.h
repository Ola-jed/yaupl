#ifndef TOKEN_H
#define TOKEN_H
#include <string>

#include "token_type.h"

struct Token
{
    TokenType type;
    std::string_view lexeme;
    int line;

    Token(const TokenType type, const std::string_view &lexeme, const int line)
        : type(type),
          lexeme(lexeme),
          line(line)
    {
    }
};
#endif //TOKEN_H

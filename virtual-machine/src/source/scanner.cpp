#include "../include/scanner.h"

Token Scanner::scanToken()
{
    start = current;
    if (isAtEnd())
    {
        return makeToken(TokenType::FILE_EOF);
    }

    return errorToken("Unexpected character");
}

bool Scanner::isAtEnd() const
{
    return current >= source.length();
}

Token Scanner::makeToken(TokenType type) const
{
    const auto lexeme = std::string_view{source.begin() + start, source.begin() + current};
    return Token{type, lexeme, line};
}

Token Scanner::errorToken(const std::string_view &message) const
{
    return Token {TokenType::ERROR, message, line};
}


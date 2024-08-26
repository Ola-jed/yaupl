#ifndef SCANNER_H
#define SCANNER_H
#include <string>

#include "token.h"

struct Scanner
{
    std::string source;
    int start;
    int current;
    int line;

    explicit Scanner(const std::string &source): source(source), start(0), current(0), line(1)
    {
    }

    Token scanToken();

private:
    [[nodiscard]] bool isAtEnd() const;

    [[nodiscard]] Token makeToken(TokenType) const;

    [[nodiscard]] Token errorToken(const std::string_view&) const;
};

#endif //SCANNER_H

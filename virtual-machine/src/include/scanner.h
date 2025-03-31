#ifndef SCANNER_H
#define SCANNER_H
#include <string>

#include "token.h"

class Scanner
{
public:
    explicit Scanner(const std::string &source): source(source), start(0), current(0), line(1)
    {
    }

    Token scanToken();

private:
    std::string source;
    int start;
    int current;
    int line;

    char advance();

    [[nodiscard]] bool isAtEnd() const;

    [[nodiscard]] static bool isLetterOrUnderscore(char) ;

    [[nodiscard]] Token makeToken(TokenType) const;

    [[nodiscard]] Token errorToken(const std::string_view &) const;

    [[nodiscard]] bool match(char);

    [[nodiscard]] char peek() const;

    [[nodiscard]] char peekNext() const;

    [[nodiscard]] Token string();

    [[nodiscard]] Token number();

    [[nodiscard]] Token identifier();

    [[nodiscard]] TokenType identifierType();

    [[nodiscard]] TokenType checkKeyword(int, const std::string&, TokenType) const;

    void skipWhitespacesAndComments();
};

#endif //SCANNER_H

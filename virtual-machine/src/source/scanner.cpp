#include "../include/scanner.h"

#include <cstring>

Token Scanner::scanToken()
{
    skipWhitespacesAndComments();
    start = current;
    if (isAtEnd())
    {
        return makeToken(TokenType::FILE_EOF);
    }

    auto const c = advance();

    if (isLetterOrUnderscore(c))
    {
        return identifier();
    }

    if (isdigit(c))
    {
        return number();
    }

    switch (c)
    {
        // One char tokens
        case '(': return makeToken(TokenType::LEFT_PAREN);
        case ')': return makeToken(TokenType::RIGHT_PAREN);
        case '{': return makeToken(TokenType::LEFT_BRACE);
        case '}': return makeToken(TokenType::RIGHT_BRACE);
        case ':': return makeToken(TokenType::COLON);
        case ',': return makeToken(TokenType::COMMA);
        case '.': return makeToken(TokenType::DOT);
        case '-': return makeToken(TokenType::MINUS);
        case '+': return makeToken(TokenType::PLUS);
        case ';': return makeToken(TokenType::SEMICOLON);
        case '*': return makeToken(TokenType::STAR);
        case '%': return makeToken(TokenType::MODULO);
        case '^': return makeToken(TokenType::EXPONENT);
        case '/': return makeToken(TokenType::SLASH);

        // Either one or two char tokens
        case '!': return makeToken(match('=') ? TokenType::BANG_EQUAL : TokenType::BANG);
        case '=': return makeToken(match('=') ? TokenType::EQUAL_EQUAL : TokenType::EQUAL);
        case '>':
        {
            if (match('>'))
            {
                return makeToken(TokenType::RSHIFT);
            }

            return match('=') ? makeToken(TokenType::GREATER_EQUAL) : makeToken(TokenType::GREATER);
        }
        case '<':
        {
            if (match('<'))
            {
                return makeToken(TokenType::LSHIFT);
            }

            return match('=') ? makeToken(TokenType::LESS_EQUAL) : makeToken(TokenType::LESS);
        }

        // Literals
        case '"': return string();

        default: break;
    }

    return errorToken("Unexpected character");
}

bool Scanner::isAtEnd() const
{
    return current >= source.length();
}

bool Scanner::isLetterOrUnderscore(const char c)
{
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
}

Token Scanner::makeToken(const TokenType type) const
{
    const auto lexeme = std::string_view{source.begin() + start, source.begin() + current};
    return Token{type, lexeme, line};
}

Token Scanner::errorToken(const std::string_view &message) const
{
    return Token{TokenType::ERROR, message, line};
}

char Scanner::advance()
{
    current++;
    return source[current - 1];
}

bool Scanner::match(const char expected)
{
    if (isAtEnd())
    {
        return false;
    }

    if (source[current] != expected)
    {
        return false;
    }

    current++;
    return true;
}

char Scanner::peek() const
{
    return source[current];
}

char Scanner::peekNext() const
{
    if (isAtEnd())
    {
        return '\0';
    }

    return source[current + 1];
}

Token Scanner::string()
{
    while (peek() != '"' && !isAtEnd())
    {
        if (peek() == '\n')
        {
            line++;
        }

        advance();
    }

    if (isAtEnd())
    {
        return errorToken("Unterminated string.");
    }

    advance(); // Consume closing quote
    return makeToken(TokenType::STRING);
}

Token Scanner::number()
{
    while (isdigit(peek()))
    {
        advance();
    }

    if (peek() == '.' && isdigit(peekNext()))
    {
        advance();
        while (isdigit(peek()))
        {
            advance();
        }
    }

    return makeToken(TokenType::NUMBER);
}

Token Scanner::identifier()
{
    while (isLetterOrUnderscore(peek()) || isdigit(peek()))
    {
        advance();
    }

    return makeToken(identifierType());
}

TokenType Scanner::identifierType()
{
    switch (source[current])
    {
        case 'a': return checkKeyword(1, "nd", TokenType::AND);
        case 'b': return checkKeyword(1, "reak", TokenType::BREAK);
        case 'c':
            if (current - start > 1)
            {
                switch (source[current + 1])
                {
                    case 'l': return checkKeyword(2, "ass", TokenType::CLASS);
                    case 'o': return checkKeyword(2, "ntinue", TokenType::CONTINUE);
                    default: break;
                }
            }
            break;
        case 'd': return checkKeyword(1, "o", TokenType::DO);
        case 'e': return checkKeyword(1, "lse", TokenType::ELSE);
        case 'f':
            if (current - start > 1)
            {
                switch (source[current + 1])
                {
                    case 'a': return checkKeyword(2, "lse", TokenType::FALSE);
                    case 'o': return checkKeyword(2, "r", TokenType::FOR);
                    case 'u': return checkKeyword(2, "n", TokenType::FUN);
                    default: break;
                }
            }
            break;
        case 'i':
            if (current - start > 1)
            {
                switch (source[current + 1])
                {
                    case 'f': return TokenType::IF;
                    case 'm': return checkKeyword(2, "port", TokenType::IMPORT);
                    default: break;
                }
            }
            break;

        case 'l': return checkKeyword(1, "et", TokenType::LET);
        case 'n':
            if (current - start > 1)
            {
                switch (source[current + 1])
                {
                    case 'a': return checkKeyword(2, "nd", TokenType::NAND);
                    case 'o': return checkKeyword(2, "r", TokenType::NOR);
                    case 'u': return checkKeyword(2, "ll", TokenType::NIL);
                    default: break;
                }
            }
            break;
        case 'o': return checkKeyword(1, "r", TokenType::OR);
        case 'p': return checkKeyword(1, "rint", TokenType::PRINT);
        case 'r': return checkKeyword(1, "eturn", TokenType::RETURN);
        case 's':
            if (current - start > 1)
            {
                switch (source[current + 1])
                {
                    case 't': return checkKeyword(2, "atic", TokenType::STATIC);
                    case 'u': return checkKeyword(2, "per", TokenType::SUPER);
                    default: break;
                }
            }
            break;
        case 't':
            if (current - start > 1)
            {
                switch (source[current + 1])
                {
                    case 'r': return checkKeyword(2, "ue", TokenType::TRUE);
                    case 'h': return checkKeyword(2, "is", TokenType::THIS);
                    default: break;
                }
            }
            break;
        case 'w': return checkKeyword(1, "r", TokenType::WHILE);
        case 'x': return checkKeyword(1, "or", TokenType::XOR);
        default: break;
    }

    return TokenType::IDENTIFIER;
}

TokenType Scanner::checkKeyword(const int start, const std::string &rest, const TokenType type) const
{
    if (source.length() - current + 1 >= rest.length()
        && std::memcmp(source.data() + current + start, rest.data(), rest.length()) == 0)
    {
        return type;
    }

    return TokenType::IDENTIFIER;
}

void Scanner::skipWhitespacesAndComments()
{
    for (;;)
    {
        switch (peek())
        {
            case ' ':
            case '\r':
            case '\t':
                advance();
                break;
            case '\n':
                line++;
                advance();
                break;
            case '/':
                if (peekNext() == '/')
                {
                    while (peek() != '\n' && !isAtEnd())
                    {
                        advance();
                    }
                }
                else if (peekNext() == '*')
                {
                    advance(); // Consume the opening /
                    advance(); // Consume the opening *

                    while (!isAtEnd() && (peek() != '*' || peekNext() != '/'))
                    {
                        if (peek() == '\n')
                        {
                            line++;
                        }

                        advance();
                    }

                    advance(); // Consume the closing *
                    advance(); // Consume the closing /
                }
                else
                {
                    return;
                }

                break;
            default:
                return;
        }
    }
}

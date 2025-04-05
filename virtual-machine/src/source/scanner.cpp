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

TokenType Scanner::identifierType() const
{
    const std::string_view lexeme(&source[start], current - start);

    if (lexeme == "and") return TokenType::AND;
    if (lexeme == "break") return TokenType::BREAK;
    if (lexeme == "class") return TokenType::CLASS;
    if (lexeme == "continue") return TokenType::CONTINUE;
    if (lexeme == "do") return TokenType::DO;
    if (lexeme == "else") return TokenType::ELSE;
    if (lexeme == "false") return TokenType::FALSE;
    if (lexeme == "for") return TokenType::FOR;
    if (lexeme == "fun") return TokenType::FUN;
    if (lexeme == "if") return TokenType::IF;
    if (lexeme == "import") return TokenType::IMPORT;
    if (lexeme == "let") return TokenType::LET;
    if (lexeme == "nand") return TokenType::NAND;
    if (lexeme == "nor") return TokenType::NOR;
    if (lexeme == "null") return TokenType::NIL;
    if (lexeme == "or") return TokenType::OR;
    if (lexeme == "print") return TokenType::PRINT;
    if (lexeme == "return") return TokenType::RETURN;
    if (lexeme == "static") return TokenType::STATIC;
    if (lexeme == "super") return TokenType::SUPER;
    if (lexeme == "this") return TokenType::THIS;
    if (lexeme == "true") return TokenType::TRUE;
    if (lexeme == "while") return TokenType::WHILE;
    if (lexeme == "xor") return TokenType::XOR;

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

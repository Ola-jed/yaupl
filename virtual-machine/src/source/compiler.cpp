#include "../include/compiler.h"

#include <iomanip>
#include <iostream>

#include "../include/scanner.h"
#include "../include/token.h"

namespace Compiler
{
    void compile(const std::string &source)
    {
        auto scanner = Scanner{source};
        auto line = -1;

        for (;;)
        {
            const auto token = scanner.scanToken();

            if (token.line != line)
            {
                std::cout << std::setfill('0') << std::setw(4) << token.line;
                line = token.line;
            }
            else
            {
                std::cout << "   | ";
            }

            std::cout << std::setw(2) << token.type << " '" << token.lexeme << "'\n";

            if (token.type == TokenType::FILE_EOF)
            {
                break;
            }
        }
    }
}

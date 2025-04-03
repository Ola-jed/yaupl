#ifndef PARSER_H
#define PARSER_H
#include "token.h"

struct Parser
{
    Token current;
    Token previous;
    bool hadError = false;
    bool panicMode = false;
};
#endif //PARSER_H

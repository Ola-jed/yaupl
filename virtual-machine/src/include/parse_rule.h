#ifndef PARSE_RULE_H
#define PARSE_RULE_H

#include "precedence.h"

class Compiler;
using ParseFn = void (Compiler::*)(bool);

struct ParseRule
{
    ParseFn prefix;
    ParseFn infix;
    Precedence precedence;
};

#endif //PARSE_RULE_H

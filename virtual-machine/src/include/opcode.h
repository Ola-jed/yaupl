#ifndef OPCODE_H
#define OPCODE_H
#include <cstdint>

enum class OpCode: uint8_t
{
    OP_RETURN = 0,
    OP_CONSTANT = 1,
    OP_NEGATE = 2,
    OP_ADD = 3,
    OP_SUBTRACT = 4,
    OP_MULTIPLY = 5,
    OP_DIVIDE = 6,
    OP_EXPONENT = 7,
    OP_LSHIFT = 8,
    OP_RSHIFT = 9,
    OP_MODULO = 10
};

#endif //OPCODE_H

#ifndef OPCODE_H
#define OPCODE_H
#include <cstdint>

enum class OpCode: uint8_t
{
    OP_RETURN = 0,
    OP_CONSTANT = 1,
    OP_NULL = 2,
    OP_TRUE = 3,
    OP_FALSE = 4,
    OP_NEGATE = 5,
    OP_EQUAL = 6,
    OP_GREATER = 7,
    OP_LESS = 8,
    OP_ADD = 9,
    OP_SUBTRACT = 10,
    OP_MULTIPLY = 11,
    OP_DIVIDE = 12,
    OP_NOT = 13,
    OP_EXPONENT = 14,
    OP_LSHIFT = 15,
    OP_RSHIFT = 16,
    OP_MODULO = 17
};

#endif //OPCODE_H

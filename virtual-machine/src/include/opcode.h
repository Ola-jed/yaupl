#ifndef OPCODE_H
#define OPCODE_H
#include <cstdint>

enum class OpCode: uint8_t
{
    OP_RETURN = 0,
    OP_CONSTANT = 1
};

#endif //OPCODE_H

#ifndef OPCODE_H
#define OPCODE_H
#include <cstdint>

enum class OpCode: uint8_t
{
    OP_RETURN,
    OP_CONSTANT
};

#endif //OPCODE_H

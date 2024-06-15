#ifndef CHUNK_H
#define CHUNK_H
#include <cstdint>

enum class OpCode
{
    OP_RETURN
};

struct Chunk
{
    int count;
    int capacity;
    uint8_t *code;

    Chunk(): count(0), capacity(0), code(nullptr)
    {
    }
};
#endif //CHUNK_H

#ifndef CHUNK_H
#define CHUNK_H

#include <cstdint>
#include <cstdlib>
#include <iomanip>
#include <string>

#include "value.h"

struct Chunk
{
    int count;
    int capacity;
    uint8_t *code;
    int *lines;
    ValueArray constants;

    Chunk();

    void write(uint8_t opcode, int line);

    int addConstant(const Value &value);

    void free();

    void disassemble(const std::string &name) const;

    [[nodiscard]] int disassembleInstruction(int offset) const;

    [[nodiscard]] static int simpleInstruction(const std::string &name, int offset);

    [[nodiscard]] int constantInstruction(const std::string &name, int offset) const;
};


#endif //CHUNK_H

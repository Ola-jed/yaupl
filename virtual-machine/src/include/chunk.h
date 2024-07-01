#ifndef CHUNK_H
#define CHUNK_H

#include <concepts>
#include <cstdint>
#include <cstdlib>
#include <iomanip>
#include <iostream>
#include <string>

#include "memory.h"
#include "opcode.h"
#include "value.h"

struct Chunk
{
    int count;
    int capacity;
    uint8_t *code;
    ValueArray constants;

    Chunk(): count(0), capacity(0), code(nullptr)
    {
        constants = ValueArray{};
    }

    void write(const uint8_t opcode)
    {
        if (capacity < count + 1)
        {
            const auto oldCapacity = capacity;
            capacity = growCapacity(oldCapacity);
            code = growArray(code, oldCapacity, capacity);
        }

        code[count++] = opcode;
    }

    int addConstant(const Value &value)
    {
        constants.write(value);
        return constants.count - 1;
    }

    void free()
    {
        constants.free();
        freeArray(code, capacity);
        count = 0;
        capacity = 0;
        code = nullptr;
    }

    void disassemble(const std::string &name) const
    {
        std::cout << "======== " << name << " ========\n";
        for (auto offset = 0; offset < count;)
        {
            offset = disassembleInstruction(offset);
        }
    }

    [[nodiscard]] int disassembleInstruction(const int offset) const
    {
        std::cout << std::setw(4) << std::setfill('0') << offset << std::endl;

        switch (const auto instruction = code[offset])
        {
            case static_cast<uint8_t>(OpCode::OP_RETURN):
                return simpleInstruction("OP_RETURN", offset);
            case static_cast<uint8_t>(OpCode::OP_CONSTANT):
                return constantInstruction("OP_CONSTANT", offset);
            default:
                std::cout << "Unknown opcode " << instruction << "\n";
                return offset + 1;
        }
    }

    [[nodiscard]] int simpleInstruction(const std::string &name, const int offset) const
    {
        std::cout << name << "\n";
        return offset + 1;
    }

    [[nodiscard]] int constantInstruction(const std::string &name, const int offset) const
    {
        const auto constant = code[offset + 1];
        std::cout << name << "(" << std::setw(4) << std::setfill('0') << constant << ") ";
        printValue(constants.values[constant]);
        std::cout << "\n";
        return offset + 2;
    }

    static void printValue(const Value value)
    {
        std::cout << value << " ";
    }
};


#endif //CHUNK_H

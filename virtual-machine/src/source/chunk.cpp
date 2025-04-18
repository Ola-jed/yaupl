#include "../include/chunk.h"
#include "../include/opcode.h"
#include "../include/util.h"

#include <iostream>


Chunk::Chunk(): code(nullptr), lines(nullptr), count(0), capacity(0)
{
    constants = ValueArray{};
}


void Chunk::write(const uint8_t opcode, const int line)
{
    if (capacity < count + 1)
    {
        const auto oldCapacity = capacity;
        capacity = growCapacity(oldCapacity);
        code = growArray(code, oldCapacity, capacity);
        lines = growArray(lines, oldCapacity, capacity);
    }

    code[count] = opcode;
    lines[count] = line;
    count++;
}


int Chunk::addConstant(const Value &value)
{
    constants.write(value);
    return constants.count - 1;
}

void Chunk::free()
{
    constants.free();
    freeArray(code, capacity);
    freeArray(lines, capacity);
    count = 0;
    capacity = 0;
    code = nullptr;
}

void Chunk::disassemble(const std::string &name) const
{
    std::cout << "======== " << name << " ========\n";
    for (auto offset = 0; offset < count;)
    {
        offset = disassembleInstruction(offset);
    }
}

[[nodiscard]] int Chunk::disassembleInstruction(const int offset) const
{
    std::cout << std::setw(4) << std::setfill('0') << offset << " ";

    if (offset > 0 && lines[offset] == lines[offset - 1])
    {
        std::cout << "     |   ";
    }
    else
    {
        std::cout << std::setw(4) << std::setfill('0') << lines[offset] << " ";
    }

    switch (const auto instruction = code[offset])
    {
        case static_cast<uint8_t>(OpCode::OP_RETURN):
            return simpleInstruction("OP_RETURN", offset);
        case static_cast<uint8_t>(OpCode::OP_NEGATE):
            return simpleInstruction("OP_NEGATE", offset);
        case static_cast<uint8_t>(OpCode::OP_CONSTANT):
            return constantInstruction("OP_CONSTANT", offset);
        case static_cast<uint8_t>(OpCode::OP_NULL):
            return simpleInstruction("OP_NULL", offset);
        case static_cast<uint8_t>(OpCode::OP_TRUE):
            return simpleInstruction("OP_TRUE", offset);
        case static_cast<uint8_t>(OpCode::OP_FALSE):
            return simpleInstruction("OP_FALSE", offset);
        case static_cast<uint8_t>(OpCode::OP_ADD):
            return constantInstruction("OP_ADD", offset);
        case static_cast<uint8_t>(OpCode::OP_SUBTRACT):
            return constantInstruction("OP_SUBTRACT", offset);
        case static_cast<uint8_t>(OpCode::OP_MULTIPLY):
            return constantInstruction("OP_MULTIPLY", offset);
        case static_cast<uint8_t>(OpCode::OP_DIVIDE):
            return constantInstruction("OP_DIVIDE", offset);
        case static_cast<uint8_t>(OpCode::OP_EXPONENT):
            return constantInstruction("OP_EXPONENT", offset);
        case static_cast<uint8_t>(OpCode::OP_LSHIFT):
            return constantInstruction("OP_LSHIFT", offset);
        case static_cast<uint8_t>(OpCode::OP_RSHIFT):
            return constantInstruction("OP_RSHIFT", offset);
        case static_cast<uint8_t>(OpCode::OP_MODULO):
            return constantInstruction("OP_MODULO", offset);
        case static_cast<uint8_t>(OpCode::OP_NOT):
            return constantInstruction("OP_NOT", offset);
        case static_cast<uint8_t>(OpCode::OP_EQUAL):
            return constantInstruction("OP_EQUAL", offset);
        case static_cast<uint8_t>(OpCode::OP_GREATER):
            return constantInstruction("OP_GREATER", offset);
        case static_cast<uint8_t>(OpCode::OP_LESS):
            return constantInstruction("OP_LESS", offset);
        case static_cast<uint8_t>(OpCode::OP_PRINT):
            return simpleInstruction("OP_PRINT", offset);
        case static_cast<uint8_t>(OpCode::OP_POP):
            return simpleInstruction("OP_POP", offset);
        case static_cast<uint8_t>(OpCode::OP_DEFINE_GLOBAL):
            return constantInstruction("OP_DEFINE_GLOBAL", offset);
        case static_cast<uint8_t>(OpCode::OP_DEFINE_CONSTANT):
            return constantInstruction("OP_DEFINE_CONSTANT", offset);
        case static_cast<uint8_t>(OpCode::OP_GET_GLOBAL):
            return constantInstruction("OP_GET_GLOBAL", offset);
        case static_cast<uint8_t>(OpCode::OP_SET_GLOBAL):
            return constantInstruction("OP_SET_GLOBAL", offset);
        default:
            std::cout << "Unknown opcode " << instruction << "\n";
            return offset + 1;
    }
}

[[nodiscard]] int Chunk::simpleInstruction(const std::string &name, const int offset)
{
    std::cout << name << "\n";
    return offset + 1;
}

[[nodiscard]] int Chunk::constantInstruction(const std::string &name, const int offset) const
{
    const auto constant = code[offset + 1];
    std::cout << name << "(" << +constant << ") ";
    util::printValue(constants.values[constant]);
    std::cout << "\n";
    return offset + 2;
}

#include "src/include/chunk.h"

int main()
{
    Chunk chunk{};
    const auto constant = chunk.addConstant(1.2);
    chunk.write(static_cast<uint8_t>(OpCode::OP_CONSTANT), 123);
    chunk.write(constant, 123);
    chunk.write(static_cast<uint8_t>(OpCode::OP_RETURN), 123);
    chunk.disassemble("test chunk");
    chunk.free();
    return 0;
}

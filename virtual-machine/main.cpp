#include "src/include/chunk.h"

int main()
{
    Chunk chunk{};
    chunk.write(static_cast<uint8_t>(OpCode::OP_RETURN));
    const auto constant = chunk.addConstant(1.2);
    chunk.write(static_cast<uint8_t>(OpCode::OP_CONSTANT));
    chunk.write(constant);
    chunk.disassemble("test chunk");
    chunk.free();
    return 0;
}

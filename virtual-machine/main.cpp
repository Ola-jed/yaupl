#include "src/include/chunk.h"
#include "src/include/opcode.h"
#include "src/include/vm.h"

int main()
{
    VM vm{};


    Chunk chunk{};
    auto constant = chunk.addConstant(1.2);


    chunk.write(static_cast<uint8_t>(OpCode::OP_CONSTANT), 123);
    chunk.write(constant, 123);

    constant = chunk.addConstant(3.4);
    chunk.write(static_cast<uint8_t>(OpCode::OP_CONSTANT), 123);
    chunk.write(constant, 123);
    chunk.write(static_cast<uint8_t>(OpCode::OP_ADD), 123);
    constant = chunk.addConstant(5.6);
    chunk.write(static_cast<uint8_t>(OpCode::OP_CONSTANT), 123);
    chunk.write(constant, 123);
    chunk.write(static_cast<uint8_t>(OpCode::OP_DIVIDE), 123);
    chunk.write(static_cast<uint8_t>(OpCode::OP_NEGATE), 123);
    chunk.write(static_cast<uint8_t>(OpCode::OP_RETURN), 123);
    vm.interpret(&chunk);
    chunk.free();
    return 0;
}

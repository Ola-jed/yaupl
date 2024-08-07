#include "../include/common.h"
#include "../include/vm.h"

VM::VM()
{
    resetStack();
}

InterpretResult VM::interpret(Chunk *chunk)
{
    this->chunk = chunk;
    instructionPointer = chunk->code;
    return run();
}

InterpretResult VM::run()
{
#define READ_BYTE() (*instructionPointer++)
#define READ_CONSTANT() (chunk->constants.values[READ_BYTE()])
    for (;;)
    {
#ifdef DEBUG_TRACE_EXECUTION
        std::cout << "          ";

        for (auto slot = stack; slot < stackTop; slot++)
        {
            std::cout << "[  ";
            std::cout << *slot << " ";
            std::cout << "  ]";
        }


        chunk->disassembleInstruction(static_cast<int>(instructionPointer - chunk->code));
#endif

        uint8_t instruction;
        switch (instruction = READ_BYTE())
        {
            case static_cast<uint8_t>(OpCode::OP_CONSTANT):
            {
                auto const constant = READ_CONSTANT();
                push(constant);
                util::printValue(constant);
                std::cout << "\n";
                break;
            }
            case static_cast<uint8_t>(OpCode::OP_RETURN):
            {
                util::printValue(pop());
                std::cout << "\n";
                return InterpretResult::OK;
            }
        }
    }
#undef READ_BYTE
#undef READ_CONSTANT
}

void VM::resetStack()
{
    stackTop = stack;
}

void VM::push(const Value value)
{
    *stackTop = value;
    stackTop++;
}

Value VM::pop()
{
    stackTop--;
    return *stackTop;
}

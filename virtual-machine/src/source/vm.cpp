#include "../include/common.h"
#include "../include/opcode.h"
#include "../include/util.h"
#include "../include/vm.h"

#include <iostream>
#include <valarray>

VM::VM()
{
    resetStack();
}

VM::~VM()
{
}


InterpretResult VM::interpret(Chunk *chunk)
{
    this->chunk = chunk;
    instructionPointer = chunk->code;
    return run();
}

InterpretResult VM::run()
{
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
        switch (instruction = readByte())
        {
            case static_cast<uint8_t>(OpCode::OP_CONSTANT):
            {
                auto const constant = readConstant();
                push(constant);
                util::printValue(constant);
                std::cout << "\n";
                break;
            }
            case static_cast<uint8_t>(OpCode::OP_NEGATE):
            {
                push(-pop());
                break;
            }
            case static_cast<uint8_t>(OpCode::OP_ADD):
            {
                binaryOp([](auto a, auto b) { return a + b; });
                break;
            }
            case static_cast<uint8_t>(OpCode::OP_SUBTRACT):
            {
                binaryOp([](auto a, auto b) { return a - b; });
                break;
            }
            case static_cast<uint8_t>(OpCode::OP_MULTIPLY):
            {
                binaryOp([](auto a, auto b) { return a * b; });
                break;
            }
            case static_cast<uint8_t>(OpCode::OP_DIVIDE):
            {
                binaryOp([](auto a, auto b) { return a / b; });
                break;
            }
            case static_cast<uint8_t>(OpCode::OP_EXPONENT):
            {
                binaryOp([](auto a, auto b) { return std::pow(a, b); });
                break;
            }
            case static_cast<uint8_t>(OpCode::OP_LSHIFT):
            {
                binaryOp([](auto a, auto b) {
                    return static_cast<double>(static_cast<long>(a) << static_cast<long>(b));
                });
                break;
            }
            case static_cast<uint8_t>(OpCode::OP_RSHIFT):
            {
                binaryOp([](auto a, auto b) {
                    return static_cast<double>(static_cast<long>(a) >> static_cast<long>(b));
                });
                break;
            }
            case static_cast<uint8_t>(OpCode::OP_MODULO):
            {
                binaryOp([](auto a, auto b) { return static_cast<long>(a) % static_cast<long>(b); });
                break;
            }
            case static_cast<uint8_t>(OpCode::OP_RETURN):
            {
                util::printValue(pop());
                std::cout << "\n";
                return InterpretResult::OK;
            }
            default: ;
        }
    }
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

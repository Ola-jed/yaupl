#ifndef VM_H
#define VM_H
#include <functional>
#include <memory>

#include "chunk.h"
#include "compiler.h"
#include "interpret_result.h"

struct VM
{
    static constexpr int STACK_MAX = 256;
    Compiler compiler{};
    std::unique_ptr<Chunk> chunk;
    uint8_t *instructionPointer;
    Value stack[STACK_MAX];
    Value *stackTop;

    VM(): chunk(std::make_unique<Chunk>())
    {
        resetStack();
    }

    ~VM();

    InterpretResult interpret(const std::string &source);

    InterpretResult run();

    void resetStack();

    void push(Value value);

    Value pop();

    uint8_t readByte()
    {
        return *instructionPointer++;
    }

    Value readConstant()
    {
        return chunk->constants.values[readByte()];
    }

    void binaryOp(const std::function<Value(Value, Value)> &op)
    {
        const Value b = pop();
        const Value a = pop();
        push(op(a, b));
    }
};

#endif //VM_H

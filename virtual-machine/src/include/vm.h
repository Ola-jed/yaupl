#ifndef VM_H
#define VM_H
#include <functional>

#include "chunk.h"
#include "interpret_result.h"

struct VM
{
    static constexpr int STACK_MAX = 256;

    Chunk *chunk;
    uint8_t *instructionPointer;
    Value stack[STACK_MAX];
    Value *stackTop;

    VM();

    ~VM();

    InterpretResult interpret(Chunk *chunk);

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

#ifndef VM_H
#define VM_H
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
};

#endif //VM_H

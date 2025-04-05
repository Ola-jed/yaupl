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

    void push(Value);

    Value pop();

    [[nodiscard]] Value peek(int) const;

    [[nodiscard]] uint8_t readByte();

    [[nodiscard]] Value readConstant();

    [[nodiscard]] bool binaryOp(const std::function<Value(double, double)> &op);

    void runtimeError(const std::string &);
};

#endif //VM_H

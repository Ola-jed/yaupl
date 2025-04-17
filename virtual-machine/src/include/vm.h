#ifndef VM_H
#define VM_H
#include <functional>
#include <memory>

#include "chunk.h"
#include "compiler.h"
#include "environment.h"
#include "interpret_result.h"

template<typename T>
concept AllowedType = std::same_as<T, double> || std::same_as<T, std::string> || std::same_as<T, bool>;

struct VM
{
    static constexpr int STACK_MAX = 256;
    Compiler compiler{};
    Environment env{};
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

    void push(const Value &);

    Value pop();

    [[nodiscard]] Value peek(int distance = 0) const;

    [[nodiscard]] uint8_t readByte();

    [[nodiscard]] Value readConstant();

    template<AllowedType T>
    [[nodiscard]] bool binaryOp(const std::function<Value(T, T)> &op);

    void runtimeError(const std::string &);
};

#endif //VM_H

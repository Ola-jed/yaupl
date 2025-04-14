#include "../include/common.h"
#include "../include/opcode.h"
#include "../include/util.h"
#include "../include/vm.h"

#include <format>
#include <iostream>
#include <valarray>

#include "../include/compiler.h"

VM::~VM() = default;

InterpretResult VM::interpret(const std::string &source)
{
    chunk = std::make_unique<Chunk>();
    instructionPointer = chunk->code;
    if (!compiler.compile(source, chunk.get()))
    {
        return InterpretResult::COMPILE_ERROR;
    }

    this->instructionPointer = chunk->code;
    auto const result = run();
    return result;
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
            util::printValue(*slot);
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
            case static_cast<uint8_t>(OpCode::OP_NULL):
            {
                push(std::monostate{});
                break;
            }
            case static_cast<uint8_t>(OpCode::OP_TRUE):
            {
                push(true);
                break;
            }
            case static_cast<uint8_t>(OpCode::OP_FALSE):
            {
                push(false);
                break;
            }
            case static_cast<uint8_t>(OpCode::OP_NOT):
            {
                push(isFalsey(pop()));
                break;
            }
            case static_cast<uint8_t>(OpCode::OP_NEGATE):
            {
                if (!std::holds_alternative<double>(peek(0)))
                {
                    runtimeError("Operand must be a number.");
                    return InterpretResult::RUNTIME_ERROR;
                }

                push(-std::get<double>(pop()));
                break;
            }
            case static_cast<uint8_t>(OpCode::OP_ADD):
            {
                auto result = false;
                if (std::holds_alternative<double>(peek(0)) && std::holds_alternative<double>(peek(1)))
                {
                    result = binaryOp<double>([](const double a, const double b) { return a + b; });
                }
                else if (std::holds_alternative<std::string>(peek(0)) && std::holds_alternative<std::string>(peek(1)))
                {
                    result = binaryOp<std::string>([](const std::string &a, const std::string &b)
                    {
                        return a + b;
                    });
                }

                if (!result)
                {
                    return InterpretResult::RUNTIME_ERROR;
                }

                break;
            }
            case static_cast<uint8_t>(OpCode::OP_SUBTRACT):
            {
                const auto result = binaryOp<double>([](const double a, const double b) { return a - b; });
                if (!result)
                {
                    return InterpretResult::RUNTIME_ERROR;
                }

                break;
            }
            case static_cast<uint8_t>(OpCode::OP_MULTIPLY):
            {
                const auto result = binaryOp<double>([](const double a, const double b) { return a * b; });
                if (!result)
                {
                    return InterpretResult::RUNTIME_ERROR;
                }

                break;
            }
            case static_cast<uint8_t>(OpCode::OP_DIVIDE):
            {
                const auto result = binaryOp<double>([](const double a, const double b) { return a / b; });
                if (!result)
                {
                    return InterpretResult::RUNTIME_ERROR;
                }

                break;
            }
            case static_cast<uint8_t>(OpCode::OP_EXPONENT):
            {
                const auto result = binaryOp<double>([](const double a, const double b)
                {
                    return std::pow(a, b);
                });

                if (!result)
                {
                    return InterpretResult::RUNTIME_ERROR;
                }

                break;
            }
            case static_cast<uint8_t>(OpCode::OP_LSHIFT):
            {
                const auto result = binaryOp<double>([](const double a, const double b)
                {
                    return static_cast<double>(static_cast<long>(a) << static_cast<long>(b));
                });

                if (!result)
                {
                    return InterpretResult::RUNTIME_ERROR;
                }

                break;
            }
            case static_cast<uint8_t>(OpCode::OP_RSHIFT):
            {
                const auto result = binaryOp<double>([](const double a, const double b)
                {
                    return static_cast<double>(static_cast<long>(a) >> static_cast<long>(b));
                });

                if (!result)
                {
                    return InterpretResult::RUNTIME_ERROR;
                }

                break;
            }
            case static_cast<uint8_t>(OpCode::OP_MODULO):
            {
                const auto result = binaryOp<double>([](const double a, const double b)
                {
                    return static_cast<double>(static_cast<long>(a) % static_cast<long>(b));
                });

                if (!result)
                {
                    return InterpretResult::RUNTIME_ERROR;
                }

                break;
            }
            case static_cast<uint8_t>(OpCode::OP_EQUAL):
            {
                const auto a = pop();
                const auto b = pop();
                push(valuesEqual(a, b));
                break;
            }
            case static_cast<uint8_t>(OpCode::OP_GREATER):
            {
                const auto result = binaryOp<bool>([](const double a, const double b)
                {
                    return a > b;
                });

                if (!result)
                {
                    return InterpretResult::RUNTIME_ERROR;
                }

                break;
            }
            case static_cast<uint8_t>(OpCode::OP_LESS):
            {
                const auto result = binaryOp<bool>([](const double a, const double b)
                {
                    return a < b;
                });

                if (!result)
                {
                    return InterpretResult::RUNTIME_ERROR;
                }

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

void VM::push(const Value &value)
{
    *stackTop = value;
    stackTop++;
}

Value VM::pop()
{
    stackTop--;
    return *stackTop;
}

Value VM::peek(const int distance) const
{
    return stackTop[-1 - distance];
}

uint8_t VM::readByte()
{
    return *instructionPointer++;
}

Value VM::readConstant()
{
    return chunk->constants.values[readByte()];
}


template<AllowedType T>
bool VM::binaryOp(const std::function<Value(T, T)> &op)
{
    if (!std::holds_alternative<T>(peek(0)) || !std::holds_alternative<T>(peek(1)))
    {
        if (std::is_same_v<T, double>)
        {
            runtimeError("Operands must be numbers.");
        }
        if (std::is_same_v<T, bool>)
        {
            runtimeError("Operands must be numbers.");
        }
        else if (std::is_same_v<T, std::string>)
        {
            runtimeError("Operands must be strings.");
        }
        else
        {
            runtimeError("Operands must be of the correct type.");
        }

        return false;
    }

    const Value b = pop();
    const Value a = pop();
    push(op(std::get<T>(a), std::get<T>(b)));
    return true;
}

void VM::runtimeError(const std::string &message)
{
    std::cerr << message << "\n";

    const auto instruction = instructionPointer - chunk->code - 1;
    int line = chunk->lines[instruction];
    std::cerr << std::format("[line {}] in script\n", line); // C++20
    resetStack();
}

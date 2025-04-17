#ifndef RUNNER_H
#define RUNNER_H
#include <fstream>

#include "src/include/interpret_result.h"
#include "src/include/vm.h"

class Runner
{
    VM vm{};

public:
    InterpretResult interpret(const std::string &source)
    {
        return vm.interpret(source);
    }

    void repl()
    {
        std::string line;

        for (;;)
        {
            std::cout << std::endl << "ypl++ :  ";
            if (!std::getline(std::cin, line))
            {
                std::cout << std::endl;
                break;
            }

            interpret(line);
        }
    }

    void runFile(const std::string_view &path)
    {
        const auto source = util::readFile(path);
        const auto result = interpret(source);

        if (result == InterpretResult::COMPILE_ERROR)
        {
            exit(65);
        }

        if (result == InterpretResult::RUNTIME_ERROR)
        {
            exit(70);
        }
    }
};

#endif //RUNNER_H

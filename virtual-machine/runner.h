#ifndef RUNNER_H
#define RUNNER_H
#include <fstream>
#include <sstream>

#include "src/include/interpret_result.h"

namespace Runner
{
    inline std::string readFile(const std::string_view &path)
    {
        std::ifstream ifs{path.data()};

        if (!ifs.good())
        {
            std::cerr << "Failed to open file " << path << std::endl;
            exit(74);
        }

        std::ostringstream oss;
        oss << ifs.rdbuf();
        return oss.str();
    }

    inline InterpretResult interpret(const std::string &source)
    {
        return InterpretResult::OK;
    }

    inline void repl()
    {
        std::string line;

        for (;;)
        {
            std::cout << std::endl << "ypl++ : ";
            if (!std::getline(std::cin, line))
            {
                std::cout << std::endl;
                break;
            }

            interpret(line);
        }
    }

    inline void runFile(const std::string_view &path)
    {
        const auto source = readFile(path);
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
}

#endif //RUNNER_H

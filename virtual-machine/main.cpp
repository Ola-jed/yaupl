#include <iostream>

#include "runner.h"
#include "src/include/chunk.h"
#include "src/include/vm.h"

int main(const int argc, const char *argv[])
{
    Runner runner{};
    if (argc == 1)
    {
        runner.repl();
    }
    else if (argc == 2)
    {
        runner.runFile(argv[1]);
    }
    else
    {
        std::cerr << "Usage : yaupl [path]" << std::endl;
        exit(64);
    }

    return 0;
}

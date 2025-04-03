#include <iostream>

#include "runner.h"
#include "src/include/chunk.h"
#include "src/include/vm.h"

int main(const int argc, const char *argv[])
{
    VM vm{};
    if (argc == 1)
    {
        Runner::repl();
    }
    else
    {
        std::cerr << "Usage : yaupl++ [path]" << std::endl;
        exit(64);
    }

    return 0;
}

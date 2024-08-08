#ifndef UTIL_H
#define UTIL_H

#include <iostream>

#include "value.h"

namespace util
{
    inline void printValue(const Value value)
    {
        std::cout << value << " ";
    }
}

#endif //UTIL_H

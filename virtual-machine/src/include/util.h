#ifndef UTIL_H
#define UTIL_H

#include <iostream>

#include "value.h"

namespace util
{
    inline void printValue(const Value value)
    {
        if (std::holds_alternative<double>(value))
        {
            std::cout << std::get<double>(value) << " ";
        }
        else if (std::holds_alternative<bool>(value))
        {
            std::cout << std::get<bool>(value) << " ";
        }
        else
        {
            std::cout << "NULL ";
        }
    }
}

#endif //UTIL_H

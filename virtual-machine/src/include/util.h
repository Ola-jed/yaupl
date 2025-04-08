#ifndef UTIL_H
#define UTIL_H

#include <iostream>

#include "value.h"

namespace util
{
    inline void printValue(const Value &value)
    {
        if (std::holds_alternative<double>(value))
        {
            std::cout << std::get<double>(value) << " ";
        }
        else if (std::holds_alternative<bool>(value))
        {
            std::cout << std::boolalpha << std::get<bool>(value) << " ";
        }
        else if (std::holds_alternative<std::string>(value))
        {
            std::cout << std::get<std::string>(value) << " ";
        }
        else
        {
            std::cout << "NULL ";
        }
    }
}

#endif //UTIL_H

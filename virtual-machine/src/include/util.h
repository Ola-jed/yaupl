#ifndef UTIL_H
#define UTIL_H

#include <fstream>
#include <iostream>
#include <sstream>

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
}

#endif //UTIL_H

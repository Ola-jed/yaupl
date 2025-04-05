#ifndef VALUE_H
#define VALUE_H
#include <variant>

#include "memory.h"

using Value = std::variant<std::monostate, double, bool>;

inline bool valuesEqual(const Value x, const Value y)
{
    if (std::holds_alternative<std::monostate>(x) && std::holds_alternative<std::monostate>(y))
    {
        return true;
    }

    if (std::holds_alternative<bool>(x) && std::holds_alternative<bool>(y))
    {
        return std::get<bool>(x) == std::get<bool>(y);
    }

    if (std::holds_alternative<double>(x) && std::holds_alternative<double>(y))
    {
        return std::get<double>(x) == std::get<double>(y);
    }

    return false;
}

inline bool isFalsey(const Value value)
{
    if (std::holds_alternative<std::monostate>(value))
    {
        return true;
    }

    if (std::holds_alternative<bool>(value))
    {
        return !std::get<bool>(value);
    }

    return false;
}

struct ValueArray
{
    int capacity;
    int count;
    Value *values;

    ValueArray(): capacity(0), count(0), values(nullptr)
    {
    }

    void write(const Value &value)
    {
        if (capacity < count + 1)
        {
            const auto oldCapacity = capacity;
            capacity = growCapacity(oldCapacity);
            values = growArray(values, oldCapacity, capacity);
        }

        values[count++] = value;
    }

    void free()
    {
        freeArray(values, capacity);
        capacity = 0;
        count = 0;
        values = nullptr;
    }
};

#endif //VALUE_H

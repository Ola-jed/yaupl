#ifndef VALUE_H
#define VALUE_H
#include <variant>

#include "memory.h"

using Value = std::variant<std::monostate, double, bool, std::string>;

inline bool valuesEqual(const Value &x, const Value &y)
{
    return std::visit([]<typename U, typename V>(const U &a, const V &b) -> bool
    {
        if constexpr (std::is_same_v<U, V> || std::is_arithmetic_v<U> && std::is_arithmetic_v<V>)
        {
            return a == b;
        }
        else
        {
            return false;
        }
    }, x, y);
}

inline bool isFalsey(const Value &value)
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

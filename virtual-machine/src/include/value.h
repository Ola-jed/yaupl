#ifndef VALUE_H
#define VALUE_H
#include "memory.h"

using Value = double;

struct ValueArray
{
    int capacity;
    int count;
    Value *values;

    ValueArray(): capacity(0), count(0), values(nullptr)
    {
    }

    void write(const Value value)
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

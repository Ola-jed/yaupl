#ifndef MEMORY_H
#define MEMORY_H

#include <concepts>
#include <cstdlib>

template<typename T>
    requires std::integral<T>
T growCapacity(T capacity)
{
    return capacity < 8 ? 8 : capacity * 2;
}

template<typename T>
T *reallocate(T *pointer, const size_t oldSize, const size_t newSize)
{
    if (newSize == 0)
    {
        free(pointer);
        return nullptr;
    }

    auto result = realloc(pointer, newSize);
    if (result == nullptr)
    {
        exit(EXIT_FAILURE);
    }

    return static_cast<T *>(result);
}

template<typename T>
T *growArray(T *pointer, const size_t oldSize, const size_t newSize)
{
    return reallocate(pointer, sizeof(T) * oldSize, sizeof(T) * newSize);
}

template<typename T>
T *freeArray(T *pointer, const size_t oldSize)
{
    return reallocate(pointer, sizeof(T) * oldSize, 0);
}

#endif //MEMORY_H

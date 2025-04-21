#ifndef LOCAL_H
#define LOCAL_H
#include "token.h"

struct Local
{
    Token name;
    int depth{};

    Local(const Token &name, const int depth)
        : name(name),
          depth(depth)
    {
    }

    Local() = default;
};
#endif //LOCAL_H

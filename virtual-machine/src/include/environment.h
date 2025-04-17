#ifndef ENVIRONMENT_H
#define ENVIRONMENT_H
#include <optional>
#include <unordered_map>
#include <string>
#include <unordered_set>

#include "value.h"

class Environment
{
    std::unordered_map<std::string, Value> bindings{};
    std::unordered_set<std::string> constants{};

public:
    Environment() = default;

    bool declare(const std::string &name, const Value &value, bool constant = false);

    bool set(const std::string &name, const Value &value);

    std::optional<Value> get(const std::string &name) const;
};
#endif //ENVIRONMENT_H

#include "../include/environment.h"

bool Environment::declare(const std::string &name, const Value &value, bool constant)
{
    if (bindings.contains(name))
    {
        return false;
    }

    bindings.emplace(name, value);
    if (constant)
    {
        constants.insert(name);
    }
    return true;
}

bool Environment::set(const std::string &name, const Value &value)
{
    const auto iterator = bindings.find(name);
    if (iterator == bindings.end() || constants.contains(name))
    {
        return false;
    }

    if (iterator->second.index() != value.index())
    {
        return false;
    }

    iterator->second = value;
    return true;
}

std::optional<Value> Environment::get(const std::string &name) const
{
    if (!bindings.contains(name))
    {
        return std::nullopt;
    }

    return std::make_optional(bindings.at(name));
}

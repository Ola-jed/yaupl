#include "../include/environment.h"

EnvironmentDeclareResult Environment::declare(const std::string &name, const Value &value, bool constant)
{
    if (bindings.contains(name))
    {
        return EnvironmentDeclareResult::ALREADY_DEFINED;
    }

    bindings.emplace(name, value);
    if (constant)
    {
        constants.insert(name);
    }
    return EnvironmentDeclareResult::OK;
}

EnvironmentSetResult Environment::set(const std::string &name, const Value &value)
{
    const auto iterator = bindings.find(name);
    if (iterator == bindings.end())
    {
        return EnvironmentSetResult::NOT_DEFINED;
    }

    if (constants.contains(name))
    {
        return EnvironmentSetResult::CONSTANT_NOT_REASSIGNABLE;
    }

    if (iterator->second.index() != value.index())
    {
        return EnvironmentSetResult::TYPE_MISMATCH;
    }

    iterator->second = value;
    return EnvironmentSetResult::OK;
}

std::optional<Value> Environment::get(const std::string &name) const
{
    if (!bindings.contains(name))
    {
        return std::nullopt;
    }

    return std::make_optional(bindings.at(name));
}

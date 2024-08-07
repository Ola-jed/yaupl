cmake_minimum_required(VERSION 3.29)
project(virtual_machine)

set(CMAKE_CXX_STANDARD 20)

add_executable(virtual_machine main.cpp
        src/include/chunk.h
        src/include/opcode.h
        src/include/value.h
        src/include/memory.h
        src/include/vm.h
        src/include/interpret_result.h
        src/source/vm.cpp
        src/include/util.h
        src/include/common.h
)

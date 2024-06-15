cmake_minimum_required(VERSION 3.29)
project(virtual_machine)

set(CMAKE_CXX_STANDARD 20)

add_executable(virtual_machine main.cpp
        src/include/chunk.h)

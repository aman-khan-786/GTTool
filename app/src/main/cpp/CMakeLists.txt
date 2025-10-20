# SDK 33 Compatible CMake Configuration
cmake_minimum_required(VERSION 3.10.2)

project("gttool")

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Add include directories
include_directories(${CMAKE_SOURCE_DIR})

# Create shared library with all C++ sources
add_library(
    native-lib
    SHARED
    
    # Main JNI bridge
    native-lib.cpp
    
    # Memory operations
    memory_scanner.cpp
    memory_scanner.h
    
    # Process utilities
    process_utils.cpp
    process_utils.h
)

# Find required system libraries
find_library(log-lib log)
find_library(android-lib android)

# Link libraries
target_link_libraries(
    native-lib
    
    # Android log library
    ${log-lib}
    
    # Android library
    ${android-lib}
)

# Compiler flags for SDK 33
target_compile_options(
    native-lib
    PRIVATE
    -Wall
    -Wextra
    -O2
    -fvisibility=hidden
)
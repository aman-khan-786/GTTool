#ifndef PROCESS_UTILS_H
#define PROCESS_UTILS_H

#include <string>

namespace process_utils {
    int get_process_id(const std::string &package_name);
    bool is_process_running(int pid);
}

#endif
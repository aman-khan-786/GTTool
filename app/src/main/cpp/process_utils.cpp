#include "process_utils.h"
#include <fstream>
#include <sstream>

namespace process_utils {

    int get_process_id(const std::string &package_name) {
        return 0; // Placeholder
    }

    bool is_process_running(int pid) {
        std::string path = "/proc/" + std::to_string(pid);
        std::ifstream check(path);
        return check.good();
    }
}
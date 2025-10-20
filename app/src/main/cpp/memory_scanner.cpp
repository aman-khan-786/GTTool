#include "memory_scanner.h"
#include <cstring>

namespace memory_scanner {

    bool scan_memory_chunk(
            const unsigned char *data,
            size_t data_size,
            const unsigned char *pattern,
            size_t pattern_size,
            std::vector<size_t> &offsets) {

        for (size_t i = 0; i <= data_size - pattern_size; i++) {
            if (memcmp(data + i, pattern, pattern_size) == 0) {
                offsets.push_back(i);
            }
        }

        return !offsets.empty();
    }

    bool fuzzy_scan(
            const unsigned char *data,
            size_t data_size,
            const unsigned char *pattern,
            size_t pattern_size,
            int tolerance,
            std::vector<size_t> &offsets) {

        for (size_t i = 0; i <= data_size - pattern_size; i++) {
            int diff_count = 0;
            for (size_t j = 0; j < pattern_size; j++) {
                if (data[i + j] != pattern[j]) {
                    diff_count++;
                    if (diff_count > tolerance) break;
                }
            }
            if (diff_count <= tolerance) {
                offsets.push_back(i);
            }
        }

        return !offsets.empty();
    }
}
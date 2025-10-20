#ifndef MEMORY_SCANNER_H
#define MEMORY_SCANNER_H

#include <vector>
#include <cstddef>

namespace memory_scanner {

    bool scan_memory_chunk(
            const unsigned char *data,
            size_t data_size,
            const unsigned char *pattern,
            size_t pattern_size,
            std::vector<size_t> &offsets);

    bool fuzzy_scan(
            const unsigned char *data,
            size_t data_size,
            const unsigned char *pattern,
            size_t pattern_size,
            int tolerance,
            std::vector<size_t> &offsets);
}

#endif
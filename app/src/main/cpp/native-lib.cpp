#include <jni.h>
#include <string>
#include <vector>
#include <fcntl.h>
#include <unistd.h>
#include <sys/uio.h>
#include <android/log.h>

#define LOG_TAG "GTTool-Native"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

extern "C" {

JNIEXPORT jlongArray JNICALL
Java_com_arman_dev_gttool_util_NativeLib_scanMemoryRegion(
        JNIEnv *env,
        jobject /* this */,
        jint pid,
        jlong start_address,
        jlong end_address,
        jbyteArray search_bytes) {

    jsize search_length = env->GetArrayLength(search_bytes);
    jbyte *search_data = env->GetByteArrayElements(search_bytes, nullptr);

    std::vector<jlong> found_addresses;

    const size_t buffer_size = 4096;
    unsigned char buffer[buffer_size];
    jlong current_address = start_address;

    while (current_address < end_address) {
        size_t read_size = std::min((size_t)(end_address - current_address), buffer_size);

        struct iovec local_iov = {buffer, read_size};
        struct iovec remote_iov = {(void *)current_address, read_size};

        ssize_t bytes_read = process_vm_readv(pid, &local_iov, 1, &remote_iov, 1, 0);

        if (bytes_read > 0) {
            for (size_t i = 0; i <= bytes_read - search_length; i++) {
                bool match = true;
                for (jsize j = 0; j < search_length; j++) {
                    if (buffer[i + j] != (unsigned char)search_data[j]) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    found_addresses.push_back(current_address + i);
                }
            }
        }

        current_address += bytes_read;
    }

    env->ReleaseByteArrayElements(search_bytes, search_data, JNI_ABORT);

    jlongArray result = env->NewLongArray(found_addresses.size());
    if (result != nullptr) {
        env->SetLongArrayRegion(result, 0, found_addresses.size(), found_addresses.data());
    }

    return result;
}

JNIEXPORT jbyteArray JNICALL
Java_com_arman_dev_gttool_util_NativeLib_readMemory(
        JNIEnv *env,
        jobject /* this */,
        jint pid,
        jlong address,
        jint size) {

    unsigned char *buffer = new unsigned char[size];

    struct iovec local_iov = {buffer, (size_t)size};
    struct iovec remote_iov = {(void *)address, (size_t)size};

    ssize_t bytes_read = process_vm_readv(pid, &local_iov, 1, &remote_iov, 1, 0);

    if (bytes_read != size) {
        delete[] buffer;
        return nullptr;
    }

    jbyteArray result = env->NewByteArray(size);
    env->SetByteArrayRegion(result, 0, size, (jbyte *)buffer);

    delete[] buffer;
    return result;
}

JNIEXPORT jboolean JNICALL
Java_com_arman_dev_gttool_util_NativeLib_writeMemory(
        JNIEnv *env,
        jobject /* this */,
        jint pid,
        jlong address,
        jbyteArray data) {

    jsize data_length = env->GetArrayLength(data);
    jbyte *data_bytes = env->GetByteArrayElements(data, nullptr);

    struct iovec local_iov = {data_bytes, (size_t)data_length};
    struct iovec remote_iov = {(void *)address, (size_t)data_length};

    ssize_t bytes_written = process_vm_writev(pid, &local_iov, 1, &remote_iov, 1, 0);

    env->ReleaseByteArrayElements(data, data_bytes, JNI_ABORT);

    return bytes_written == data_length;
}

JNIEXPORT jstring JNICALL
Java_com_arman_dev_gttool_util_NativeLib_getProcessMaps(
        JNIEnv *env,
        jobject /* this */,
        jint pid) {

    char maps_path[256];
    snprintf(maps_path, sizeof(maps_path), "/proc/%d/maps", pid);

    FILE *maps_file = fopen(maps_path, "r");
    if (!maps_file) {
        return nullptr;
    }

    std::string maps_content;
    char line[512];
    while (fgets(line, sizeof(line), maps_file)) {
        maps_content += line;
    }

    fclose(maps_file);
    return env->NewStringUTF(maps_content.c_str());
}

}
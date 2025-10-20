package com.arman.dev.gttool.service;

interface IShizukuService {
    byte[] readMemory(int pid, long address, int size);
    boolean writeMemory(int pid, long address, in byte[] data);
    int getProcessId(String packageName);
    void destroy();
}
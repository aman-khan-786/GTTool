package com.arman.dev.gttool.data.model

data class MemoryRegion(
    val startAddress: Long,
    val endAddress: Long,
    val permissions: String,
    val offset: Long,
    val device: String,
    val inode: Long,
    val pathname: String
) {
    val size: Long get() = endAddress - startAddress
    val isReadable: Boolean get() = permissions.contains('r')
    val isWritable: Boolean get() = permissions.contains('w')
    val isExecutable: Boolean get() = permissions.contains('x')
    
    fun isAnonymous(): Boolean = pathname.isEmpty() || pathname == "[anon]"
    fun isHeap(): Boolean = pathname == "[heap]"
    fun isStack(): Boolean = pathname == "[stack]"
}
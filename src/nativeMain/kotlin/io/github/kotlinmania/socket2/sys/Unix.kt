// port-lint: source sys/unix.rs
package io.github.kotlinmania.socket2.sys

import io.github.kotlinmania.socket2.*
import io.github.kotlinmania.socket2.cinterop.*
import kotlinx.cinterop.*
import kotlin.experimental.ExperimentalNativeApi

/**
 * Native platform actual implementation using C++ wrapper via cinterop.
 * Mirrors Rust socket2 sys/unix.rs implementation.
 */

// Raw socket FD value class
internal actual value class RawSocket(actual val fd: Int)

// Shutdown constants
internal actual val SHUT_RD: Int = 0
internal actual val SHUT_WR: Int = 1
internal actual val SHUT_RDWR: Int = 2

// Actual syscall implementations using C++ wrapper

@OptIn(ExperimentalForeignApi::class)
internal actual fun socket(family: Int, type: Int, protocol: Int): Result<RawSocket> {
    val fd = socket2_socket(family, type, protocol)
    return if (fd == -1) {
        val errnoVal = socket2_get_errno()
        val errMsg = socket2_get_error_string(errnoVal)?.toKString() ?: "Unknown error (errno=$errnoVal)"
        Result.failure(IOException("socket() failed: $errMsg"))
    } else {
        Result.success(RawSocket(fd))
    }
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun bind(fd: RawSocket, addr: SockAddr): Result<Unit> {
    val storage = socket2_addr_storage_new() ?: return Result.failure(IOException("Failed to allocate address storage"))

    try {
        // Set family
        socket2_addr_storage_set_family(storage, addr.storage.ssFamily)

        // Copy the rest of the address data
        // TODO: Properly populate address fields based on family

        val result = socket2_bind(fd.fd, storage, addr.len())
        return if (result == -1) {
            val errnoVal = socket2_get_errno()
            val errMsg = socket2_get_error_string(errnoVal)?.toKString() ?: "Unknown error (errno=$errnoVal)"
            Result.failure(IOException("bind() failed: $errMsg"))
        } else {
            Result.success(Unit)
        }
    } finally {
        socket2_addr_storage_free(storage)
    }
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun connect(fd: RawSocket, addr: SockAddr): Result<Unit> {
    val storage = socket2_addr_storage_new() ?: return Result.failure(IOException("Failed to allocate address storage"))

    try {
        // Set family
        socket2_addr_storage_set_family(storage, addr.storage.ssFamily)

        // Copy the rest of the address data
        // TODO: Properly populate address fields based on family

        val result = socket2_connect(fd.fd, storage, addr.len())
        return if (result == -1) {
            val errnoVal = socket2_get_errno()
            val errMsg = socket2_get_error_string(errnoVal)?.toKString() ?: "Unknown error (errno=$errnoVal)"
            Result.failure(IOException("connect() failed: $errMsg"))
        } else {
            Result.success(Unit)
        }
    } finally {
        socket2_addr_storage_free(storage)
    }
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun listen(fd: RawSocket, backlog: Int): Result<Unit> {
    val result = socket2_listen(fd.fd, backlog)
    return if (result == -1) {
        val errnoVal = socket2_get_errno()
        val errMsg = socket2_get_error_string(errnoVal)?.toKString() ?: "Unknown error (errno=$errnoVal)"
        Result.failure(IOException("listen() failed: $errMsg"))
    } else {
        Result.success(Unit)
    }
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun accept(fd: RawSocket): Result<Pair<RawSocket, SockAddr>> = memScoped {
    val storage = socket2_addr_storage_new() ?: return Result.failure(IOException("Failed to allocate address storage"))

    try {
        val addrLen = alloc<UIntVar>()
        addrLen.value = 128u

        val newFd = socket2_accept(fd.fd, storage, addrLen.ptr)

        if (newFd == -1) {
            val errnoVal = socket2_get_errno()
            val errMsg = socket2_get_error_string(errnoVal)?.toKString() ?: "Unknown error (errno=$errnoVal)"
            Result.failure(IOException("accept() failed: $errMsg"))
        } else {
            // Extract family and convert to SockAddr
            val family = socket2_addr_storage_get_family(storage)
            val kotlinStorage = SockaddrStorage(
                ssFamily = family,
                padding = ByteArray(126)  // TODO: Extract full address data
            )
            val sockAddrStorage = SockAddrStorage(kotlinStorage)
            val addr = SockAddr.new(sockAddrStorage, addrLen.value)
            Result.success(Pair(RawSocket(newFd), addr))
        }
    } finally {
        socket2_addr_storage_free(storage)
    }
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun shutdown(fd: RawSocket, how: Int): Result<Unit> {
    val result = socket2_shutdown(fd.fd, how)
    return if (result == -1) {
        val errnoVal = socket2_get_errno()
        val errMsg = socket2_get_error_string(errnoVal)?.toKString() ?: "Unknown error (errno=$errnoVal)"
        Result.failure(IOException("shutdown() failed: $errMsg"))
    } else {
        Result.success(Unit)
    }
}

@OptIn(ExperimentalForeignApi::class, ExperimentalUnsignedTypes::class)
internal actual fun recv(fd: RawSocket, buffer: ByteArray, flags: Int): Result<Int> {
    return buffer.usePinned { pinned ->
        val bytesRead = socket2_recv(fd.fd, pinned.addressOf(0), buffer.size.toULong(), flags).toInt()
        if (bytesRead == -1) {
            val errnoVal = socket2_get_errno()
            val errMsg = socket2_get_error_string(errnoVal)?.toKString() ?: "Unknown error (errno=$errnoVal)"
            Result.failure(IOException("recv() failed: $errMsg"))
        } else {
            Result.success(bytesRead)
        }
    }
}

@OptIn(ExperimentalForeignApi::class, ExperimentalUnsignedTypes::class)
internal actual fun send(fd: RawSocket, buffer: ByteArray, flags: Int): Result<Int> {
    return buffer.usePinned { pinned ->
        val bytesSent = socket2_send(fd.fd, pinned.addressOf(0), buffer.size.toULong(), flags).toInt()
        if (bytesSent == -1) {
            val errnoVal = socket2_get_errno()
            val errMsg = socket2_get_error_string(errnoVal)?.toKString() ?: "Unknown error (errno=$errnoVal)"
            Result.failure(IOException("send() failed: $errMsg"))
        } else {
            Result.success(bytesSent)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun close(fd: RawSocket): Result<Unit> {
    val result = socket2_close(fd.fd)
    return if (result == -1) {
        val errnoVal = socket2_get_errno()
        val errMsg = socket2_get_error_string(errnoVal)?.toKString() ?: "Unknown error (errno=$errnoVal)"
        Result.failure(IOException("close() failed: $errMsg"))
    } else {
        Result.success(Unit)
    }
}

class IOException(message: String) : Exception(message)

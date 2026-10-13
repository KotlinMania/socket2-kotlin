// port-lint: source sys/unix.rs
package io.github.kotlinmania.socket2.sys

import io.github.kotlinmania.libc.SockaddrStorage
import io.github.kotlinmania.socket2.SockAddr
import io.github.kotlinmania.socket2.SockAddrStorage
import io.github.kotlinmania.socket2.cinterop.Socket2AddrStorage
import io.github.kotlinmania.socket2.cinterop.socket2_accept
import io.github.kotlinmania.socket2.cinterop.socket2_addr_storage_free
import io.github.kotlinmania.socket2.cinterop.socket2_addr_storage_get_family
import io.github.kotlinmania.socket2.cinterop.socket2_addr_storage_get_padding
import io.github.kotlinmania.socket2.cinterop.socket2_addr_storage_new
import io.github.kotlinmania.socket2.cinterop.socket2_addr_storage_set_family
import io.github.kotlinmania.socket2.cinterop.socket2_addr_storage_set_padding
import io.github.kotlinmania.socket2.cinterop.socket2_bind
import io.github.kotlinmania.socket2.cinterop.socket2_close
import io.github.kotlinmania.socket2.cinterop.socket2_connect
import io.github.kotlinmania.socket2.cinterop.socket2_get_errno
import io.github.kotlinmania.socket2.cinterop.socket2_get_error_string
import io.github.kotlinmania.socket2.cinterop.socket2_get_nonblocking
import io.github.kotlinmania.socket2.cinterop.socket2_getpeername
import io.github.kotlinmania.socket2.cinterop.socket2_getsockname
import io.github.kotlinmania.socket2.cinterop.socket2_getsockopt
import io.github.kotlinmania.socket2.cinterop.socket2_listen
import io.github.kotlinmania.socket2.cinterop.socket2_opt_ip_ttl
import io.github.kotlinmania.socket2.cinterop.socket2_opt_ipproto_ip
import io.github.kotlinmania.socket2.cinterop.socket2_opt_ipproto_tcp
import io.github.kotlinmania.socket2.cinterop.socket2_opt_so_broadcast
import io.github.kotlinmania.socket2.cinterop.socket2_opt_so_error
import io.github.kotlinmania.socket2.cinterop.socket2_opt_so_keepalive
import io.github.kotlinmania.socket2.cinterop.socket2_opt_so_rcvbuf
import io.github.kotlinmania.socket2.cinterop.socket2_opt_so_reuseaddr
import io.github.kotlinmania.socket2.cinterop.socket2_opt_so_reuseport
import io.github.kotlinmania.socket2.cinterop.socket2_opt_so_sndbuf
import io.github.kotlinmania.socket2.cinterop.socket2_opt_so_type
import io.github.kotlinmania.socket2.cinterop.socket2_opt_sol_socket
import io.github.kotlinmania.socket2.cinterop.socket2_opt_tcp_nodelay
import io.github.kotlinmania.socket2.cinterop.socket2_recv
import io.github.kotlinmania.socket2.cinterop.socket2_recvfrom
import io.github.kotlinmania.socket2.cinterop.socket2_send
import io.github.kotlinmania.socket2.cinterop.socket2_sendto
import io.github.kotlinmania.socket2.cinterop.socket2_set_nonblocking
import io.github.kotlinmania.socket2.cinterop.socket2_setsockopt
import io.github.kotlinmania.socket2.cinterop.socket2_shutdown
import io.github.kotlinmania.socket2.cinterop.socket2_socket
import io.github.kotlinmania.socket2.cinterop.socket2_socketpair
import io.github.kotlinmania.socket2.cinterop.socket2_try_clone
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.UIntVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.toKString
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value

/**
 * Native platform actual implementation using C wrapper via cinterop.
 * Mirrors Rust socket2 sys/unix.rs implementation.
 */

// Raw socket FD value class
internal actual value class RawSocket(
    actual val fd: Int,
)

// Shutdown constants
internal actual val SHUT_RD: Int = 0
internal actual val SHUT_WR: Int = 1
internal actual val SHUT_RDWR: Int = 2

// Socket option constants
@OptIn(ExperimentalForeignApi::class)
internal val SOL_SOCKET_VAL: Int get() = socket2_opt_sol_socket()

@OptIn(ExperimentalForeignApi::class)
internal val SO_REUSEADDR_VAL: Int get() = socket2_opt_so_reuseaddr()

@OptIn(ExperimentalForeignApi::class)
internal val SO_REUSEPORT_VAL: Int get() = socket2_opt_so_reuseport()

@OptIn(ExperimentalForeignApi::class)
internal val SO_BROADCAST_VAL: Int get() = socket2_opt_so_broadcast()

@OptIn(ExperimentalForeignApi::class)
internal val SO_KEEPALIVE_VAL: Int get() = socket2_opt_so_keepalive()

@OptIn(ExperimentalForeignApi::class)
internal val SO_SNDBUF_VAL: Int get() = socket2_opt_so_sndbuf()

@OptIn(ExperimentalForeignApi::class)
internal val SO_RCVBUF_VAL: Int get() = socket2_opt_so_rcvbuf()

@OptIn(ExperimentalForeignApi::class)
internal val SO_ERROR_VAL: Int get() = socket2_opt_so_error()

@OptIn(ExperimentalForeignApi::class)
internal val SO_TYPE_VAL: Int get() = socket2_opt_so_type()

@OptIn(ExperimentalForeignApi::class)
internal val IPPROTO_TCP_VAL: Int get() = socket2_opt_ipproto_tcp()

@OptIn(ExperimentalForeignApi::class)
internal val TCP_NODELAY_VAL: Int get() = socket2_opt_tcp_nodelay()

@OptIn(ExperimentalForeignApi::class)
internal val IPPROTO_IP_VAL: Int get() = socket2_opt_ipproto_ip()

@OptIn(ExperimentalForeignApi::class)
internal val IP_TTL_VAL: Int get() = socket2_opt_ip_ttl()

@OptIn(ExperimentalForeignApi::class)
internal fun extractSockAddr(storage: CPointer<Socket2AddrStorage>, len: UInt): SockAddr {
    val family = socket2_addr_storage_get_family(storage)
    val padding = ByteArray(126)
    val paddingLen = padding.size.toUInt()
    padding.usePinned { pinned ->
        val paddingPtr = pinned.addressOf(0).reinterpret<UByteVar>()
        socket2_addr_storage_get_padding(storage, paddingPtr, paddingLen)
    }
    val kotlinStorage =
        SockaddrStorage(
            ssFamily = family,
            padding = padding,
        )
    return SockAddr.new(SockAddrStorage(kotlinStorage), len)
}

// Actual syscall implementations using C wrapper

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
        socket2_addr_storage_set_family(storage, addr.storage.ssFamily)
        val padding = addr.storage.padding
        val paddingLen = padding.size.toUInt()
        padding.usePinned { pinned ->
            val paddingPtr = pinned.addressOf(0).reinterpret<UByteVar>()
            socket2_addr_storage_set_padding(storage, paddingPtr, paddingLen)
        }
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
        socket2_addr_storage_set_family(storage, addr.storage.ssFamily)
        val padding = addr.storage.padding
        val paddingLen = padding.size.toUInt()
        padding.usePinned { pinned ->
            val paddingPtr = pinned.addressOf(0).reinterpret<UByteVar>()
            socket2_addr_storage_set_padding(storage, paddingPtr, paddingLen)
        }
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
internal actual fun accept(fd: RawSocket): Result<Pair<RawSocket, SockAddr>> =
    memScoped {
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
                val addr = extractSockAddr(storage, addrLen.value)
                Result.success(Pair(RawSocket(newFd), addr))
            }
        } finally {
            socket2_addr_storage_free(storage)
        }
    }

@OptIn(ExperimentalForeignApi::class)
internal fun getsockname(fd: RawSocket): Result<SockAddr> =
    memScoped {
        val storage = socket2_addr_storage_new() ?: return Result.failure(IOException("Failed to allocate address storage"))
        try {
            val addrLen = alloc<UIntVar>()
            addrLen.value = 128u
            val res = socket2_getsockname(fd.fd, storage, addrLen.ptr)
            if (res == -1) {
                val errnoVal = socket2_get_errno()
                val errMsg = socket2_get_error_string(errnoVal)?.toKString() ?: "Unknown error (errno=$errnoVal)"
                Result.failure(IOException("getsockname() failed: $errMsg"))
            } else {
                Result.success(extractSockAddr(storage, addrLen.value))
            }
        } finally {
            socket2_addr_storage_free(storage)
        }
    }

@OptIn(ExperimentalForeignApi::class)
internal fun getpeername(fd: RawSocket): Result<SockAddr> =
    memScoped {
        val storage = socket2_addr_storage_new() ?: return Result.failure(IOException("Failed to allocate address storage"))
        try {
            val addrLen = alloc<UIntVar>()
            addrLen.value = 128u
            val res = socket2_getpeername(fd.fd, storage, addrLen.ptr)
            if (res == -1) {
                val errnoVal = socket2_get_errno()
                val errMsg = socket2_get_error_string(errnoVal)?.toKString() ?: "Unknown error (errno=$errnoVal)"
                Result.failure(IOException("getpeername() failed: $errMsg"))
            } else {
                Result.success(extractSockAddr(storage, addrLen.value))
            }
        } finally {
            socket2_addr_storage_free(storage)
        }
    }

@OptIn(ExperimentalForeignApi::class)
internal fun socketpair(family: Int, type: Int, protocol: Int): Result<Pair<RawSocket, RawSocket>> =
    memScoped {
        val sv = allocArray<IntVar>(2)
        val res = socket2_socketpair(family, type, protocol, sv)
        if (res == -1) {
            val errnoVal = socket2_get_errno()
            val errMsg = socket2_get_error_string(errnoVal)?.toKString() ?: "Unknown error (errno=$errnoVal)"
            Result.failure(IOException("socketpair() failed: $errMsg"))
        } else {
            Result.success(Pair(RawSocket(sv[0]), RawSocket(sv[1])))
        }
    }

@OptIn(ExperimentalForeignApi::class)
internal fun tryClone(fd: RawSocket): Result<RawSocket> {
    val newFd = socket2_try_clone(fd.fd)
    return if (newFd == -1) {
        val errnoVal = socket2_get_errno()
        val errMsg = socket2_get_error_string(errnoVal)?.toKString() ?: "Unknown error (errno=$errnoVal)"
        Result.failure(IOException("try_clone() failed: $errMsg"))
    } else {
        Result.success(RawSocket(newFd))
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun setNonblocking(fd: RawSocket, nonblocking: Boolean): Result<Unit> {
    val res = socket2_set_nonblocking(fd.fd, if (nonblocking) 1 else 0)
    return if (res == -1) {
        val errnoVal = socket2_get_errno()
        val errMsg = socket2_get_error_string(errnoVal)?.toKString() ?: "Unknown error (errno=$errnoVal)"
        Result.failure(IOException("set_nonblocking() failed: $errMsg"))
    } else {
        Result.success(Unit)
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun nonblocking(fd: RawSocket): Result<Boolean> {
    val res = socket2_get_nonblocking(fd.fd)
    return if (res == -1) {
        val errnoVal = socket2_get_errno()
        val errMsg = socket2_get_error_string(errnoVal)?.toKString() ?: "Unknown error (errno=$errnoVal)"
        Result.failure(IOException("nonblocking() failed: $errMsg"))
    } else {
        Result.success(res != 0)
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun getsockoptInt(fd: RawSocket, level: Int, optname: Int): Result<Int> =
    memScoped {
        val optval = alloc<IntVar>()
        val optlen = alloc<UIntVar>()
        optlen.value = sizeOf<IntVar>().toUInt()
        val res = socket2_getsockopt(fd.fd, level, optname, optval.ptr, optlen.ptr)
        if (res == -1) {
            val errnoVal = socket2_get_errno()
            val errMsg = socket2_get_error_string(errnoVal)?.toKString() ?: "Unknown error (errno=$errnoVal)"
            Result.failure(IOException("getsockopt() failed: $errMsg"))
        } else {
            Result.success(optval.value)
        }
    }

@OptIn(ExperimentalForeignApi::class)
internal fun setsockoptInt(fd: RawSocket, level: Int, optname: Int, value: Int): Result<Unit> =
    memScoped {
        val optval = alloc<IntVar>()
        optval.value = value
        val res = socket2_setsockopt(fd.fd, level, optname, optval.ptr, sizeOf<IntVar>().toUInt())
        if (res == -1) {
            val errnoVal = socket2_get_errno()
            val errMsg = socket2_get_error_string(errnoVal)?.toKString() ?: "Unknown error (errno=$errnoVal)"
            Result.failure(IOException("setsockopt() failed: $errMsg"))
        } else {
            Result.success(Unit)
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
internal actual fun recv(fd: RawSocket, buffer: ByteArray, flags: Int): Result<Int> =
    buffer.usePinned { pinned ->
        val bytesRead = socket2_recv(fd.fd, pinned.addressOf(0), buffer.size.toULong(), flags).toInt()
        if (bytesRead == -1) {
            val errnoVal = socket2_get_errno()
            val errMsg = socket2_get_error_string(errnoVal)?.toKString() ?: "Unknown error (errno=$errnoVal)"
            Result.failure(IOException("recv() failed: $errMsg"))
        } else {
            Result.success(bytesRead)
        }
    }

@OptIn(ExperimentalForeignApi::class, ExperimentalUnsignedTypes::class)
internal fun recvfrom(fd: RawSocket, buffer: ByteArray, flags: Int): Result<Pair<Int, SockAddr>> =
    memScoped {
        val storage = socket2_addr_storage_new() ?: return Result.failure(IOException("Failed to allocate address storage"))
        try {
            val addrLen = alloc<UIntVar>()
            addrLen.value = 128u
            buffer.usePinned { pinned ->
                val bytesRead = socket2_recvfrom(fd.fd, pinned.addressOf(0), buffer.size.toULong(), flags, storage, addrLen.ptr).toInt()
                if (bytesRead == -1) {
                    val errnoVal = socket2_get_errno()
                    val errMsg = socket2_get_error_string(errnoVal)?.toKString() ?: "Unknown error (errno=$errnoVal)"
                    Result.failure(IOException("recvfrom() failed: $errMsg"))
                } else {
                    val addr = extractSockAddr(storage, addrLen.value)
                    Result.success(Pair(bytesRead, addr))
                }
            }
        } finally {
            socket2_addr_storage_free(storage)
        }
    }

@OptIn(ExperimentalForeignApi::class, ExperimentalUnsignedTypes::class)
internal actual fun send(fd: RawSocket, buffer: ByteArray, flags: Int): Result<Int> =
    buffer.usePinned { pinned ->
        val bytesSent = socket2_send(fd.fd, pinned.addressOf(0), buffer.size.toULong(), flags).toInt()
        if (bytesSent == -1) {
            val errnoVal = socket2_get_errno()
            val errMsg = socket2_get_error_string(errnoVal)?.toKString() ?: "Unknown error (errno=$errnoVal)"
            Result.failure(IOException("send() failed: $errMsg"))
        } else {
            Result.success(bytesSent)
        }
    }

@OptIn(ExperimentalForeignApi::class, ExperimentalUnsignedTypes::class)
internal fun sendto(fd: RawSocket, buffer: ByteArray, flags: Int, addr: SockAddr): Result<Int> {
    val storage = socket2_addr_storage_new() ?: return Result.failure(IOException("Failed to allocate address storage"))
    try {
        socket2_addr_storage_set_family(storage, addr.storage.ssFamily)
        val padding = addr.storage.padding
        val paddingLen = padding.size.toUInt()
        padding.usePinned { pinned ->
            val paddingPtr = pinned.addressOf(0).reinterpret<UByteVar>()
            socket2_addr_storage_set_padding(storage, paddingPtr, paddingLen)
        }
        return buffer.usePinned { pinned ->
            val bytesSent = socket2_sendto(fd.fd, pinned.addressOf(0), buffer.size.toULong(), flags, storage, addr.len()).toInt()
            if (bytesSent == -1) {
                val errnoVal = socket2_get_errno()
                val errMsg = socket2_get_error_string(errnoVal)?.toKString() ?: "Unknown error (errno=$errnoVal)"
                Result.failure(IOException("sendto() failed: $errMsg"))
            } else {
                Result.success(bytesSent)
            }
        }
    } finally {
        socket2_addr_storage_free(storage)
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

class IOException(
    message: String,
) : Exception(message)

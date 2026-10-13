// port-lint: source sys/unix.rs
package io.github.kotlinmania.socket2.sys

import io.github.kotlinmania.libc.SockaddrStorage
import io.github.kotlinmania.libc.unix.bsd.apple.SHUT_RD
import io.github.kotlinmania.libc.unix.bsd.apple.SHUT_RDWR
import io.github.kotlinmania.libc.unix.bsd.apple.SHUT_WR
import io.github.kotlinmania.socket2.Domain
import io.github.kotlinmania.socket2.Shutdown
import io.github.kotlinmania.socket2.SockAddr
import io.github.kotlinmania.socket2.SockAddrStorage
import io.github.kotlinmania.socket2.SocketProtocol
import io.github.kotlinmania.socket2.SocketType
import io.github.kotlinmania.socket2.cinterop.Socket2AddrStorage
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
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import kotlin.time.Duration
import io.github.kotlinmania.libc.unix.access as libcAccess
import io.github.kotlinmania.libc.unix.close as libcClose
import io.github.kotlinmania.libc.unix.listen as libcListen
import io.github.kotlinmania.libc.unix.shutdown as libcShutdown
import io.github.kotlinmania.libc.unix.socket as libcSocket
import io.github.kotlinmania.libc.unix.strerror as libcStrerror
import io.github.kotlinmania.socket2.cinterop.socket2_accept as socket2Accept
import io.github.kotlinmania.socket2.cinterop.socket2_addr_storage_free as socket2AddrStorageFree
import io.github.kotlinmania.socket2.cinterop.socket2_addr_storage_get_family as socket2AddrStorageGetFamily
import io.github.kotlinmania.socket2.cinterop.socket2_addr_storage_get_padding as socket2AddrStorageGetPadding
import io.github.kotlinmania.socket2.cinterop.socket2_addr_storage_new as socket2AddrStorageNew
import io.github.kotlinmania.socket2.cinterop.socket2_addr_storage_set_family as socket2AddrStorageSetFamily
import io.github.kotlinmania.socket2.cinterop.socket2_addr_storage_set_padding as socket2AddrStorageSetPadding
import io.github.kotlinmania.socket2.cinterop.socket2_bind as socket2Bind
import io.github.kotlinmania.socket2.cinterop.socket2_connect as socket2Connect
import io.github.kotlinmania.socket2.cinterop.socket2_get_errno as socket2GetErrno
import io.github.kotlinmania.socket2.cinterop.socket2_get_nonblocking as socket2GetNonblocking
import io.github.kotlinmania.socket2.cinterop.socket2_getpeername as socket2Getpeername
import io.github.kotlinmania.socket2.cinterop.socket2_getsockname as socket2Getsockname
import io.github.kotlinmania.socket2.cinterop.socket2_getsockopt as socket2Getsockopt
import io.github.kotlinmania.socket2.cinterop.socket2_opt_ip_ttl as socket2OptIpTtl
import io.github.kotlinmania.socket2.cinterop.socket2_opt_ipproto_ip as socket2OptIpprotoIp
import io.github.kotlinmania.socket2.cinterop.socket2_opt_ipproto_tcp as socket2OptIpprotoTcp
import io.github.kotlinmania.socket2.cinterop.socket2_opt_so_broadcast as socket2OptSoBroadcast
import io.github.kotlinmania.socket2.cinterop.socket2_opt_so_error as socket2OptSoError
import io.github.kotlinmania.socket2.cinterop.socket2_opt_so_keepalive as socket2OptSoKeepalive
import io.github.kotlinmania.socket2.cinterop.socket2_opt_so_rcvbuf as socket2OptSoRcvbuf
import io.github.kotlinmania.socket2.cinterop.socket2_opt_so_reuseaddr as socket2OptSoReuseaddr
import io.github.kotlinmania.socket2.cinterop.socket2_opt_so_reuseport as socket2OptSoReuseport
import io.github.kotlinmania.socket2.cinterop.socket2_opt_so_sndbuf as socket2OptSoSndbuf
import io.github.kotlinmania.socket2.cinterop.socket2_opt_so_type as socket2OptSoType
import io.github.kotlinmania.socket2.cinterop.socket2_opt_sol_socket as socket2OptSolSocket
import io.github.kotlinmania.socket2.cinterop.socket2_opt_tcp_nodelay as socket2OptTcpNodelay
import io.github.kotlinmania.socket2.cinterop.socket2_poll_connect as socket2PollConnect
import io.github.kotlinmania.socket2.cinterop.socket2_recv as socket2Recv
import io.github.kotlinmania.socket2.cinterop.socket2_recvfrom as socket2Recvfrom
import io.github.kotlinmania.socket2.cinterop.socket2_send as socket2Send
import io.github.kotlinmania.socket2.cinterop.socket2_sendto as socket2Sendto
import io.github.kotlinmania.socket2.cinterop.socket2_set_nonblocking as socket2SetNonblocking
import io.github.kotlinmania.socket2.cinterop.socket2_setsockopt as socket2Setsockopt
import io.github.kotlinmania.socket2.cinterop.socket2_socketpair as socket2Socketpair
import io.github.kotlinmania.socket2.cinterop.socket2_try_clone as socket2TryClone

/**
 * Native platform actual implementation using C wrapper via cinterop.
 * Mirrors Rust socket2 sys/unix.rs implementation.
 */

// Raw socket FD value class
internal value class RawSocket(
    val fd: Int,
)

/**
 * Owned file descriptor corresponding to Rust's std::os::fd::OwnedFd.
 */
internal typealias OwnedFd = RawSocket

/**
 * Safely constructs a [Socket] from a raw socket descriptor.
 */
internal fun socketFromRaw(socket: RawSocket): Socket = SysSocket(socket)

/**
 * Returns the raw socket descriptor for [socket].
 */
internal fun socketAsRaw(socket: Socket): RawSocket = socket.rawSocket

/**
 * Consumes the [socket] and returns its raw socket descriptor.
 */
internal fun socketIntoRaw(socket: Socket): RawSocket = socket.rawSocket

internal actual class SysSocket internal constructor(
    internal val rawSocket: RawSocket,
) {
    actual val raw: Int get() = rawSocket.fd

    actual fun bind(address: SockAddr): Result<Unit> = bind(rawSocket, address)

    actual fun connect(address: SockAddr): Result<Unit> = connect(rawSocket, address)

    actual fun connectTimeout(
        address: SockAddr,
        timeout: Duration,
    ): Result<Unit> = connectTimeout(rawSocket, address, timeout)

    actual fun listen(backlog: Int): Result<Unit> = listen(rawSocket, backlog)

    actual fun accept(): Result<Pair<SysSocket, SockAddr>> =
        accept(rawSocket).map { (newRaw, addr) -> Pair(SysSocket(newRaw), addr) }

    actual fun localAddr(): Result<SockAddr> = getsockname(rawSocket)

    actual fun peerAddr(): Result<SockAddr> = getpeername(rawSocket)

    actual fun type(): Result<SocketType> = getsockoptInt(rawSocket, SOL_SOCKET_VAL, SO_TYPE_VAL).map { SocketType(it) }

    actual fun tryClone(): Result<SysSocket> = tryClone(rawSocket).map { SysSocket(it) }

    actual fun setNonblocking(nonblocking: Boolean): Result<Unit> = setNonblocking(rawSocket, nonblocking)

    actual fun nonblocking(): Result<Boolean> = nonblocking(rawSocket)

    actual fun shutdown(how: Shutdown): Result<Unit> {
        val howValue =
            when (how) {
                Shutdown.Read -> SHUT_RD
                Shutdown.Write -> SHUT_WR
                Shutdown.Both -> SHUT_RDWR
            }
        return shutdown(rawSocket, howValue)
    }

    actual fun recv(
        buffer: ByteArray,
        flags: Int,
    ): Result<Int> = recv(rawSocket, buffer, flags)

    actual fun recvFrom(
        buffer: ByteArray,
        flags: Int,
    ): Result<Pair<Int, SockAddr>> = recvfrom(rawSocket, buffer, flags)

    actual fun send(
        buffer: ByteArray,
        flags: Int,
    ): Result<Int> = send(rawSocket, buffer, flags)

    actual fun sendTo(
        buffer: ByteArray,
        flags: Int,
        address: SockAddr,
    ): Result<Int> = sendto(rawSocket, buffer, flags, address)

    actual fun close(): Result<Unit> = close(rawSocket)

    actual fun takeError(): Result<Int?> = getsockoptInt(rawSocket, SOL_SOCKET_VAL, SO_ERROR_VAL).map { if (it == 0) null else it }

    actual fun reuseAddress(): Result<Boolean> = getsockoptInt(rawSocket, SOL_SOCKET_VAL, SO_REUSEADDR_VAL).map { it != 0 }

    actual fun setReuseAddress(reuse: Boolean): Result<Unit> =
        setsockoptInt(rawSocket, SOL_SOCKET_VAL, SO_REUSEADDR_VAL, if (reuse) 1 else 0)

    actual fun reusePort(): Result<Boolean> {
        val opt = SO_REUSEPORT_VAL
        if (opt == -1) return Result.failure(UnsupportedOperationException("SO_REUSEPORT not supported on this platform"))
        return getsockoptInt(rawSocket, SOL_SOCKET_VAL, opt).map { it != 0 }
    }

    actual fun setReusePort(reuse: Boolean): Result<Unit> {
        val opt = SO_REUSEPORT_VAL
        if (opt == -1) return Result.failure(UnsupportedOperationException("SO_REUSEPORT not supported on this platform"))
        return setsockoptInt(rawSocket, SOL_SOCKET_VAL, opt, if (reuse) 1 else 0)
    }

    actual fun broadcast(): Result<Boolean> = getsockoptInt(rawSocket, SOL_SOCKET_VAL, SO_BROADCAST_VAL).map { it != 0 }

    actual fun setBroadcast(broadcast: Boolean): Result<Unit> =
        setsockoptInt(rawSocket, SOL_SOCKET_VAL, SO_BROADCAST_VAL, if (broadcast) 1 else 0)

    actual fun keepalive(): Result<Boolean> = getsockoptInt(rawSocket, SOL_SOCKET_VAL, SO_KEEPALIVE_VAL).map { it != 0 }

    actual fun setKeepalive(keepalive: Boolean): Result<Unit> =
        setsockoptInt(rawSocket, SOL_SOCKET_VAL, SO_KEEPALIVE_VAL, if (keepalive) 1 else 0)

    actual fun sendBufferSize(): Result<Int> = getsockoptInt(rawSocket, SOL_SOCKET_VAL, SO_SNDBUF_VAL)

    actual fun setSendBufferSize(size: Int): Result<Unit> = setsockoptInt(rawSocket, SOL_SOCKET_VAL, SO_SNDBUF_VAL, size)

    actual fun recvBufferSize(): Result<Int> = getsockoptInt(rawSocket, SOL_SOCKET_VAL, SO_RCVBUF_VAL)

    actual fun setRecvBufferSize(size: Int): Result<Unit> = setsockoptInt(rawSocket, SOL_SOCKET_VAL, SO_RCVBUF_VAL, size)

    actual fun nodelay(): Result<Boolean> = getsockoptInt(rawSocket, IPPROTO_TCP_VAL, TCP_NODELAY_VAL).map { it != 0 }

    actual fun setNodelay(nodelay: Boolean): Result<Unit> =
        setsockoptInt(rawSocket, IPPROTO_TCP_VAL, TCP_NODELAY_VAL, if (nodelay) 1 else 0)

    actual fun ttl(): Result<UInt> = getsockoptInt(rawSocket, IPPROTO_IP_VAL, IP_TTL_VAL).map { it.toUInt() }

    actual fun setTtl(ttl: UInt): Result<Unit> = setsockoptInt(rawSocket, IPPROTO_IP_VAL, IP_TTL_VAL, ttl.toInt())

    actual companion object {
        actual fun fromRaw(raw: Int): SysSocket = SysSocket(RawSocket(raw))

        actual fun new(
            domain: Domain,
            type: SocketType,
            protocol: SocketProtocol?,
        ): Result<SysSocket> {
            val protocolValue = protocol?.value ?: 0
            return socket(domain.value, type.value, protocolValue).map { SysSocket(it) }
        }

        actual fun pair(
            domain: Domain,
            type: SocketType,
            protocol: SocketProtocol?,
        ): Result<Pair<SysSocket, SysSocket>> {
            val protocolValue = protocol?.value ?: 0
            return socketpair(domain.value, type.value, protocolValue).map { (a, b) ->
                Pair(SysSocket(a), SysSocket(b))
            }
        }
    }
}

// Socket option constants
@OptIn(ExperimentalForeignApi::class)
internal val SOL_SOCKET_VAL: Int get() = socket2OptSolSocket()

@OptIn(ExperimentalForeignApi::class)
internal val SO_REUSEADDR_VAL: Int get() = socket2OptSoReuseaddr()

@OptIn(ExperimentalForeignApi::class)
internal val SO_REUSEPORT_VAL: Int get() = socket2OptSoReuseport()

@OptIn(ExperimentalForeignApi::class)
internal val SO_BROADCAST_VAL: Int get() = socket2OptSoBroadcast()

@OptIn(ExperimentalForeignApi::class)
internal val SO_KEEPALIVE_VAL: Int get() = socket2OptSoKeepalive()

@OptIn(ExperimentalForeignApi::class)
internal val SO_SNDBUF_VAL: Int get() = socket2OptSoSndbuf()

@OptIn(ExperimentalForeignApi::class)
internal val SO_RCVBUF_VAL: Int get() = socket2OptSoRcvbuf()

@OptIn(ExperimentalForeignApi::class)
internal val SO_ERROR_VAL: Int get() = socket2OptSoError()

@OptIn(ExperimentalForeignApi::class)
internal val SO_TYPE_VAL: Int get() = socket2OptSoType()

@OptIn(ExperimentalForeignApi::class)
internal val IPPROTO_TCP_VAL: Int get() = socket2OptIpprotoTcp()

@OptIn(ExperimentalForeignApi::class)
internal val TCP_NODELAY_VAL: Int get() = socket2OptTcpNodelay()

@OptIn(ExperimentalForeignApi::class)
internal val IPPROTO_IP_VAL: Int get() = socket2OptIpprotoIp()

@OptIn(ExperimentalForeignApi::class)
internal val IP_TTL_VAL: Int get() = socket2OptIpTtl()

@OptIn(ExperimentalForeignApi::class)
internal fun extractSockAddr(storage: CPointer<Socket2AddrStorage>, len: UInt): SockAddr {
    val family = socket2AddrStorageGetFamily(storage)
    val padding = ByteArray(126)
    val paddingLen = padding.size.toUInt()
    padding.usePinned { pinned ->
        val paddingPtr = pinned.addressOf(0).reinterpret<UByteVar>()
        socket2AddrStorageGetPadding(storage, paddingPtr, paddingLen)
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
internal fun socket(family: Int, type: Int, protocol: Int): Result<RawSocket> {
    val fd = libcSocket(family, type, protocol)
    return if (fd == -1) {
        val errnoVal = socket2GetErrno()
        val errMsg = libcStrerror(errnoVal) ?: "Unknown error (errno=$errnoVal)"
        Result.failure(IOException("socket() failed: $errMsg"))
    } else {
        Result.success(RawSocket(fd))
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun bind(fd: RawSocket, addr: SockAddr): Result<Unit> {
    val storage = socket2AddrStorageNew() ?: return Result.failure(IOException("Failed to allocate address storage"))
    try {
        socket2AddrStorageSetFamily(storage, addr.storage.ssFamily)
        val padding = addr.storage.padding
        val paddingLen = padding.size.toUInt()
        padding.usePinned { pinned ->
            val paddingPtr = pinned.addressOf(0).reinterpret<UByteVar>()
            socket2AddrStorageSetPadding(storage, paddingPtr, paddingLen)
        }
        val result = socket2Bind(fd.fd, storage, addr.len())
        return if (result == -1) {
            val errnoVal = socket2GetErrno()
            val errMsg = libcStrerror(errnoVal) ?: "Unknown error (errno=$errnoVal)"
            Result.failure(IOException("bind() failed: $errMsg"))
        } else {
            Result.success(Unit)
        }
    } finally {
        socket2AddrStorageFree(storage)
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun connect(fd: RawSocket, addr: SockAddr): Result<Unit> {
    val storage = socket2AddrStorageNew() ?: return Result.failure(IOException("Failed to allocate address storage"))
    try {
        socket2AddrStorageSetFamily(storage, addr.storage.ssFamily)
        val padding = addr.storage.padding
        val paddingLen = padding.size.toUInt()
        padding.usePinned { pinned ->
            val paddingPtr = pinned.addressOf(0).reinterpret<UByteVar>()
            socket2AddrStorageSetPadding(storage, paddingPtr, paddingLen)
        }
        val result = socket2Connect(fd.fd, storage, addr.len())
        return if (result == -1) {
            val errnoVal = socket2GetErrno()
            val errMsg = libcStrerror(errnoVal) ?: "Unknown error (errno=$errnoVal)"
            Result.failure(IOException("connect() failed: $errMsg"))
        } else {
            Result.success(Unit)
        }
    } finally {
        socket2AddrStorageFree(storage)
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun connectTimeout(fd: RawSocket, addr: SockAddr, timeout: kotlin.time.Duration): Result<Unit> {
    setNonblocking(fd, true)
    val res = connect(fd, addr)
    setNonblocking(fd, false)
    if (res.isSuccess) {
        return Result.success(Unit)
    }
    val timeoutMs = timeout.inWholeMilliseconds.coerceIn(1, Int.MAX_VALUE.toLong()).toInt()
    val pollRes = socket2PollConnect(fd.fd, timeoutMs)
    return when {
        pollRes == 0 -> {
            Result.failure(IOException("Connection timed out"))
        }

        pollRes < 0 -> {
            if (pollRes == -2) {
                val err = getsockoptInt(fd, SOL_SOCKET_VAL, SO_ERROR_VAL).getOrNull() ?: 0
                if (err != 0) {
                    Result.failure(IOException("Connection error: $err"))
                } else {
                    Result.failure(IOException("Connection error"))
                }
            } else {
                val errnoVal = socket2GetErrno()
                Result.failure(IOException("poll() failed: ${libcStrerror(errnoVal) ?: "errno=$errnoVal"}"))
            }
        }

        else -> {
            Result.success(Unit)
        }
    }
}

/**
 * Tests for the existence of a socket file at [path].
 * Returns true if socket file exists, false otherwise.
 */
@OptIn(ExperimentalForeignApi::class)
internal fun socketExists(path: String): Boolean =
    libcAccess(path, 0) == 0

@OptIn(ExperimentalForeignApi::class)
internal fun listen(fd: RawSocket, backlog: Int): Result<Unit> {
    val result = libcListen(fd.fd, backlog)
    return if (result == -1) {
        val errnoVal = socket2GetErrno()
        val errMsg = libcStrerror(errnoVal) ?: "Unknown error (errno=$errnoVal)"
        Result.failure(IOException("listen() failed: $errMsg"))
    } else {
        Result.success(Unit)
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun accept(fd: RawSocket): Result<Pair<RawSocket, SockAddr>> =
    memScoped {
        val storage = socket2AddrStorageNew() ?: return Result.failure(IOException("Failed to allocate address storage"))
        try {
            val addrLen = alloc<UIntVar>()
            addrLen.value = 128u
            val newFd = socket2Accept(fd.fd, storage, addrLen.ptr)
            if (newFd == -1) {
                val errnoVal = socket2GetErrno()
                val errMsg = libcStrerror(errnoVal) ?: "Unknown error (errno=$errnoVal)"
                Result.failure(IOException("accept() failed: $errMsg"))
            } else {
                val addr = extractSockAddr(storage, addrLen.value)
                Result.success(Pair(RawSocket(newFd), addr))
            }
        } finally {
            socket2AddrStorageFree(storage)
        }
    }

@OptIn(ExperimentalForeignApi::class)
internal fun getsockname(fd: RawSocket): Result<SockAddr> =
    memScoped {
        val storage = socket2AddrStorageNew() ?: return Result.failure(IOException("Failed to allocate address storage"))
        try {
            val addrLen = alloc<UIntVar>()
            addrLen.value = 128u
            val res = socket2Getsockname(fd.fd, storage, addrLen.ptr)
            if (res == -1) {
                val errnoVal = socket2GetErrno()
                val errMsg = libcStrerror(errnoVal) ?: "Unknown error (errno=$errnoVal)"
                Result.failure(IOException("getsockname() failed: $errMsg"))
            } else {
                Result.success(extractSockAddr(storage, addrLen.value))
            }
        } finally {
            socket2AddrStorageFree(storage)
        }
    }

@OptIn(ExperimentalForeignApi::class)
internal fun getpeername(fd: RawSocket): Result<SockAddr> =
    memScoped {
        val storage = socket2AddrStorageNew() ?: return Result.failure(IOException("Failed to allocate address storage"))
        try {
            val addrLen = alloc<UIntVar>()
            addrLen.value = 128u
            val res = socket2Getpeername(fd.fd, storage, addrLen.ptr)
            if (res == -1) {
                val errnoVal = socket2GetErrno()
                val errMsg = libcStrerror(errnoVal) ?: "Unknown error (errno=$errnoVal)"
                Result.failure(IOException("getpeername() failed: $errMsg"))
            } else {
                Result.success(extractSockAddr(storage, addrLen.value))
            }
        } finally {
            socket2AddrStorageFree(storage)
        }
    }

@OptIn(ExperimentalForeignApi::class)
internal fun socketpair(family: Int, type: Int, protocol: Int): Result<Pair<RawSocket, RawSocket>> =
    memScoped {
        val sv = allocArray<IntVar>(2)
        val res = socket2Socketpair(family, type, protocol, sv)
        if (res == -1) {
            val errnoVal = socket2GetErrno()
            val errMsg = libcStrerror(errnoVal) ?: "Unknown error (errno=$errnoVal)"
            Result.failure(IOException("socketpair() failed: $errMsg"))
        } else {
            Result.success(Pair(RawSocket(sv[0]), RawSocket(sv[1])))
        }
    }

@OptIn(ExperimentalForeignApi::class)
internal fun tryClone(fd: RawSocket): Result<RawSocket> {
    val newFd = socket2TryClone(fd.fd)
    return if (newFd == -1) {
        val errnoVal = socket2GetErrno()
        val errMsg = libcStrerror(errnoVal) ?: "Unknown error (errno=$errnoVal)"
        Result.failure(IOException("tryClone() failed: $errMsg"))
    } else {
        Result.success(RawSocket(newFd))
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun setNonblocking(fd: RawSocket, nonblocking: Boolean): Result<Unit> {
    val res = socket2SetNonblocking(fd.fd, if (nonblocking) 1 else 0)
    return if (res == -1) {
        val errnoVal = socket2GetErrno()
        val errMsg = libcStrerror(errnoVal) ?: "Unknown error (errno=$errnoVal)"
        Result.failure(IOException("setNonblocking() failed: $errMsg"))
    } else {
        Result.success(Unit)
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun nonblocking(fd: RawSocket): Result<Boolean> {
    val res = socket2GetNonblocking(fd.fd)
    return if (res == -1) {
        val errnoVal = socket2GetErrno()
        val errMsg = libcStrerror(errnoVal) ?: "Unknown error (errno=$errnoVal)"
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
        val res = socket2Getsockopt(fd.fd, level, optname, optval.ptr, optlen.ptr)
        if (res == -1) {
            val errnoVal = socket2GetErrno()
            val errMsg = libcStrerror(errnoVal) ?: "Unknown error (errno=$errnoVal)"
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
        val res = socket2Setsockopt(fd.fd, level, optname, optval.ptr, sizeOf<IntVar>().toUInt())
        if (res == -1) {
            val errnoVal = socket2GetErrno()
            val errMsg = libcStrerror(errnoVal) ?: "Unknown error (errno=$errnoVal)"
            Result.failure(IOException("setsockopt() failed: $errMsg"))
        } else {
            Result.success(Unit)
        }
    }

@OptIn(ExperimentalForeignApi::class)
internal fun shutdown(fd: RawSocket, how: Int): Result<Unit> {
    val result = libcShutdown(fd.fd, how)
    return if (result == -1) {
        val errnoVal = socket2GetErrno()
        val errMsg = libcStrerror(errnoVal) ?: "Unknown error (errno=$errnoVal)"
        Result.failure(IOException("shutdown() failed: $errMsg"))
    } else {
        Result.success(Unit)
    }
}

@OptIn(ExperimentalForeignApi::class, ExperimentalUnsignedTypes::class)
internal fun recv(fd: RawSocket, buffer: ByteArray, flags: Int): Result<Int> =
    buffer.usePinned { pinned ->
        val bytesRead = socket2Recv(fd.fd, pinned.addressOf(0), buffer.size.toULong(), flags).toInt()
        if (bytesRead == -1) {
            val errnoVal = socket2GetErrno()
            val errMsg = libcStrerror(errnoVal) ?: "Unknown error (errno=$errnoVal)"
            Result.failure(IOException("recv() failed: $errMsg"))
        } else {
            Result.success(bytesRead)
        }
    }

@OptIn(ExperimentalForeignApi::class, ExperimentalUnsignedTypes::class)
internal fun recvfrom(fd: RawSocket, buffer: ByteArray, flags: Int): Result<Pair<Int, SockAddr>> =
    memScoped {
        val storage = socket2AddrStorageNew() ?: return Result.failure(IOException("Failed to allocate address storage"))
        try {
            val addrLen = alloc<UIntVar>()
            addrLen.value = 128u
            buffer.usePinned { pinned ->
                val bytesRead = socket2Recvfrom(fd.fd, pinned.addressOf(0), buffer.size.toULong(), flags, storage, addrLen.ptr).toInt()
                if (bytesRead == -1) {
                    val errnoVal = socket2GetErrno()
                    val errMsg = libcStrerror(errnoVal) ?: "Unknown error (errno=$errnoVal)"
                    Result.failure(IOException("recvfrom() failed: $errMsg"))
                } else {
                    val addr = extractSockAddr(storage, addrLen.value)
                    Result.success(Pair(bytesRead, addr))
                }
            }
        } finally {
            socket2AddrStorageFree(storage)
        }
    }

@OptIn(ExperimentalForeignApi::class, ExperimentalUnsignedTypes::class)
internal fun send(fd: RawSocket, buffer: ByteArray, flags: Int): Result<Int> =
    buffer.usePinned { pinned ->
        val bytesSent = socket2Send(fd.fd, pinned.addressOf(0), buffer.size.toULong(), flags).toInt()
        if (bytesSent == -1) {
            val errnoVal = socket2GetErrno()
            val errMsg = libcStrerror(errnoVal) ?: "Unknown error (errno=$errnoVal)"
            Result.failure(IOException("send() failed: $errMsg"))
        } else {
            Result.success(bytesSent)
        }
    }

@OptIn(ExperimentalForeignApi::class, ExperimentalUnsignedTypes::class)
internal fun sendto(fd: RawSocket, buffer: ByteArray, flags: Int, addr: SockAddr): Result<Int> {
    val storage = socket2AddrStorageNew() ?: return Result.failure(IOException("Failed to allocate address storage"))
    try {
        socket2AddrStorageSetFamily(storage, addr.storage.ssFamily)
        val padding = addr.storage.padding
        val paddingLen = padding.size.toUInt()
        padding.usePinned { pinned ->
            val paddingPtr = pinned.addressOf(0).reinterpret<UByteVar>()
            socket2AddrStorageSetPadding(storage, paddingPtr, paddingLen)
        }
        return buffer.usePinned { pinned ->
            val bytesSent = socket2Sendto(fd.fd, pinned.addressOf(0), buffer.size.toULong(), flags, storage, addr.len()).toInt()
            if (bytesSent == -1) {
                val errnoVal = socket2GetErrno()
                val errMsg = libcStrerror(errnoVal) ?: "Unknown error (errno=$errnoVal)"
                Result.failure(IOException("sendto() failed: $errMsg"))
            } else {
                Result.success(bytesSent)
            }
        }
    } finally {
        socket2AddrStorageFree(storage)
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun close(fd: RawSocket): Result<Unit> {
    val result = libcClose(fd.fd)
    return if (result == -1) {
        val errnoVal = socket2GetErrno()
        val errMsg = libcStrerror(errnoVal) ?: "Unknown error (errno=$errnoVal)"
        Result.failure(IOException("close() failed: $errMsg"))
    } else {
        Result.success(Unit)
    }
}

class IOException(
    message: String,
) : Exception(message)

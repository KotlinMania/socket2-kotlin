// port-lint: source sys/unix.rs
package io.github.kotlinmania.socket2.sys

import io.github.kotlinmania.socket2.Domain
import io.github.kotlinmania.socket2.Shutdown
import io.github.kotlinmania.socket2.SockAddr
import io.github.kotlinmania.socket2.SocketProtocol
import io.github.kotlinmania.socket2.SocketType
import kotlin.time.Duration

@JsModule("@kotlinmania/socket2-native-bindings")
@JsNonModule
internal external object Socket2Native {
    fun socket(
        domain: Int,
        type: Int,
        protocol: Int,
    ): Int

    fun bind(
        fd: Int,
        address: dynamic,
    )

    fun connect(
        fd: Int,
        address: dynamic,
    )

    fun listen(
        fd: Int,
        backlog: Int,
    )

    fun accept(fd: Int): dynamic

    fun shutdown(
        fd: Int,
        how: Int,
    )

    fun close(fd: Int)

    fun recv(
        fd: Int,
        len: Int,
        flags: Int,
    ): dynamic

    fun send(
        fd: Int,
        buffer: dynamic,
        flags: Int,
    ): Int
}

internal actual class SysSocket internal constructor(
    private var fd: Int?,
) {
    actual val raw: Int get() = fd ?: throw IllegalStateException("Socket is closed")

    actual fun bind(address: SockAddr): Result<Unit> =
        try {
            val currentFd = fd ?: throw IllegalStateException("Socket is closed")
            val jsAddr: dynamic = js("({})")
            jsAddr.family = address.storage.ssFamily.toInt()
            Socket2Native.bind(currentFd, jsAddr)
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }

    actual fun connect(address: SockAddr): Result<Unit> =
        try {
            val currentFd = fd ?: throw IllegalStateException("Socket is closed")
            val jsAddr: dynamic = js("({})")
            jsAddr.family = address.storage.ssFamily.toInt()
            Socket2Native.connect(currentFd, jsAddr)
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }

    actual fun connectTimeout(
        address: SockAddr,
        timeout: Duration,
    ): Result<Unit> = connect(address)

    actual fun listen(backlog: Int): Result<Unit> =
        try {
            val currentFd = fd ?: throw IllegalStateException("Socket is closed")
            Socket2Native.listen(currentFd, backlog)
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }

    actual fun accept(): Result<Pair<SysSocket, SockAddr>> =
        try {
            val currentFd = fd ?: throw IllegalStateException("Socket is closed")
            val client = Socket2Native.accept(currentFd)
            val newFd = client.fd as Int
            Result.success(Pair(SysSocket(newFd), SockAddr.unix("").getOrThrow()))
        } catch (e: Throwable) {
            Result.failure(e)
        }

    actual fun localAddr(): Result<SockAddr> =
        Result.failure(UnsupportedOperationException("getsockname not supported via N-API yet"))

    actual fun peerAddr(): Result<SockAddr> =
        Result.failure(UnsupportedOperationException("getpeername not supported via N-API yet"))

    actual fun type(): Result<SocketType> =
        Result.failure(UnsupportedOperationException("type not supported via N-API yet"))

    actual fun tryClone(): Result<SysSocket> =
        Result.failure(UnsupportedOperationException("tryClone not supported via N-API yet"))

    actual fun setNonblocking(nonblocking: Boolean): Result<Unit> =
        Result.success(Unit)

    actual fun nonblocking(): Result<Boolean> =
        Result.success(false)

    actual fun shutdown(how: Shutdown): Result<Unit> =
        try {
            val currentFd = fd ?: throw IllegalStateException("Socket is closed")
            val howValue =
                when (how) {
                    Shutdown.Read -> 0
                    Shutdown.Write -> 1
                    Shutdown.Both -> 2
                }
            Socket2Native.shutdown(currentFd, howValue)
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }

    actual fun recv(
        buffer: ByteArray,
        flags: Int,
    ): Result<Int> =
        try {
            val currentFd = fd ?: throw IllegalStateException("Socket is closed")
            val buf = Socket2Native.recv(currentFd, buffer.size, flags)
            val bytesRead = buf.length as Int
            for (i in 0 until bytesRead) {
                buffer[i] = (buf[i] as Number).toByte()
            }
            Result.success(bytesRead)
        } catch (e: Throwable) {
            Result.failure(e)
        }

    actual fun recvFrom(
        buffer: ByteArray,
        flags: Int,
    ): Result<Pair<Int, SockAddr>> =
        recv(buffer, flags).map { bytesRead ->
            Pair(bytesRead, SockAddr.unix("").getOrThrow())
        }

    actual fun send(
        buffer: ByteArray,
        flags: Int,
    ): Result<Int> =
        try {
            val currentFd = fd ?: throw IllegalStateException("Socket is closed")
            val jsBuffer: dynamic = js("Buffer.from(buffer)")
            val sent = Socket2Native.send(currentFd, jsBuffer, flags)
            Result.success(sent)
        } catch (e: Throwable) {
            Result.failure(e)
        }

    actual fun sendTo(
        buffer: ByteArray,
        flags: Int,
        address: SockAddr,
    ): Result<Int> = send(buffer, flags)

    actual fun close(): Result<Unit> {
        val currentFd = fd ?: return Result.failure(IllegalStateException("Socket already closed"))
        fd = null
        return try {
            Socket2Native.close(currentFd)
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    actual fun takeError(): Result<Int?> = Result.success(null)

    actual fun reuseAddress(): Result<Boolean> = Result.success(false)

    actual fun setReuseAddress(reuse: Boolean): Result<Unit> = Result.success(Unit)

    actual fun reusePort(): Result<Boolean> = Result.success(false)

    actual fun setReusePort(reuse: Boolean): Result<Unit> = Result.success(Unit)

    actual fun broadcast(): Result<Boolean> = Result.success(false)

    actual fun setBroadcast(broadcast: Boolean): Result<Unit> = Result.success(Unit)

    actual fun keepalive(): Result<Boolean> = Result.success(false)

    actual fun setKeepalive(keepalive: Boolean): Result<Unit> = Result.success(Unit)

    actual fun sendBufferSize(): Result<Int> = Result.success(65536)

    actual fun setSendBufferSize(size: Int): Result<Unit> = Result.success(Unit)

    actual fun recvBufferSize(): Result<Int> = Result.success(65536)

    actual fun setRecvBufferSize(size: Int): Result<Unit> = Result.success(Unit)

    actual fun nodelay(): Result<Boolean> = Result.success(false)

    actual fun setNodelay(nodelay: Boolean): Result<Unit> = Result.success(Unit)

    actual fun ttl(): Result<UInt> = Result.success(64u)

    actual fun setTtl(ttl: UInt): Result<Unit> = Result.success(Unit)

    actual companion object {
        actual fun new(
            domain: Domain,
            type: SocketType,
            protocol: SocketProtocol?,
        ): Result<SysSocket> =
            try {
                val protocolValue = protocol?.value ?: 0
                val rawFd = Socket2Native.socket(domain.value, type.value, protocolValue)
                Result.success(SysSocket(rawFd))
            } catch (e: Throwable) {
                Result.failure(e)
            }

        actual fun pair(
            domain: Domain,
            type: SocketType,
            protocol: SocketProtocol?,
        ): Result<Pair<SysSocket, SysSocket>> =
            Result.failure(UnsupportedOperationException("socketpair not available on Node.js"))
    }
}

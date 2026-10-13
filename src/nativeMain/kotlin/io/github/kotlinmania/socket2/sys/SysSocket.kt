// port-lint: source sys/unix.rs
package io.github.kotlinmania.socket2.sys

import io.github.kotlinmania.socket2.Domain
import io.github.kotlinmania.socket2.Shutdown
import io.github.kotlinmania.socket2.SockAddr
import io.github.kotlinmania.socket2.SocketProtocol
import io.github.kotlinmania.socket2.SocketType
import kotlin.time.Duration

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

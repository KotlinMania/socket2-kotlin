// port-lint: source sys/unix.rs
package io.github.kotlinmania.socket2.sys

import io.github.kotlinmania.socket2.Domain
import io.github.kotlinmania.socket2.Shutdown
import io.github.kotlinmania.socket2.SockAddr
import io.github.kotlinmania.socket2.SocketProtocol
import io.github.kotlinmania.socket2.SocketType
import kotlin.time.Duration

internal actual class SysSocket internal constructor(
    actual val raw: Int,
) {
    actual fun bind(address: SockAddr): Result<Unit> =
        Result.failure(UnsupportedOperationException("SysSocket bind requires WASI Preview 2 (wasi:sockets/tcp.start-bind)"))

    actual fun connect(address: SockAddr): Result<Unit> =
        Result.failure(UnsupportedOperationException("SysSocket connect requires WASI Preview 2 (wasi:sockets/tcp.start-connect)"))

    actual fun connectTimeout(
        address: SockAddr,
        timeout: Duration,
    ): Result<Unit> =
        Result.failure(UnsupportedOperationException("SysSocket connectTimeout requires WASI Preview 2 (wasi:sockets/tcp)"))

    actual fun listen(backlog: Int): Result<Unit> =
        Result.failure(UnsupportedOperationException("SysSocket listen requires WASI Preview 2 (wasi:sockets/tcp.start-listen)"))

    actual fun accept(): Result<Pair<SysSocket, SockAddr>> =
        Result.failure(UnsupportedOperationException("SysSocket accept requires WASI Preview 2 (wasi:sockets/tcp.accept)"))

    actual fun localAddr(): Result<SockAddr> =
        Result.failure(UnsupportedOperationException("SysSocket localAddr requires WASI Preview 2 (wasi:sockets/tcp.local-address)"))

    actual fun peerAddr(): Result<SockAddr> =
        Result.failure(UnsupportedOperationException("SysSocket peerAddr requires WASI Preview 2 (wasi:sockets/tcp.remote-address)"))

    actual fun type(): Result<SocketType> =
        Result.failure(UnsupportedOperationException("SysSocket type requires WASI Preview 2 (wasi:sockets)"))

    actual fun tryClone(): Result<SysSocket> =
        Result.failure(UnsupportedOperationException("SysSocket clone not supported on WASI (wasi:sockets resources are uncloneable)"))

    actual fun setNonblocking(nonblocking: Boolean): Result<Unit> {
        // All WASI Preview 2 sockets are non-blocking by definition per specification.
        return if (nonblocking) {
            Result.success(Unit)
        } else {
            Result.failure(UnsupportedOperationException("Blocking sockets are unsupported in WASI Preview 2"))
        }
    }

    actual fun nonblocking(): Result<Boolean> = Result.success(true)

    actual fun shutdown(how: Shutdown): Result<Unit> {
        val wasiShutdown =
            when (how) {
                Shutdown.Read -> WasiShutdownType.Receive
                Shutdown.Write -> WasiShutdownType.Send
                Shutdown.Both -> WasiShutdownType.Both
            }
        return Result.failure(
            UnsupportedOperationException("SysSocket shutdown($wasiShutdown) requires WASI Preview 2 (wasi:sockets/tcp.shutdown)"),
        )
    }

    actual fun recv(
        buffer: ByteArray,
        flags: Int,
    ): Result<Int> =
        Result.failure(UnsupportedOperationException("SysSocket recv requires WASI Preview 2 (wasi:io/streams.read)"))

    actual fun recvFrom(
        buffer: ByteArray,
        flags: Int,
    ): Result<Pair<Int, SockAddr>> =
        Result.failure(UnsupportedOperationException("SysSocket recvFrom requires WASI Preview 2 (wasi:sockets/udp.stream)"))

    actual fun send(
        buffer: ByteArray,
        flags: Int,
    ): Result<Int> =
        Result.failure(UnsupportedOperationException("SysSocket send requires WASI Preview 2 (wasi:io/streams.write)"))

    actual fun sendTo(
        buffer: ByteArray,
        flags: Int,
        address: SockAddr,
    ): Result<Int> =
        Result.failure(UnsupportedOperationException("SysSocket sendTo requires WASI Preview 2 (wasi:sockets/udp.stream)"))

    actual fun close(): Result<Unit> =
        Result.failure(UnsupportedOperationException("SysSocket close requires WASI Preview 2 (wasi:sockets drop)"))

    actual fun takeError(): Result<Int?> =
        Result.failure(UnsupportedOperationException("SysSocket takeError not applicable in WASI Preview 2"))

    actual fun reuseAddress(): Result<Boolean> =
        Result.success(true) // Always enabled in WASI Preview 2 tcp-socket::bind

    actual fun setReuseAddress(reuse: Boolean): Result<Unit> =
        Result.success(Unit)

    actual fun reusePort(): Result<Boolean> =
        Result.failure(UnsupportedOperationException("SO_REUSEPORT not supported on WASI"))

    actual fun setReusePort(reuse: Boolean): Result<Unit> =
        Result.failure(UnsupportedOperationException("SO_REUSEPORT not supported on WASI"))

    actual fun broadcast(): Result<Boolean> =
        Result.failure(UnsupportedOperationException("SO_BROADCAST not supported on WASI"))

    actual fun setBroadcast(broadcast: Boolean): Result<Unit> =
        Result.failure(UnsupportedOperationException("SO_BROADCAST not supported on WASI"))

    actual fun keepalive(): Result<Boolean> =
        Result.failure(UnsupportedOperationException("SysSocket keepalive requires WASI Preview 2 (wasi:sockets/tcp.keep-alive-enabled)"))

    actual fun setKeepalive(keepalive: Boolean): Result<Unit> =
        Result.failure(UnsupportedOperationException("SysSocket setKeepalive requires WASI Preview 2 (wasi:sockets/tcp.set-keep-alive-enabled)"))

    actual fun sendBufferSize(): Result<Int> =
        Result.failure(UnsupportedOperationException("SysSocket sendBufferSize requires WASI Preview 2 (wasi:sockets/tcp.send-buffer-size)"))

    actual fun setSendBufferSize(size: Int): Result<Unit> =
        Result.failure(UnsupportedOperationException("SysSocket setSendBufferSize requires WASI Preview 2 (wasi:sockets/tcp.set-send-buffer-size)"))

    actual fun recvBufferSize(): Result<Int> =
        Result.failure(UnsupportedOperationException("SysSocket recvBufferSize requires WASI Preview 2 (wasi:sockets/tcp.receive-buffer-size)"))

    actual fun setRecvBufferSize(size: Int): Result<Unit> =
        Result.failure(UnsupportedOperationException("SysSocket setRecvBufferSize requires WASI Preview 2 (wasi:sockets/tcp.set-receive-buffer-size)"))

    actual fun nodelay(): Result<Boolean> =
        Result.failure(UnsupportedOperationException("TCP_NODELAY not exposed in WASI Preview 2"))

    actual fun setNodelay(nodelay: Boolean): Result<Unit> =
        Result.failure(UnsupportedOperationException("TCP_NODELAY not exposed in WASI Preview 2"))

    actual fun ttl(): Result<UInt> =
        Result.failure(UnsupportedOperationException("SysSocket ttl requires WASI Preview 2 (wasi:sockets/tcp.hop-limit)"))

    actual fun setTtl(ttl: UInt): Result<Unit> =
        Result.failure(UnsupportedOperationException("SysSocket setTtl requires WASI Preview 2 (wasi:sockets/tcp.set-hop-limit)"))

    actual companion object {
        actual fun new(
            domain: Domain,
            type: SocketType,
            protocol: SocketProtocol?,
        ): Result<SysSocket> {
            val family =
                when (domain) {
                    Domain.IPV4 -> WasiIpAddressFamily.IPv4
                    Domain.IPV6 -> WasiIpAddressFamily.IPv6
                    else -> return Result.failure(UnsupportedOperationException("Domain $domain not supported on WASI"))
                }
            return Result.failure(
                UnsupportedOperationException(
                    "WASI socket creation for $family and type $type requires WASI Preview 2 (wasi:sockets/${if (type == SocketType.STREAM) "tcp" else "udp"}-create-socket)",
                ),
            )
        }

        actual fun pair(
            domain: Domain,
            type: SocketType,
            protocol: SocketProtocol?,
        ): Result<Pair<SysSocket, SysSocket>> =
            Result.failure(UnsupportedOperationException("Socketpair not supported on WASI"))
    }
}

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
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun connect(address: SockAddr): Result<Unit> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun connectTimeout(
        address: SockAddr,
        timeout: Duration,
    ): Result<Unit> = Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun listen(backlog: Int): Result<Unit> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun accept(): Result<Pair<SysSocket, SockAddr>> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun localAddr(): Result<SockAddr> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun peerAddr(): Result<SockAddr> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun type(): Result<SocketType> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun tryClone(): Result<SysSocket> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun setNonblocking(nonblocking: Boolean): Result<Unit> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun nonblocking(): Result<Boolean> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun shutdown(how: Shutdown): Result<Unit> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun recv(
        buffer: ByteArray,
        flags: Int,
    ): Result<Int> = Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun recvFrom(
        buffer: ByteArray,
        flags: Int,
    ): Result<Pair<Int, SockAddr>> = Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun send(
        buffer: ByteArray,
        flags: Int,
    ): Result<Int> = Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun sendTo(
        buffer: ByteArray,
        flags: Int,
        address: SockAddr,
    ): Result<Int> = Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun close(): Result<Unit> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun takeError(): Result<Int?> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun reuseAddress(): Result<Boolean> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun setReuseAddress(reuse: Boolean): Result<Unit> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun reusePort(): Result<Boolean> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun setReusePort(reuse: Boolean): Result<Unit> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun broadcast(): Result<Boolean> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun setBroadcast(broadcast: Boolean): Result<Unit> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun keepalive(): Result<Boolean> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun setKeepalive(keepalive: Boolean): Result<Unit> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun sendBufferSize(): Result<Int> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun setSendBufferSize(size: Int): Result<Unit> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun recvBufferSize(): Result<Int> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun setRecvBufferSize(size: Int): Result<Unit> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun nodelay(): Result<Boolean> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun setNodelay(nodelay: Boolean): Result<Unit> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun ttl(): Result<UInt> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual fun setTtl(ttl: UInt): Result<Unit> =
        Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

    actual companion object {
        actual fun fromRaw(raw: Int): SysSocket = SysSocket(raw)

        actual fun new(
            domain: Domain,
            type: SocketType,
            protocol: SocketProtocol?,
        ): Result<SysSocket> = Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))

        actual fun pair(
            domain: Domain,
            type: SocketType,
            protocol: SocketProtocol?,
        ): Result<Pair<SysSocket, SysSocket>> =
            Result.failure(UnsupportedOperationException("SysSocket not available on WASM JS"))
    }
}

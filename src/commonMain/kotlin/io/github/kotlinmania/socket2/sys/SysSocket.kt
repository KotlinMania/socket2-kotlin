// port-lint: source sys/unix.rs
package io.github.kotlinmania.socket2.sys

import io.github.kotlinmania.socket2.Domain
import io.github.kotlinmania.socket2.Shutdown
import io.github.kotlinmania.socket2.SockAddr
import io.github.kotlinmania.socket2.SocketProtocol
import io.github.kotlinmania.socket2.SocketType
import kotlin.time.Duration

/**
 * Socket type alias corresponding to Rust's pub(crate) type Socket.
 */
internal typealias Socket = SysSocket

/**
 * Platform-specific low-level socket handle and syscall bridge.
 */
internal expect class SysSocket {
    val raw: Int

    fun bind(address: SockAddr): Result<Unit>

    fun connect(address: SockAddr): Result<Unit>

    fun connectTimeout(
        address: SockAddr,
        timeout: Duration,
    ): Result<Unit>

    fun listen(backlog: Int): Result<Unit>

    fun accept(): Result<Pair<SysSocket, SockAddr>>

    fun localAddr(): Result<SockAddr>

    fun peerAddr(): Result<SockAddr>

    fun type(): Result<SocketType>

    fun tryClone(): Result<SysSocket>

    fun setNonblocking(nonblocking: Boolean): Result<Unit>

    fun nonblocking(): Result<Boolean>

    fun shutdown(how: Shutdown): Result<Unit>

    fun recv(
        buffer: ByteArray,
        flags: Int,
    ): Result<Int>

    fun recvFrom(
        buffer: ByteArray,
        flags: Int,
    ): Result<Pair<Int, SockAddr>>

    fun send(
        buffer: ByteArray,
        flags: Int,
    ): Result<Int>

    fun sendTo(
        buffer: ByteArray,
        flags: Int,
        address: SockAddr,
    ): Result<Int>

    fun close(): Result<Unit>

    fun takeError(): Result<Int?>

    fun reuseAddress(): Result<Boolean>

    fun setReuseAddress(reuse: Boolean): Result<Unit>

    fun reusePort(): Result<Boolean>

    fun setReusePort(reuse: Boolean): Result<Unit>

    fun broadcast(): Result<Boolean>

    fun setBroadcast(broadcast: Boolean): Result<Unit>

    fun keepalive(): Result<Boolean>

    fun setKeepalive(keepalive: Boolean): Result<Unit>

    fun sendBufferSize(): Result<Int>

    fun setSendBufferSize(size: Int): Result<Unit>

    fun recvBufferSize(): Result<Int>

    fun setRecvBufferSize(size: Int): Result<Unit>

    fun nodelay(): Result<Boolean>

    fun setNodelay(nodelay: Boolean): Result<Unit>

    fun ttl(): Result<UInt>

    fun setTtl(ttl: UInt): Result<Unit>

    companion object {
        fun fromRaw(raw: Int): SysSocket

        fun new(
            domain: Domain,
            type: SocketType,
            protocol: SocketProtocol?,
        ): Result<SysSocket>

        fun pair(
            domain: Domain,
            type: SocketType,
            protocol: SocketProtocol?,
        ): Result<Pair<SysSocket, SysSocket>>
    }
}

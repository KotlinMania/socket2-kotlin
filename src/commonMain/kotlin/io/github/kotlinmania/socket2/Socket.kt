// port-lint: source socket.rs
package io.github.kotlinmania.socket2

import io.github.kotlinmania.socket2.sys.SysSocket
import kotlin.time.Duration

/**
 * An owned wrapper around a system socket.
 *
 * This provides cross-platform access to low-level socket APIs without
 * using Java networking or runtime abstractions.
 */
public class Socket internal constructor(
    private var inner: SysSocket?,
) {
    public companion object {
        /**
         * Creates a new socket and configures standard flags.
         */
        public fun new(
            domain: Domain,
            type: SocketType,
            protocol: SocketProtocol? = null,
        ): Result<Socket> = SysSocket.new(domain, type, protocol).map { Socket(it) }

        /**
         * Creates a new socket without setting any flags.
         */
        public fun newRaw(
            domain: Domain,
            type: SocketType,
            protocol: SocketProtocol? = null,
        ): Result<Socket> = SysSocket.new(domain, type, protocol).map { Socket(it) }

        /**
         * Creates a pair of interconnected sockets.
         */
        public fun pair(
            domain: Domain,
            type: SocketType,
            protocol: SocketProtocol? = null,
        ): Result<Pair<Socket, Socket>> =
            SysSocket.pair(domain, type, protocol).map { (a, b) ->
                Pair(Socket(a), Socket(b))
            }

        /**
         * Creates a pair of interconnected sockets without setting any flags.
         */
        public fun pairRaw(
            domain: Domain,
            type: SocketType,
            protocol: SocketProtocol? = null,
        ): Result<Pair<Socket, Socket>> =
            SysSocket.pair(domain, type, protocol).map { (a, b) ->
                Pair(Socket(a), Socket(b))
            }
    }

    private fun inner(): SysSocket = inner ?: throw IllegalStateException("Socket is closed")

    public fun asRaw(): Int = inner().raw

    public fun bind(address: SockAddr): Result<Unit> = inner().bind(address)

    public fun connect(address: SockAddr): Result<Unit> = inner().connect(address)

    public fun connectTimeout(
        address: SockAddr,
        timeout: Duration,
    ): Result<Unit> = inner().connectTimeout(address, timeout)

    public fun listen(backlog: Int): Result<Unit> = inner().listen(backlog)

    public fun accept(): Result<Pair<Socket, SockAddr>> =
        inner().accept().map { (client, addr) ->
            Pair(Socket(client), addr)
        }

    public fun localAddr(): Result<SockAddr> = inner().localAddr()

    public fun peerAddr(): Result<SockAddr> = inner().peerAddr()

    public fun type(): Result<SocketType> = inner().type()

    public fun tryClone(): Result<Socket> = inner().tryClone().map { Socket(it) }

    public fun setNonblocking(nonblocking: Boolean): Result<Unit> = inner().setNonblocking(nonblocking)

    public fun nonblocking(): Result<Boolean> = inner().nonblocking()

    public fun shutdown(how: Shutdown): Result<Unit> = inner().shutdown(how)

    public fun recv(
        buffer: ByteArray,
        flags: Int = 0,
    ): Result<Int> = inner().recv(buffer, flags)

    public fun recvFrom(
        buffer: ByteArray,
        flags: Int = 0,
    ): Result<Pair<Int, SockAddr>> = inner().recvFrom(buffer, flags)

    public fun send(
        buffer: ByteArray,
        flags: Int = 0,
    ): Result<Int> = inner().send(buffer, flags)

    public fun sendTo(
        buffer: ByteArray,
        flags: Int = 0,
        address: SockAddr,
    ): Result<Int> = inner().sendTo(buffer, flags, address)

    public fun close(): Result<Unit> {
        val s = inner ?: return Result.failure(IllegalStateException("Socket already closed"))
        inner = null
        return s.close()
    }

    public fun takeError(): Result<Int?> = inner().takeError()

    public fun reuseAddress(): Result<Boolean> = inner().reuseAddress()

    public fun setReuseAddress(reuse: Boolean): Result<Unit> = inner().setReuseAddress(reuse)

    public fun reusePort(): Result<Boolean> = inner().reusePort()

    public fun setReusePort(reuse: Boolean): Result<Unit> = inner().setReusePort(reuse)

    public fun broadcast(): Result<Boolean> = inner().broadcast()

    public fun setBroadcast(broadcast: Boolean): Result<Unit> = inner().setBroadcast(broadcast)

    public fun keepalive(): Result<Boolean> = inner().keepalive()

    public fun setKeepalive(keepalive: Boolean): Result<Unit> = inner().setKeepalive(keepalive)

    public fun sendBufferSize(): Result<Int> = inner().sendBufferSize()

    public fun setSendBufferSize(size: Int): Result<Unit> = inner().setSendBufferSize(size)

    public fun recvBufferSize(): Result<Int> = inner().recvBufferSize()

    public fun setRecvBufferSize(size: Int): Result<Unit> = inner().setRecvBufferSize(size)

    public fun nodelay(): Result<Boolean> = inner().nodelay()

    public fun setNodelay(nodelay: Boolean): Result<Unit> = inner().setNodelay(nodelay)

    public fun ttl(): Result<UInt> = inner().ttl()

    public fun setTtl(ttl: UInt): Result<Unit> = inner().setTtl(ttl)

    override fun toString(): String = "Socket(inner=$inner)"
}

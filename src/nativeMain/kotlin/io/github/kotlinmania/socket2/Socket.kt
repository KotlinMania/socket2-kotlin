// port-lint: source socket.rs
package io.github.kotlinmania.socket2

import io.github.kotlinmania.socket2.sys.IPPROTO_IP_VAL
import io.github.kotlinmania.socket2.sys.IPPROTO_TCP_VAL
import io.github.kotlinmania.socket2.sys.IP_TTL_VAL
import io.github.kotlinmania.socket2.sys.RawSocket
import io.github.kotlinmania.socket2.sys.SHUT_RD
import io.github.kotlinmania.socket2.sys.SHUT_RDWR
import io.github.kotlinmania.socket2.sys.SHUT_WR
import io.github.kotlinmania.socket2.sys.SOL_SOCKET_VAL
import io.github.kotlinmania.socket2.sys.SO_BROADCAST_VAL
import io.github.kotlinmania.socket2.sys.SO_ERROR_VAL
import io.github.kotlinmania.socket2.sys.SO_KEEPALIVE_VAL
import io.github.kotlinmania.socket2.sys.SO_RCVBUF_VAL
import io.github.kotlinmania.socket2.sys.SO_REUSEADDR_VAL
import io.github.kotlinmania.socket2.sys.SO_REUSEPORT_VAL
import io.github.kotlinmania.socket2.sys.SO_SNDBUF_VAL
import io.github.kotlinmania.socket2.sys.SO_TYPE_VAL
import io.github.kotlinmania.socket2.sys.TCP_NODELAY_VAL
import io.github.kotlinmania.socket2.sys.accept
import io.github.kotlinmania.socket2.sys.bind
import io.github.kotlinmania.socket2.sys.close
import io.github.kotlinmania.socket2.sys.connect
import io.github.kotlinmania.socket2.sys.connectTimeout
import io.github.kotlinmania.socket2.sys.getpeername
import io.github.kotlinmania.socket2.sys.getsockname
import io.github.kotlinmania.socket2.sys.getsockoptInt
import io.github.kotlinmania.socket2.sys.listen
import io.github.kotlinmania.socket2.sys.nonblocking
import io.github.kotlinmania.socket2.sys.recv
import io.github.kotlinmania.socket2.sys.recvfrom
import io.github.kotlinmania.socket2.sys.send
import io.github.kotlinmania.socket2.sys.sendto
import io.github.kotlinmania.socket2.sys.setNonblocking
import io.github.kotlinmania.socket2.sys.setsockoptInt
import io.github.kotlinmania.socket2.sys.shutdown
import io.github.kotlinmania.socket2.sys.socket
import io.github.kotlinmania.socket2.sys.socketpair
import io.github.kotlinmania.socket2.sys.tryClone

/**
 * Native (Kotlin/Native) implementation of Socket.
 *
 * Delegates to platform-specific syscall wrappers via the `sys` package.
 */
public actual class Socket internal constructor(
    private var fd: RawSocket?,
) {
    public actual companion object {
        public actual fun new(domain: Domain, type: SocketType, protocol: SocketProtocol?): Result<Socket> =
            newRaw(domain, type, protocol).mapCatching { socket ->
                socket
            }

        public actual fun newRaw(domain: Domain, type: SocketType, protocol: SocketProtocol?): Result<Socket> {
            val protocolValue = protocol?.value ?: 0
            return socket(domain.value, type.value, protocolValue).map { rawFd ->
                Socket(rawFd)
            }
        }

        public actual fun pair(domain: Domain, type: SocketType, protocol: SocketProtocol?): Result<Pair<Socket, Socket>> =
            pairRaw(domain, type, protocol)

        public actual fun pairRaw(domain: Domain, type: SocketType, protocol: SocketProtocol?): Result<Pair<Socket, Socket>> {
            val protocolValue = protocol?.value ?: 0
            return socketpair(domain.value, type.value, protocolValue).map { (a, b) ->
                Pair(Socket(a), Socket(b))
            }
        }
    }

    internal fun asRaw(): RawSocket = fd ?: throw IllegalStateException("Socket has been closed")

    public actual fun bind(address: SockAddr): Result<Unit> = bind(asRaw(), address)

    public actual fun connect(address: SockAddr): Result<Unit> = connect(asRaw(), address)

    public actual fun connectTimeout(address: SockAddr, timeout: kotlin.time.Duration): Result<Unit> =
        connectTimeout(asRaw(), address, timeout)

    public actual fun listen(backlog: Int): Result<Unit> = listen(asRaw(), backlog)

    public actual fun accept(): Result<Pair<Socket, SockAddr>> =
        accept(asRaw()).map { (newFd, addr) ->
            Pair(Socket(newFd), addr)
        }

    public actual fun localAddr(): Result<SockAddr> = getsockname(asRaw())

    public actual fun peerAddr(): Result<SockAddr> = getpeername(asRaw())

    public actual fun type(): Result<SocketType> =
        getsockoptInt(asRaw(), SOL_SOCKET_VAL, SO_TYPE_VAL).map { SocketType(it) }

    public actual fun tryClone(): Result<Socket> =
        tryClone(asRaw()).map { Socket(it) }

    public actual fun setNonblocking(nonblocking: Boolean): Result<Unit> =
        setNonblocking(asRaw(), nonblocking)

    public actual fun nonblocking(): Result<Boolean> =
        nonblocking(asRaw())

    public actual fun shutdown(how: Shutdown): Result<Unit> {
        val howValue =
            when (how) {
                Shutdown.Read -> SHUT_RD
                Shutdown.Write -> SHUT_WR
                Shutdown.Both -> SHUT_RDWR
            }
        return shutdown(asRaw(), howValue)
    }

    public actual fun recv(buffer: ByteArray, flags: Int): Result<Int> = recv(asRaw(), buffer, flags)

    public actual fun recvFrom(buffer: ByteArray, flags: Int): Result<Pair<Int, SockAddr>> =
        recvfrom(asRaw(), buffer, flags)

    public actual fun send(buffer: ByteArray, flags: Int): Result<Int> = send(asRaw(), buffer, flags)

    public actual fun sendTo(buffer: ByteArray, flags: Int, address: SockAddr): Result<Int> =
        sendto(asRaw(), buffer, flags, address)

    public actual fun close(): Result<Unit> {
        val currentFd = fd
        return if (currentFd == null) {
            Result.failure(IllegalStateException("Socket already closed"))
        } else {
            fd = null
            close(currentFd)
        }
    }

    public actual fun takeError(): Result<Int?> =
        getsockoptInt(asRaw(), SOL_SOCKET_VAL, SO_ERROR_VAL).map { if (it == 0) null else it }

    public actual fun reuseAddress(): Result<Boolean> =
        getsockoptInt(asRaw(), SOL_SOCKET_VAL, SO_REUSEADDR_VAL).map { it != 0 }

    public actual fun setReuseAddress(reuse: Boolean): Result<Unit> =
        setsockoptInt(asRaw(), SOL_SOCKET_VAL, SO_REUSEADDR_VAL, if (reuse) 1 else 0)

    public actual fun reusePort(): Result<Boolean> {
        val opt = SO_REUSEPORT_VAL
        if (opt == -1) return Result.failure(UnsupportedOperationException("SO_REUSEPORT not supported on this platform"))
        return getsockoptInt(asRaw(), SOL_SOCKET_VAL, opt).map { it != 0 }
    }

    public actual fun setReusePort(reuse: Boolean): Result<Unit> {
        val opt = SO_REUSEPORT_VAL
        if (opt == -1) return Result.failure(UnsupportedOperationException("SO_REUSEPORT not supported on this platform"))
        return setsockoptInt(asRaw(), SOL_SOCKET_VAL, opt, if (reuse) 1 else 0)
    }

    public actual fun broadcast(): Result<Boolean> =
        getsockoptInt(asRaw(), SOL_SOCKET_VAL, SO_BROADCAST_VAL).map { it != 0 }

    public actual fun setBroadcast(broadcast: Boolean): Result<Unit> =
        setsockoptInt(asRaw(), SOL_SOCKET_VAL, SO_BROADCAST_VAL, if (broadcast) 1 else 0)

    public actual fun keepalive(): Result<Boolean> =
        getsockoptInt(asRaw(), SOL_SOCKET_VAL, SO_KEEPALIVE_VAL).map { it != 0 }

    public actual fun setKeepalive(keepalive: Boolean): Result<Unit> =
        setsockoptInt(asRaw(), SOL_SOCKET_VAL, SO_KEEPALIVE_VAL, if (keepalive) 1 else 0)

    public actual fun sendBufferSize(): Result<Int> =
        getsockoptInt(asRaw(), SOL_SOCKET_VAL, SO_SNDBUF_VAL)

    public actual fun setSendBufferSize(size: Int): Result<Unit> =
        setsockoptInt(asRaw(), SOL_SOCKET_VAL, SO_SNDBUF_VAL, size)

    public actual fun recvBufferSize(): Result<Int> =
        getsockoptInt(asRaw(), SOL_SOCKET_VAL, SO_RCVBUF_VAL)

    public actual fun setRecvBufferSize(size: Int): Result<Unit> =
        setsockoptInt(asRaw(), SOL_SOCKET_VAL, SO_RCVBUF_VAL, size)

    public actual fun nodelay(): Result<Boolean> =
        getsockoptInt(asRaw(), IPPROTO_TCP_VAL, TCP_NODELAY_VAL).map { it != 0 }

    public actual fun setNodelay(nodelay: Boolean): Result<Unit> =
        setsockoptInt(asRaw(), IPPROTO_TCP_VAL, TCP_NODELAY_VAL, if (nodelay) 1 else 0)

    public actual fun ttl(): Result<UInt> =
        getsockoptInt(asRaw(), IPPROTO_IP_VAL, IP_TTL_VAL).map { it.toUInt() }

    public actual fun setTtl(ttl: UInt): Result<Unit> =
        setsockoptInt(asRaw(), IPPROTO_IP_VAL, IP_TTL_VAL, ttl.toInt())

    override fun toString(): String = "Socket(fd=$fd)"
}

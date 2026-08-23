// port-lint: source socket.rs
package io.github.kotlinmania.socket2

import io.github.kotlinmania.socket2.sys.RawSocket
import io.github.kotlinmania.socket2.sys.SHUT_RD
import io.github.kotlinmania.socket2.sys.SHUT_RDWR
import io.github.kotlinmania.socket2.sys.SHUT_WR
import io.github.kotlinmania.socket2.sys.accept
import io.github.kotlinmania.socket2.sys.bind
import io.github.kotlinmania.socket2.sys.close
import io.github.kotlinmania.socket2.sys.connect
import io.github.kotlinmania.socket2.sys.listen
import io.github.kotlinmania.socket2.sys.recv
import io.github.kotlinmania.socket2.sys.send
import io.github.kotlinmania.socket2.sys.shutdown
import io.github.kotlinmania.socket2.sys.socket

/**
 * Native (Kotlin/Native) implementation of Socket.
 *
 * Delegates to platform-specific syscall wrappers via the `sys` package.
 */
public actual class Socket internal constructor(
    private var fd: RawSocket?,
) {
    public actual companion object {
        public actual fun new(domain: Domain, type: Type, protocol: Protocol?): Result<Socket> =
            newRaw(domain, type, protocol).mapCatching { socket ->
                socket
            }

        public actual fun newRaw(domain: Domain, type: Type, protocol: Protocol?): Result<Socket> {
            val protocolValue = protocol?.value ?: 0
            return socket(domain.value, type.value, protocolValue).map { rawFd ->
                Socket(rawFd)
            }
        }
    }

    internal fun asRaw(): RawSocket = fd ?: throw IllegalStateException("Socket has been closed")

    public actual fun bind(address: SockAddr): Result<Unit> = bind(asRaw(), address)

    public actual fun connect(address: SockAddr): Result<Unit> = connect(asRaw(), address)

    public actual fun listen(backlog: Int): Result<Unit> = listen(asRaw(), backlog)

    public actual fun accept(): Result<Pair<Socket, SockAddr>> =
        accept(asRaw()).map { (newFd, addr) ->
            Pair(Socket(newFd), addr)
        }

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

    public actual fun send(buffer: ByteArray, flags: Int): Result<Int> = send(asRaw(), buffer, flags)

    public actual fun close(): Result<Unit> {
        val currentFd = fd
        return if (currentFd == null) {
            Result.failure(IllegalStateException("Socket already closed"))
        } else {
            fd = null
            close(currentFd)
        }
    }

    override fun toString(): String = "Socket(fd=$fd)"
}

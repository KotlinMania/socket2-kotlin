// port-lint: source socket.rs
package io.github.kotlinmania.socket2

import io.github.kotlinmania.socket2.sys.*

/**
 * Native (Kotlin/Native) implementation of Socket.
 *
 * Delegates to platform-specific syscall wrappers via the `sys` package.
 */
public actual class Socket internal constructor(
    private var fd: RawSocket?
) {
    public actual companion object {
        public actual fun new(domain: Domain, type: Type, protocol: Protocol?): Result<Socket> {
            return newRaw(domain, type, protocol).mapCatching { socket ->
                socket
            }
        }

        public actual fun newRaw(domain: Domain, type: Type, protocol: Protocol?): Result<Socket> {
            val protocolValue = protocol?.value ?: 0
            return socket(domain.value, type.value, protocolValue).map { rawFd ->
                Socket(rawFd)
            }
        }
    }

    internal fun asRaw(): RawSocket {
        return fd ?: throw IllegalStateException("Socket has been closed")
    }

    public actual fun bind(address: SockAddr): Result<Unit> {
        return bind(asRaw(), address)
    }

    public actual fun connect(address: SockAddr): Result<Unit> {
        return connect(asRaw(), address)
    }

    public actual fun listen(backlog: Int): Result<Unit> {
        return listen(asRaw(), backlog)
    }

    public actual fun accept(): Result<Pair<Socket, SockAddr>> {
        return accept(asRaw()).map { (newFd, addr) ->
            Pair(Socket(newFd), addr)
        }
    }

    public actual fun shutdown(how: Shutdown): Result<Unit> {
        val howValue = when (how) {
            Shutdown.Read -> SHUT_RD
            Shutdown.Write -> SHUT_WR
            Shutdown.Both -> SHUT_RDWR
        }
        return shutdown(asRaw(), howValue)
    }

    public actual fun recv(buffer: ByteArray, flags: Int): Result<Int> {
        return recv(asRaw(), buffer, flags)
    }

    public actual fun send(buffer: ByteArray, flags: Int): Result<Int> {
        return send(asRaw(), buffer, flags)
    }

    public actual fun close(): Result<Unit> {
        val currentFd = fd
        return if (currentFd == null) {
            Result.failure(IllegalStateException("Socket already closed"))
        } else {
            fd = null
            close(currentFd)
        }
    }

    override fun toString(): String {
        return "Socket(fd=$fd)"
    }
}
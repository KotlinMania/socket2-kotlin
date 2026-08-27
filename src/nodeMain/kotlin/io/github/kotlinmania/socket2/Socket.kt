// port-lint: source socket2/src/socket.rs
package io.github.kotlinmania.socket2

/**
 * Node.js N-API native bindings for direct POSIX socket syscalls.
 *
 * These bindings provide the same level of control as our Kotlin/Native implementation:
 * - Direct C library calls (no Node.js net module abstractions)
 * - Same syscalls: socket(2), bind(2), connect(2), etc.
 * - Full control over socket behavior
 */
@JsModule("@socket2-kotlin/native-bindings")
@JsNonModule
external object Socket2Native {
    // Core socket functions - direct syscall bindings
    fun socket(domain: Int, type: Int, protocol: Int): Int
    fun bind(fd: Int, address: dynamic)
    fun connect(fd: Int, address: dynamic)
    fun listen(fd: Int, backlog: Int)
    fun accept(fd: Int): dynamic  // Returns { fd: number, address: object }
    fun shutdown(fd: Int, how: Int)
    fun close(fd: Int)
    fun recv(fd: Int, len: Int, flags: Int): dynamic  // Returns Buffer
    fun send(fd: Int, buffer: dynamic, flags: Int): Int

    // Socket constants
    val AF_INET: Int
    val AF_INET6: Int
    val SOCK_STREAM: Int
    val SOCK_DGRAM: Int
    val SHUT_RD: Int
    val SHUT_WR: Int
    val SHUT_RDWR: Int
}

/**
 * Node.js implementation of Socket using N-API native bindings.
 *
 * This implementation uses direct POSIX socket syscalls via N-API,
 * providing the same level of control as our Kotlin/Native implementation.
 */
public actual class Socket internal constructor(
    private var fd: Int?
) {
    public actual companion object {
        /**
         * Creates a new socket using direct socket(2) syscall via N-API.
         *
         * See commonMain/Socket.kt for full documentation.
         */
        public actual fun new(domain: Domain, type: SocketType, protocol: SocketProtocol?): Result<Socket> {
            return try {
                val protocolValue = protocol?.value ?: 0
                val socketFd = Socket2Native.socket(domain.value, type.value, protocolValue)
                Result.success(Socket(socketFd))
            } catch (e: Throwable) {
                Result.failure(IOException("socket() failed: ${e.message}"))
            }
        }

        /**
         * Creates a new socket without additional configuration.
         *
         * See commonMain/Socket.kt for full documentation.
         */
        public actual fun newRaw(domain: Domain, type: SocketType, protocol: SocketProtocol?): Result<Socket> {
            return new(domain, type, protocol)
        }
    }

    /**
     * Binds this socket to the specified address.
     *
     * Direct binding to bind(2) syscall via N-API.
     * See commonMain/Socket.kt for full documentation.
     */
    public actual fun bind(address: SockAddr): Result<Unit> {
        return try {
            val socketFd = fd ?: return Result.failure(IllegalStateException("Socket already closed"))
            val socketAddr = address.asSocket() ?: return Result.failure(IOException("Invalid address"))

            val addrObj = when (socketAddr) {
                is Socket2SocketAddress.V4 -> js("{}")
                    .apply {
                        this.asDynamic().family = Socket2Native.AF_INET
                        this.asDynamic().port = socketAddr.port
                        this.asDynamic().addr = socketAddr.address
                    }
                is Socket2SocketAddress.V6 -> js("{}")
                    .apply {
                        this.asDynamic().family = Socket2Native.AF_INET6
                        this.asDynamic().port = socketAddr.port
                        this.asDynamic().addr = socketAddr.address
                    }
            }

            Socket2Native.bind(socketFd, addrObj)
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(IOException("bind() failed: ${e.message}"))
        }
    }

    /**
     * Initiate a connection on this socket to the specified address.
     *
     * Direct binding to connect(2) syscall via N-API.
     * See commonMain/Socket.kt for full documentation.
     */
    public actual fun connect(address: SockAddr): Result<Unit> {
        return try {
            val socketFd = fd ?: return Result.failure(IllegalStateException("Socket already closed"))
            val socketAddr = address.asSocket() ?: return Result.failure(IOException("Invalid address"))

            val addrObj = when (socketAddr) {
                is Socket2SocketAddress.V4 -> js("{}")
                    .apply {
                        this.asDynamic().family = Socket2Native.AF_INET
                        this.asDynamic().port = socketAddr.port
                        this.asDynamic().addr = socketAddr.address
                    }
                is Socket2SocketAddress.V6 -> js("{}")
                    .apply {
                        this.asDynamic().family = Socket2Native.AF_INET6
                        this.asDynamic().port = socketAddr.port
                        this.asDynamic().addr = socketAddr.address
                    }
            }

            Socket2Native.connect(socketFd, addrObj)
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(IOException("connect() failed: ${e.message}"))
        }
    }

    /**
     * Marks the socket as ready to accept incoming connection requests.
     *
     * Direct binding to listen(2) syscall via N-API.
     * See commonMain/Socket.kt for full documentation.
     */
    public actual fun listen(backlog: Int): Result<Unit> {
        return try {
            val socketFd = fd ?: return Result.failure(IllegalStateException("Socket already closed"))
            Socket2Native.listen(socketFd, backlog)
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(IOException("listen() failed: ${e.message}"))
        }
    }

    /**
     * Accept a new incoming connection from this listener.
     *
     * Direct binding to accept(2) syscall via N-API.
     * See commonMain/Socket.kt for full documentation.
     */
    public actual fun accept(): Result<Pair<Socket, SockAddr>> {
        return try {
            val socketFd = fd ?: return Result.failure(IllegalStateException("Socket already closed"))
            val result = Socket2Native.accept(socketFd)
            val newFd = result.asDynamic().fd as Int

            // TODO: Properly parse address from result.address
            val storage = SockaddrStorage(
                ssFamily = 2u.toUShort(),
                padding = ByteArray(126)
            )
            val addr = SockAddr.new(SockAddrStorage(storage), 16u)

            Result.success(Pair(Socket(newFd), addr))
        } catch (e: Throwable) {
            Result.failure(IOException("accept() failed: ${e.message}"))
        }
    }

    /**
     * Shuts down the read, write, or both halves of this connection.
     *
     * Direct binding to shutdown(2) syscall via N-API.
     * See commonMain/Socket.kt for full documentation.
     */
    public actual fun shutdown(how: Shutdown): Result<Unit> {
        return try {
            val socketFd = fd ?: return Result.failure(IllegalStateException("Socket already closed"))
            val howValue = when (how) {
                Shutdown.Read -> Socket2Native.SHUT_RD
                Shutdown.Write -> Socket2Native.SHUT_WR
                Shutdown.Both -> Socket2Native.SHUT_RDWR
            }
            Socket2Native.shutdown(socketFd, howValue)
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(IOException("shutdown() failed: ${e.message}"))
        }
    }

    /**
     * Receives data on the socket from the remote address to which it is connected.
     *
     * Direct binding to recv(2) syscall via N-API.
     * See commonMain/Socket.kt for full documentation.
     */
    public actual fun recv(buffer: ByteArray, flags: Int): Result<Int> {
        return try {
            val socketFd = fd ?: return Result.failure(IllegalStateException("Socket already closed"))
            val recvBuffer = Socket2Native.recv(socketFd, buffer.size, flags)

            // Copy received data into the provided buffer
            val jsArray = js("Array.from(recvBuffer)")
            val receivedBytes = jsArray.asDynamic().length as Int
            for (i in 0 until receivedBytes) {
                buffer[i] = jsArray.asDynamic()[i] as Byte
            }

            Result.success(receivedBytes)
        } catch (e: Throwable) {
            Result.failure(IOException("recv() failed: ${e.message}"))
        }
    }

    /**
     * Sends data on the socket to a connected peer.
     *
     * Direct binding to send(2) syscall via N-API.
     * See commonMain/Socket.kt for full documentation.
     */
    public actual fun send(buffer: ByteArray, flags: Int): Result<Int> {
        return try {
            val socketFd = fd ?: return Result.failure(IllegalStateException("Socket already closed"))

            // Convert ByteArray to Node.js Buffer
            val jsBuffer = js("Buffer.from(buffer)")
            val bytesSent = Socket2Native.send(socketFd, jsBuffer, flags)

            Result.success(bytesSent)
        } catch (e: Throwable) {
            Result.failure(IOException("send() failed: ${e.message}"))
        }
    }

    /**
     * Closes this socket.
     *
     * Direct binding to close(2) syscall via N-API.
     * See commonMain/Socket.kt for full documentation.
     */
    public actual fun close(): Result<Unit> {
        val currentFd = fd
        return if (currentFd == null) {
            Result.failure(IllegalStateException("Socket already closed"))
        } else {
            try {
                fd = null
                Socket2Native.close(currentFd)
                Result.success(Unit)
            } catch (e: Throwable) {
                Result.failure(IOException("close() failed: ${e.message}"))
            }
        }
    }

    override fun toString(): String {
        return "Socket(fd=$fd)"
    }
}

/**
 * Exception thrown when a socket operation fails.
 */
class IOException(message: String) : Exception(message)

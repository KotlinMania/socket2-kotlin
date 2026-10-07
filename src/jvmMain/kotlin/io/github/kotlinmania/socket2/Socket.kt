// port-lint: source socket.rs
package io.github.kotlinmania.socket2

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.nio.channels.SocketChannel

/**
 * JVM implementation of Socket using Java NIO SocketChannel and DatagramChannel.
 *
 * This implementation wraps Java's NIO channels which themselves call native socket APIs
 * on the underlying platform.
 */
public actual class Socket internal constructor(
    private var streamChannel: SocketChannel? = null,
    private var datagramChannel: DatagramChannel? = null,
) {
    public actual companion object {
        /**
         * Creates a new socket using Java NIO SocketChannel or DatagramChannel.
         *
         * See commonMain/Socket.kt for full documentation.
         */
        public actual fun new(
            domain: Domain,
            type: SocketType,
            protocol: SocketProtocol?,
        ): Result<Socket> =
            try {
                when (type) {
                    SocketType.STREAM -> {
                        val channel = SocketChannel.open()
                        channel.configureBlocking(true)
                        Result.success(Socket(streamChannel = channel))
                    }
                    SocketType.DGRAM -> {
                        val channel = DatagramChannel.open()
                        channel.configureBlocking(true)
                        Result.success(Socket(datagramChannel = channel))
                    }
                    else -> {
                        Result.failure(IOException("Unsupported socket type on JVM: $type"))
                    }
                }
            } catch (e: Exception) {
                Result.failure(IOException("socket() failed: ${e.message}"))
            }

        /**
         * Creates a new socket without additional configuration.
         *
         * See commonMain/Socket.kt for full documentation.
         */
        public actual fun newRaw(
            domain: Domain,
            type: SocketType,
            protocol: SocketProtocol?,
        ): Result<Socket> =
            try {
                when (type) {
                    SocketType.DGRAM -> {
                        val channel = DatagramChannel.open()
                        Result.success(Socket(datagramChannel = channel))
                    }
                    else -> {
                        val channel = SocketChannel.open()
                        Result.success(Socket(streamChannel = channel))
                    }
                }
            } catch (e: Exception) {
                Result.failure(IOException("socket() failed: ${e.message}"))
            }
    }

    /**
     * Binds this socket to the specified address.
     *
     * See commonMain/Socket.kt for full documentation.
     */
    public actual fun bind(address: SockAddr): Result<Unit> {
        return try {
            val socketAddr = address.asSocket() ?: return Result.failure(IOException("Invalid address for JVM socket"))

            val inetAddr =
                when (socketAddr) {
                    is Socket2SocketAddress.V4 -> {
                        InetSocketAddress(socketAddr.address, socketAddr.port)
                    }
                    is Socket2SocketAddress.V6 -> {
                        InetSocketAddress(socketAddr.address, socketAddr.port)
                    }
                }

            val sc = streamChannel
            val dc = datagramChannel
            when {
                sc != null -> {
                    sc.socket().bind(inetAddr)
                    Result.success(Unit)
                }
                dc != null -> {
                    dc.socket().bind(inetAddr)
                    Result.success(Unit)
                }
                else -> Result.failure(IllegalStateException("Socket already closed"))
            }
        } catch (e: Exception) {
            Result.failure(IOException("bind() failed: ${e.message}"))
        }
    }

    /**
     * Initiate a connection on this socket to the specified address.
     *
     * See commonMain/Socket.kt for full documentation.
     */
    public actual fun connect(address: SockAddr): Result<Unit> {
        return try {
            val socketAddr = address.asSocket() ?: return Result.failure(IOException("Invalid address for JVM socket"))

            val inetAddr =
                when (socketAddr) {
                    is Socket2SocketAddress.V4 -> {
                        InetSocketAddress(socketAddr.address, socketAddr.port)
                    }
                    is Socket2SocketAddress.V6 -> {
                        InetSocketAddress(socketAddr.address, socketAddr.port)
                    }
                }

            val sc = streamChannel
            val dc = datagramChannel
            when {
                sc != null -> {
                    sc.connect(inetAddr)
                    Result.success(Unit)
                }
                dc != null -> {
                    dc.connect(inetAddr)
                    Result.success(Unit)
                }
                else -> Result.failure(IllegalStateException("Socket already closed"))
            }
        } catch (e: Exception) {
            Result.failure(IOException("connect() failed: ${e.message}"))
        }
    }

    /**
     * Marks the socket as ready to accept incoming connection requests.
     *
     * Note: For JVM, this requires converting to a ServerSocketChannel.
     * See commonMain/Socket.kt for full documentation.
     */
    public actual fun listen(backlog: Int): Result<Unit> =
        Result.failure(IOException("listen() not yet implemented for JVM - requires ServerSocketChannel refactoring"))

    /**
     * Accept a new incoming connection from this listener.
     *
     * See commonMain/Socket.kt for full documentation.
     */
    public actual fun accept(): Result<Pair<Socket, SockAddr>> =
        Result.failure(IOException("accept() not yet implemented for JVM - requires ServerSocketChannel refactoring"))

    /**
     * Shuts down the read, write, or both halves of this connection.
     *
     * See commonMain/Socket.kt for full documentation.
     */
    public actual fun shutdown(how: Shutdown): Result<Unit> =
        try {
            val sc = streamChannel
            if (sc != null) {
                val socket = sc.socket()
                when (how) {
                    Shutdown.Read -> socket.shutdownInput()
                    Shutdown.Write -> socket.shutdownOutput()
                    Shutdown.Both -> {
                        socket.shutdownInput()
                        socket.shutdownOutput()
                    }
                }
                Result.success(Unit)
            } else if (datagramChannel != null) {
                // Datagram socket does not support shutdown, treat as success
                Result.success(Unit)
            } else {
                Result.failure(IllegalStateException("Socket already closed"))
            }
        } catch (e: Exception) {
            Result.failure(IOException("shutdown() failed: ${e.message}"))
        }

    /**
     * Receives data on the socket from the remote address to which it is connected.
     *
     * See commonMain/Socket.kt for full documentation.
     */
    public actual fun recv(buffer: ByteArray, flags: Int): Result<Int> {
        return try {
            val byteBuffer = ByteBuffer.wrap(buffer)
            val sc = streamChannel
            val dc = datagramChannel
            val bytesRead =
                when {
                    sc != null -> sc.read(byteBuffer)
                    dc != null -> dc.read(byteBuffer)
                    else -> return Result.failure(IllegalStateException("Socket already closed"))
                }

            if (bytesRead == -1) {
                Result.success(0) // EOF
            } else {
                Result.success(bytesRead)
            }
        } catch (e: Exception) {
            Result.failure(IOException("recv() failed: ${e.message}"))
        }
    }

    /**
     * Sends data on the socket to a connected peer.
     *
     * See commonMain/Socket.kt for full documentation.
     */
    public actual fun send(buffer: ByteArray, flags: Int): Result<Int> {
        return try {
            val byteBuffer = ByteBuffer.wrap(buffer)
            val sc = streamChannel
            val dc = datagramChannel
            val bytesSent =
                when {
                    sc != null -> sc.write(byteBuffer)
                    dc != null -> dc.write(byteBuffer)
                    else -> return Result.failure(IllegalStateException("Socket already closed"))
                }
            Result.success(bytesSent)
        } catch (e: Exception) {
            Result.failure(IOException("send() failed: ${e.message}"))
        }
    }

    /**
     * Closes this socket.
     *
     * See commonMain/Socket.kt for full documentation.
     */
    public actual fun close(): Result<Unit> {
        val sc = streamChannel
        val dc = datagramChannel
        return if (sc == null && dc == null) {
            Result.failure(IllegalStateException("Socket already closed"))
        } else {
            try {
                streamChannel = null
                datagramChannel = null
                sc?.close()
                dc?.close()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(IOException("close() failed: ${e.message}"))
            }
        }
    }

    override fun toString(): String = "Socket(stream=$streamChannel, datagram=$datagramChannel)"
}

/**
 * Exception thrown when a socket operation fails.
 */
class IOException(
    message: String,
) : Exception(message)

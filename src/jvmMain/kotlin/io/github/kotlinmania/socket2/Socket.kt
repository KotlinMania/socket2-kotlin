// port-lint: source socket.rs
package io.github.kotlinmania.socket2

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

/**
 * JVM implementation of Socket using Java NIO SocketChannel.
 *
 * This implementation wraps Java's SocketChannel which itself uses JNI
 * to call native socket APIs on the underlying platform.
 */
public actual class Socket internal constructor(
    private var channel: SocketChannel?,
) {
    public actual companion object {
        /**
         * Creates a new socket using Java NIO SocketChannel.
         *
         * See commonMain/Socket.kt for full documentation.
         */
        public actual fun new(domain: Domain, type: SocketType, protocol: SocketProtocol?): Result<Socket> {
            return try {
                val channel = SocketChannel.open()
                // Configure socket based on type
                when (type) {
                    SocketType.STREAM -> {
                        // TCP socket - default for SocketChannel
                        channel.configureBlocking(true)
                    }
                    SocketType.DGRAM -> {
                        // UDP not directly supported by SocketChannel
                        // Would need DatagramChannel instead
                        return Result.failure(IOException("DGRAM type requires DatagramChannel, not yet implemented"))
                    }
                    else -> {
                        return Result.failure(IOException("Unsupported socket type on JVM: $type"))
                    }
                }
                Result.success(Socket(channel))
            } catch (e: Exception) {
                Result.failure(IOException("socket() failed: ${e.message}"))
            }
        }

        /**
         * Creates a new socket without additional configuration.
         *
         * See commonMain/Socket.kt for full documentation.
         */
        public actual fun newRaw(domain: Domain, type: SocketType, protocol: SocketProtocol?): Result<Socket> =
            try {
                val channel = SocketChannel.open()
                Result.success(Socket(channel))
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
            val ch = channel ?: return Result.failure(IllegalStateException("Socket already closed"))
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

            ch.socket().bind(inetAddr)
            Result.success(Unit)
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
            val ch = channel ?: return Result.failure(IllegalStateException("Socket already closed"))
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

            ch.connect(inetAddr)
            Result.success(Unit)
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
    public actual fun listen(backlog: Int): Result<Unit> = Result.failure(IOException("listen() not yet implemented for JVM - requires ServerSocketChannel refactoring"))

    /**
     * Accept a new incoming connection from this listener.
     *
     * See commonMain/Socket.kt for full documentation.
     */
    public actual fun accept(): Result<Pair<Socket, SockAddr>> = Result.failure(IOException("accept() not yet implemented for JVM - requires ServerSocketChannel refactoring"))

    /**
     * Shuts down the read, write, or both halves of this connection.
     *
     * See commonMain/Socket.kt for full documentation.
     */
    public actual fun shutdown(how: Shutdown): Result<Unit> {
        return try {
            val ch = channel ?: return Result.failure(IllegalStateException("Socket already closed"))
            val socket = ch.socket()

            when (how) {
                Shutdown.Read -> socket.shutdownInput()
                Shutdown.Write -> socket.shutdownOutput()
                Shutdown.Both -> {
                    socket.shutdownInput()
                    socket.shutdownOutput()
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(IOException("shutdown() failed: ${e.message}"))
        }
    }

    /**
     * Receives data on the socket from the remote address to which it is connected.
     *
     * See commonMain/Socket.kt for full documentation.
     */
    public actual fun recv(buffer: ByteArray, flags: Int): Result<Int> {
        return try {
            val ch = channel ?: return Result.failure(IllegalStateException("Socket already closed"))
            val byteBuffer = ByteBuffer.wrap(buffer)
            val bytesRead = ch.read(byteBuffer)

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
            val ch = channel ?: return Result.failure(IllegalStateException("Socket already closed"))
            val byteBuffer = ByteBuffer.wrap(buffer)
            val bytesSent = ch.write(byteBuffer)
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
        val currentChannel = channel
        return if (currentChannel == null) {
            Result.failure(IllegalStateException("Socket already closed"))
        } else {
            try {
                channel = null
                currentChannel.close()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(IOException("close() failed: ${e.message}"))
            }
        }
    }

    override fun toString(): String = "Socket(channel=$channel)"
}

/**
 * Exception thrown when a socket operation fails.
 */
class IOException(
    message: String,
) : Exception(message)

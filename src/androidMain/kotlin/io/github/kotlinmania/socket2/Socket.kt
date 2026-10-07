// port-lint: source socket.rs
package io.github.kotlinmania.socket2

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.nio.channels.SocketChannel

public actual class Socket internal constructor(
    private var streamChannel: SocketChannel? = null,
    private var datagramChannel: DatagramChannel? = null,
) {
    public actual companion object {
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
                        Result.failure(IOException("Unsupported socket type on Android: $type"))
                    }
                }
            } catch (e: Exception) {
                Result.failure(IOException("socket() failed: ${e.message}"))
            }

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

    public actual fun bind(address: SockAddr): Result<Unit> {
        return try {
            val socketAddr = address.asSocket() ?: return Result.failure(IOException("Invalid address"))
            val inetAddr =
                when (socketAddr) {
                    is Socket2SocketAddress.V4 -> InetSocketAddress(socketAddr.address, socketAddr.port)
                    is Socket2SocketAddress.V6 -> InetSocketAddress(socketAddr.address, socketAddr.port)
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

    public actual fun connect(address: SockAddr): Result<Unit> {
        return try {
            val socketAddr = address.asSocket() ?: return Result.failure(IOException("Invalid address"))
            val inetAddr =
                when (socketAddr) {
                    is Socket2SocketAddress.V4 -> InetSocketAddress(socketAddr.address, socketAddr.port)
                    is Socket2SocketAddress.V6 -> InetSocketAddress(socketAddr.address, socketAddr.port)
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

    public actual fun listen(backlog: Int): Result<Unit> =
        Result.failure(IOException("listen() not yet implemented for Android"))

    public actual fun accept(): Result<Pair<Socket, SockAddr>> =
        Result.failure(IOException("accept() not yet implemented for Android"))

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
                Result.success(Unit)
            } else {
                Result.failure(IllegalStateException("Socket already closed"))
            }
        } catch (e: Exception) {
            Result.failure(IOException("shutdown() failed: ${e.message}"))
        }

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
            Result.success(if (bytesRead == -1) 0 else bytesRead)
        } catch (e: Exception) {
            Result.failure(IOException("recv() failed: ${e.message}"))
        }
    }

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

class IOException(
    message: String,
) : Exception(message)

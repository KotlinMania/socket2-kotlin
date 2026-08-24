// port-lint: source socket.rs
package io.github.kotlinmania.socket2

import java.net.InetSocketAddress
import java.nio.channels.SocketChannel

public actual class Socket internal constructor(
    private var channel: SocketChannel?,
) {
    public actual companion object {
        public actual fun new(domain: Domain, type: Type, protocol: Protocol?): Result<Socket> {
            return try {
                val channel = SocketChannel.open()
                when (type) {
                    Type.STREAM -> channel.configureBlocking(true)
                    Type.DGRAM -> return Result.failure(IOException("DGRAM type requires DatagramChannel, not yet implemented"))
                    else -> return Result.failure(IOException("Unsupported socket type on Android: $type"))
                }
                Result.success(Socket(channel))
            } catch (e: Exception) {
                Result.failure(IOException("socket() failed: ${e.message}"))
            }
        }

        public actual fun newRaw(domain: Domain, type: Type, protocol: Protocol?): Result<Socket> =
            try {
                val channel = SocketChannel.open()
                Result.success(Socket(channel))
            } catch (e: Exception) {
                Result.failure(IOException("socket() failed: ${e.message}"))
            }
    }

    public actual fun bind(address: SockAddr): Result<Unit> {
        return try {
            val ch = channel ?: return Result.failure(IllegalStateException("Socket already closed"))
            val socketAddr = address.asSocket() ?: return Result.failure(IOException("Invalid address"))
            val inetAddr =
                when (socketAddr) {
                    is Socket2SocketAddress.V4 -> InetSocketAddress(socketAddr.address, socketAddr.port)
                    is Socket2SocketAddress.V6 -> InetSocketAddress(socketAddr.address, socketAddr.port)
                }
            ch.socket().bind(inetAddr)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(IOException("bind() failed: ${e.message}"))
        }
    }

    public actual fun connect(address: SockAddr): Result<Unit> {
        return try {
            val ch = channel ?: return Result.failure(IllegalStateException("Socket already closed"))
            val socketAddr = address.asSocket() ?: return Result.failure(IOException("Invalid address"))
            val inetAddr =
                when (socketAddr) {
                    is Socket2SocketAddress.V4 -> InetSocketAddress(socketAddr.address, socketAddr.port)
                    is Socket2SocketAddress.V6 -> InetSocketAddress(socketAddr.address, socketAddr.port)
                }
            ch.connect(inetAddr)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(IOException("connect() failed: ${e.message}"))
        }
    }

    public actual fun listen(backlog: Int): Result<Unit> =
        Result.failure(IOException("listen() not yet implemented for Android"))

    public actual fun accept(): Result<Pair<Socket, SockAddr>> =
        Result.failure(IOException("accept() not yet implemented for Android"))

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

    public actual fun recv(buffer: ByteArray, flags: Int): Result<Int> {
        return try {
            val ch = channel ?: return Result.failure(IllegalStateException("Socket already closed"))
            val byteBuffer = java.nio.ByteBuffer.wrap(buffer)
            val bytesRead = ch.read(byteBuffer)
            Result.success(if (bytesRead == -1) 0 else bytesRead)
        } catch (e: Exception) {
            Result.failure(IOException("recv() failed: ${e.message}"))
        }
    }

    public actual fun send(buffer: ByteArray, flags: Int): Result<Int> {
        return try {
            val ch = channel ?: return Result.failure(IllegalStateException("Socket already closed"))
            val byteBuffer = java.nio.ByteBuffer.wrap(buffer)
            Result.success(ch.write(byteBuffer))
        } catch (e: Exception) {
            Result.failure(IOException("send() failed: ${e.message}"))
        }
    }

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

class IOException(
    message: String,
) : Exception(message)

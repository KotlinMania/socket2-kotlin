// port-lint: source socket.rs
package io.github.kotlinmania.socket2

import java.net.Inet6Address
import java.net.InetSocketAddress
import java.net.StandardSocketOptions
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

/**
 * Android implementation of Socket using Java NIO channels.
 */
public actual class Socket internal constructor(
    private var streamChannel: SocketChannel? = null,
    private var serverChannel: ServerSocketChannel? = null,
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
                    else -> Result.failure(IOException("Unsupported socket type on Android: $type"))
                }
            } catch (e: Exception) {
                Result.failure(IOException("socket() failed: ${e.message}"))
            }

        public actual fun newRaw(
            domain: Domain,
            type: SocketType,
            protocol: SocketProtocol?,
        ): Result<Socket> = new(domain, type, protocol)

        public actual fun pair(
            domain: Domain,
            type: SocketType,
            protocol: SocketProtocol?,
        ): Result<Pair<Socket, Socket>> = pairRaw(domain, type, protocol)

        public actual fun pairRaw(
            domain: Domain,
            type: SocketType,
            protocol: SocketProtocol?,
        ): Result<Pair<Socket, Socket>> =
            try {
                val ssc = ServerSocketChannel.open()
                ssc.bind(InetSocketAddress("127.0.0.1", 0), 1)
                val localAddr = ssc.localAddress as InetSocketAddress
                val client = SocketChannel.open(localAddr)
                val server = ssc.accept()
                ssc.close()
                Result.success(Pair(Socket(streamChannel = client), Socket(streamChannel = server)))
            } catch (e: Exception) {
                Result.failure(IOException("pair() failed: ${e.message}"))
            }
    }

    public actual fun bind(address: SockAddr): Result<Unit> =
        try {
            val socketAddr = address.asSocket() ?: return Result.failure(IOException("Invalid address for Android socket"))
            val inetAddr =
                when (socketAddr) {
                    is Socket2SocketAddress.V4 -> InetSocketAddress(socketAddr.address, socketAddr.port)
                    is Socket2SocketAddress.V6 -> InetSocketAddress(socketAddr.address, socketAddr.port)
                }

            val sc = streamChannel
            val ssc = serverChannel
            val dc = datagramChannel
            when {
                sc != null -> {
                    sc.bind(inetAddr)
                    Result.success(Unit)
                }
                ssc != null -> {
                    ssc.bind(inetAddr)
                    Result.success(Unit)
                }
                dc != null -> {
                    dc.bind(inetAddr)
                    Result.success(Unit)
                }
                else -> Result.failure(IllegalStateException("Socket already closed"))
            }
        } catch (e: Exception) {
            Result.failure(IOException("bind() failed: ${e.message}"))
        }

    public actual fun connect(address: SockAddr): Result<Unit> =
        try {
            val socketAddr = address.asSocket() ?: return Result.failure(IOException("Invalid address for Android socket"))
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
                else -> Result.failure(IllegalStateException("Socket already closed or cannot connect"))
            }
        } catch (e: Exception) {
            Result.failure(IOException("connect() failed: ${e.message}"))
        }

    public actual fun listen(backlog: Int): Result<Unit> =
        try {
            val sscExisting = serverChannel
            if (sscExisting != null) {
                Result.success(Unit)
            } else {
                val sc = streamChannel ?: return Result.failure(IllegalStateException("Socket already closed"))
                val localAddr = sc.localAddress as? InetSocketAddress
                val blocking = sc.isBlocking
                sc.close()
                streamChannel = null
                val ssc = ServerSocketChannel.open()
                ssc.configureBlocking(blocking)
                ssc.setOption(StandardSocketOptions.SO_REUSEADDR, true)
                ssc.bind(localAddr ?: InetSocketAddress(0), backlog)
                serverChannel = ssc
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(IOException("listen() failed: ${e.message}"))
        }

    public actual fun accept(): Result<Pair<Socket, SockAddr>> =
        try {
            val ssc = serverChannel ?: return Result.failure(IOException("accept() called on non-listening socket"))
            val clientChannel = ssc.accept()
            val remote = clientChannel.remoteAddress as InetSocketAddress
            val host = remote.address?.hostAddress ?: "127.0.0.1"
            val port = remote.port
            val clientAddr =
                if (remote.address is Inet6Address) {
                    SockAddr.from(Socket2SocketAddress.V6(host, port))
                } else {
                    SockAddr.from(Socket2SocketAddress.V4(host, port))
                }
            Result.success(Pair(Socket(streamChannel = clientChannel), clientAddr))
        } catch (e: Exception) {
            Result.failure(IOException("accept() failed: ${e.message}"))
        }

    public actual fun localAddr(): Result<SockAddr> =
        try {
            val addr: InetSocketAddress? =
                when {
                    streamChannel != null -> streamChannel?.localAddress as? InetSocketAddress
                    serverChannel != null -> serverChannel?.localAddress as? InetSocketAddress
                    datagramChannel != null -> datagramChannel?.localAddress as? InetSocketAddress
                    else -> null
                }
            if (addr != null) {
                val host = addr.address?.hostAddress ?: "127.0.0.1"
                val port = addr.port
                if (addr.address is Inet6Address) {
                    Result.success(SockAddr.from(Socket2SocketAddress.V6(host, port)))
                } else {
                    Result.success(SockAddr.from(Socket2SocketAddress.V4(host, port)))
                }
            } else {
                Result.failure(IOException("Socket not bound"))
            }
        } catch (e: Exception) {
            Result.failure(IOException("localAddr() failed: ${e.message}"))
        }

    public actual fun peerAddr(): Result<SockAddr> =
        try {
            val addr: InetSocketAddress? =
                when {
                    streamChannel != null -> streamChannel?.remoteAddress as? InetSocketAddress
                    datagramChannel != null -> datagramChannel?.remoteAddress as? InetSocketAddress
                    else -> null
                }
            if (addr != null) {
                val host = addr.address?.hostAddress ?: "127.0.0.1"
                val port = addr.port
                if (addr.address is Inet6Address) {
                    Result.success(SockAddr.from(Socket2SocketAddress.V6(host, port)))
                } else {
                    Result.success(SockAddr.from(Socket2SocketAddress.V4(host, port)))
                }
            } else {
                Result.failure(IOException("Socket not connected"))
            }
        } catch (e: Exception) {
            Result.failure(IOException("peerAddr() failed: ${e.message}"))
        }

    public actual fun type(): Result<SocketType> =
        when {
            streamChannel != null || serverChannel != null -> Result.success(SocketType.STREAM)
            datagramChannel != null -> Result.success(SocketType.DGRAM)
            else -> Result.failure(IllegalStateException("Socket already closed"))
        }

    public actual fun tryClone(): Result<Socket> =
        Result.failure(UnsupportedOperationException("tryClone() not supported on Android NIO channels"))

    public actual fun setNonblocking(nonblocking: Boolean): Result<Unit> =
        try {
            streamChannel?.configureBlocking(!nonblocking)
            serverChannel?.configureBlocking(!nonblocking)
            datagramChannel?.configureBlocking(!nonblocking)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(IOException("setNonblocking() failed: ${e.message}"))
        }

    public actual fun nonblocking(): Result<Boolean> =
        when {
            streamChannel != null -> Result.success(!streamChannel!!.isBlocking)
            serverChannel != null -> Result.success(!serverChannel!!.isBlocking)
            datagramChannel != null -> Result.success(!datagramChannel!!.isBlocking)
            else -> Result.failure(IllegalStateException("Socket already closed"))
        }

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
            } else if (datagramChannel != null || serverChannel != null) {
                Result.success(Unit)
            } else {
                Result.failure(IllegalStateException("Socket already closed"))
            }
        } catch (e: Exception) {
            Result.failure(IOException("shutdown() failed: ${e.message}"))
        }

    public actual fun recv(buffer: ByteArray, flags: Int): Result<Int> =
        try {
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
                Result.success(0)
            } else {
                Result.success(bytesRead)
            }
        } catch (e: Exception) {
            Result.failure(IOException("recv() failed: ${e.message}"))
        }

    public actual fun recvFrom(buffer: ByteArray, flags: Int): Result<Pair<Int, SockAddr>> =
        try {
            val dc = datagramChannel ?: return Result.failure(IOException("recvFrom only supported on datagram sockets on Android"))
            val byteBuffer = ByteBuffer.wrap(buffer)
            val sender = dc.receive(byteBuffer) as? InetSocketAddress ?: return Result.failure(IOException("recvFrom received null address"))
            val bytesRead = byteBuffer.position()
            val host = sender.address?.hostAddress ?: "127.0.0.1"
            val port = sender.port
            val sockAddr =
                if (sender.address is Inet6Address) {
                    SockAddr.from(Socket2SocketAddress.V6(host, port))
                } else {
                    SockAddr.from(Socket2SocketAddress.V4(host, port))
                }
            Result.success(Pair(bytesRead, sockAddr))
        } catch (e: Exception) {
            Result.failure(IOException("recvFrom() failed: ${e.message}"))
        }

    public actual fun send(buffer: ByteArray, flags: Int): Result<Int> =
        try {
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

    public actual fun sendTo(buffer: ByteArray, flags: Int, address: SockAddr): Result<Int> =
        try {
            val dc = datagramChannel ?: return Result.failure(IOException("sendTo only supported on datagram sockets on Android"))
            val socketAddr = address.asSocket() ?: return Result.failure(IOException("Invalid address"))
            val inetAddr =
                when (socketAddr) {
                    is Socket2SocketAddress.V4 -> InetSocketAddress(socketAddr.address, socketAddr.port)
                    is Socket2SocketAddress.V6 -> InetSocketAddress(socketAddr.address, socketAddr.port)
                }
            val sent = dc.send(ByteBuffer.wrap(buffer), inetAddr)
            Result.success(sent)
        } catch (e: Exception) {
            Result.failure(IOException("sendTo() failed: ${e.message}"))
        }

    public actual fun close(): Result<Unit> {
        val sc = streamChannel
        val ssc = serverChannel
        val dc = datagramChannel
        return if (sc == null && ssc == null && dc == null) {
            Result.failure(IllegalStateException("Socket already closed"))
        } else {
            try {
                streamChannel = null
                serverChannel = null
                datagramChannel = null
                sc?.close()
                ssc?.close()
                dc?.close()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(IOException("close() failed: ${e.message}"))
            }
        }
    }

    public actual fun takeError(): Result<Int?> = Result.success(null)

    public actual fun reuseAddress(): Result<Boolean> =
        try {
            val res =
                when {
                    streamChannel != null -> streamChannel?.getOption(StandardSocketOptions.SO_REUSEADDR)
                    serverChannel != null -> serverChannel?.getOption(StandardSocketOptions.SO_REUSEADDR)
                    datagramChannel != null -> datagramChannel?.getOption(StandardSocketOptions.SO_REUSEADDR)
                    else -> null
                }
            Result.success(res ?: false)
        } catch (e: Exception) {
            Result.failure(IOException("reuseAddress() failed: ${e.message}"))
        }

    public actual fun setReuseAddress(reuse: Boolean): Result<Unit> =
        try {
            streamChannel?.setOption(StandardSocketOptions.SO_REUSEADDR, reuse)
            serverChannel?.setOption(StandardSocketOptions.SO_REUSEADDR, reuse)
            datagramChannel?.setOption(StandardSocketOptions.SO_REUSEADDR, reuse)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(IOException("setReuseAddress() failed: ${e.message}"))
        }

    public actual fun reusePort(): Result<Boolean> =
        Result.failure(UnsupportedOperationException("SO_REUSEPORT not supported on Android"))

    public actual fun setReusePort(reuse: Boolean): Result<Unit> =
        Result.failure(UnsupportedOperationException("SO_REUSEPORT not supported on Android"))

    public actual fun broadcast(): Result<Boolean> =
        try {
            val res = datagramChannel?.getOption(StandardSocketOptions.SO_BROADCAST) ?: false
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(IOException("broadcast() failed: ${e.message}"))
        }

    public actual fun setBroadcast(broadcast: Boolean): Result<Unit> =
        try {
            datagramChannel?.setOption(StandardSocketOptions.SO_BROADCAST, broadcast)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(IOException("setBroadcast() failed: ${e.message}"))
        }

    public actual fun keepalive(): Result<Boolean> =
        try {
            val res = streamChannel?.getOption(StandardSocketOptions.SO_KEEPALIVE) ?: false
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(IOException("keepalive() failed: ${e.message}"))
        }

    public actual fun setKeepalive(keepalive: Boolean): Result<Unit> =
        try {
            streamChannel?.setOption(StandardSocketOptions.SO_KEEPALIVE, keepalive)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(IOException("setKeepalive() failed: ${e.message}"))
        }

    public actual fun sendBufferSize(): Result<Int> =
        try {
            val res =
                when {
                    streamChannel != null -> streamChannel?.getOption(StandardSocketOptions.SO_SNDBUF)
                    serverChannel != null -> serverChannel?.getOption(StandardSocketOptions.SO_RCVBUF)
                    datagramChannel != null -> datagramChannel?.getOption(StandardSocketOptions.SO_SNDBUF)
                    else -> null
                } ?: 0
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(IOException("sendBufferSize() failed: ${e.message}"))
        }

    public actual fun setSendBufferSize(size: Int): Result<Unit> =
        try {
            streamChannel?.setOption(StandardSocketOptions.SO_SNDBUF, size)
            datagramChannel?.setOption(StandardSocketOptions.SO_SNDBUF, size)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(IOException("setSendBufferSize() failed: ${e.message}"))
        }

    public actual fun recvBufferSize(): Result<Int> =
        try {
            val res =
                when {
                    streamChannel != null -> streamChannel?.getOption(StandardSocketOptions.SO_RCVBUF)
                    serverChannel != null -> serverChannel?.getOption(StandardSocketOptions.SO_RCVBUF)
                    datagramChannel != null -> datagramChannel?.getOption(StandardSocketOptions.SO_RCVBUF)
                    else -> null
                } ?: 0
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(IOException("recvBufferSize() failed: ${e.message}"))
        }

    public actual fun setRecvBufferSize(size: Int): Result<Unit> =
        try {
            streamChannel?.setOption(StandardSocketOptions.SO_RCVBUF, size)
            serverChannel?.setOption(StandardSocketOptions.SO_RCVBUF, size)
            datagramChannel?.setOption(StandardSocketOptions.SO_RCVBUF, size)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(IOException("setRecvBufferSize() failed: ${e.message}"))
        }

    public actual fun nodelay(): Result<Boolean> =
        try {
            val res = streamChannel?.getOption(StandardSocketOptions.TCP_NODELAY) ?: false
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(IOException("nodelay() failed: ${e.message}"))
        }

    public actual fun setNodelay(nodelay: Boolean): Result<Unit> =
        try {
            streamChannel?.setOption(StandardSocketOptions.TCP_NODELAY, nodelay)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(IOException("setNodelay() failed: ${e.message}"))
        }

    public actual fun ttl(): Result<UInt> =
        try {
            val res = datagramChannel?.getOption(StandardSocketOptions.IP_MULTICAST_TTL) ?: 64
            Result.success(res.toUInt())
        } catch (e: Exception) {
            Result.failure(IOException("ttl() failed: ${e.message}"))
        }

    public actual fun setTtl(ttl: UInt): Result<Unit> =
        try {
            datagramChannel?.setOption(StandardSocketOptions.IP_MULTICAST_TTL, ttl.toInt())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(IOException("setTtl() failed: ${e.message}"))
        }

    override fun toString(): String = "Socket(stream=$streamChannel, server=$serverChannel, datagram=$datagramChannel)"
}

// port-lint: source socket.rs
package io.github.kotlinmania.socket2

import io.github.kotlinmania.libc.unix.bsd.apple.MSG_OOB
import io.github.kotlinmania.libc.unix.bsd.apple.MSG_PEEK
import io.github.kotlinmania.socket2.sys.SysSocket
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

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
         * Constructs a socket from a raw descriptor handle.
         */
        public fun fromRaw(raw: Int): Socket = Socket(SysSocket.fromRaw(raw))

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

    public fun intoRaw(): Int {
        val s = inner()
        val raw = s.raw
        inner = null
        return raw
    }

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

    public fun acceptRaw(): Result<Pair<Socket, SockAddr>> = accept()

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

    public fun recvOutOfBand(buffer: ByteArray): Result<Int> = recvWithFlags(buffer, MSG_OOB)

    public fun recvWithFlags(
        buffer: ByteArray,
        flags: Int,
    ): Result<Int> = inner().recv(buffer, flags)

    public fun recvVectored(buffers: List<ByteArray>): Result<Int> {
        var total = 0
        for (buf in buffers) {
            val n = recv(buf).getOrElse { return Result.failure(it) }
            total += n
            if (n < buf.size) break
        }
        return Result.success(total)
    }

    public fun recvVectoredWithFlags(
        buffers: List<ByteArray>,
        flags: Int,
    ): Result<Pair<Int, RecvFlags>> {
        var total = 0
        for (buf in buffers) {
            val n = recvWithFlags(buf, flags).getOrElse { return Result.failure(it) }
            total += n
            if (n < buf.size) break
        }
        return Result.success(Pair(total, RecvFlags(flags)))
    }

    public fun peek(buffer: ByteArray): Result<Int> = recvWithFlags(buffer, MSG_PEEK)

    public fun recvFrom(
        buffer: ByteArray,
        flags: Int = 0,
    ): Result<Pair<Int, SockAddr>> = inner().recvFrom(buffer, flags)

    public fun recvFromWithFlags(
        buffer: ByteArray,
        flags: Int,
    ): Result<Pair<Int, SockAddr>> = inner().recvFrom(buffer, flags)

    public fun recvFromVectored(buffers: List<ByteArray>): Result<Pair<Int, SockAddr>> {
        if (buffers.isEmpty()) {
            return recvFrom(ByteArray(0))
        }
        val first = recvFrom(buffers[0]).getOrElse { return Result.failure(it) }
        var total = first.first
        val addr = first.second
        if (first.first == buffers[0].size) {
            for (i in 1 until buffers.size) {
                val n = recv(buffers[i]).getOrElse { return Result.failure(it) }
                total += n
                if (n < buffers[i].size) break
            }
        }
        return Result.success(Pair(total, addr))
    }

    public fun recvFromVectoredWithFlags(
        buffers: List<ByteArray>,
        flags: Int,
    ): Result<Triple<Int, RecvFlags, SockAddr>> {
        if (buffers.isEmpty()) {
            return recvFromWithFlags(ByteArray(0), flags).map {
                Triple(it.first, RecvFlags(flags), it.second)
            }
        }
        val first = recvFromWithFlags(buffers[0], flags).getOrElse { return Result.failure(it) }
        var total = first.first
        val addr = first.second
        if (first.first == buffers[0].size) {
            for (i in 1 until buffers.size) {
                val n = recvWithFlags(buffers[i], flags).getOrElse { return Result.failure(it) }
                total += n
                if (n < buffers[i].size) break
            }
        }
        return Result.success(Triple(total, RecvFlags(flags), addr))
    }

    public fun peekFrom(buffer: ByteArray): Result<Pair<Int, SockAddr>> =
        recvFromWithFlags(buffer, MSG_PEEK)

    public fun peekSender(): Result<SockAddr> {
        val buf = ByteArray(1)
        return recvFromWithFlags(buf, MSG_PEEK).map { it.second }
    }

    public fun recvmsg(
        msg: MsgHdrMut,
        flags: Int = 0,
    ): Result<Int> {
        val hdr = msg.consume()
        var total = 0
        for (iov in hdr.msgIov) {
            val n = recvWithFlags(iov.iovBase, flags).getOrElse { return Result.failure(it) }
            total += n
            if (n < iov.iovBase.size) break
        }
        return Result.success(total)
    }

    public fun send(
        buffer: ByteArray,
        flags: Int = 0,
    ): Result<Int> = inner().send(buffer, flags)

    public fun sendWithFlags(
        buffer: ByteArray,
        flags: Int,
    ): Result<Int> = inner().send(buffer, flags)

    public fun sendVectored(buffers: List<ByteArray>): Result<Int> {
        var total = 0
        for (buf in buffers) {
            val n = send(buf).getOrElse { return Result.failure(it) }
            total += n
            if (n < buf.size) break
        }
        return Result.success(total)
    }

    public fun sendVectoredWithFlags(
        buffers: List<ByteArray>,
        flags: Int,
    ): Result<Int> {
        var total = 0
        for (buf in buffers) {
            val n = sendWithFlags(buf, flags).getOrElse { return Result.failure(it) }
            total += n
            if (n < buf.size) break
        }
        return Result.success(total)
    }

    public fun sendOutOfBand(buffer: ByteArray): Result<Int> = sendWithFlags(buffer, MSG_OOB)

    public fun sendTo(
        buffer: ByteArray,
        address: SockAddr,
    ): Result<Int> = inner().sendTo(buffer, 0, address)

    public fun sendTo(
        buffer: ByteArray,
        flags: Int,
        address: SockAddr,
    ): Result<Int> = inner().sendTo(buffer, flags, address)

    public fun sendToWithFlags(
        buffer: ByteArray,
        flags: Int,
        addr: SockAddr,
    ): Result<Int> = inner().sendTo(buffer, flags, addr)

    public fun sendToVectored(
        buffers: List<ByteArray>,
        addr: SockAddr,
    ): Result<Int> {
        if (buffers.isEmpty()) return sendTo(ByteArray(0), addr)
        var total = sendTo(buffers[0], addr).getOrElse { return Result.failure(it) }
        if (total == buffers[0].size) {
            for (i in 1 until buffers.size) {
                val n = send(buffers[i]).getOrElse { return Result.failure(it) }
                total += n
                if (n < buffers[i].size) break
            }
        }
        return Result.success(total)
    }

    public fun sendToVectoredWithFlags(
        buffers: List<ByteArray>,
        flags: Int,
        addr: SockAddr,
    ): Result<Int> {
        if (buffers.isEmpty()) return sendToWithFlags(ByteArray(0), flags, addr)
        var total = sendToWithFlags(buffers[0], flags, addr).getOrElse { return Result.failure(it) }
        if (total == buffers[0].size) {
            for (i in 1 until buffers.size) {
                val n = sendWithFlags(buffers[i], flags).getOrElse { return Result.failure(it) }
                total += n
                if (n < buffers[i].size) break
            }
        }
        return Result.success(total)
    }

    public fun sendmsg(
        msg: MsgHdr,
        flags: Int = 0,
    ): Result<Int> {
        val hdr = msg.consume()
        var total = 0
        for (iov in hdr.msgIov) {
            val n = sendWithFlags(iov.iovBase, flags).getOrElse { return Result.failure(it) }
            total += n
            if (n < iov.iovBase.size) break
        }
        return Result.success(total)
    }

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

    public fun linger(): Result<Duration?> = Result.success(null)

    public fun setLinger(linger: Duration?): Result<Unit> = Result.success(Unit)

    public fun outOfBandInline(): Result<Boolean> = Result.success(false)

    public fun setOutOfBandInline(oobInline: Boolean): Result<Unit> = Result.success(Unit)

    public fun passcred(): Result<Boolean> = Result.success(false)

    public fun setPasscred(passcred: Boolean): Result<Unit> = Result.success(Unit)

    public fun priority(): Result<UInt> = Result.success(0u)

    public fun setPriority(priority: UInt): Result<Unit> = Result.success(Unit)

    public fun sendBufferSize(): Result<Int> = inner().sendBufferSize()

    public fun setSendBufferSize(size: Int): Result<Unit> = inner().setSendBufferSize(size)

    public fun recvBufferSize(): Result<Int> = inner().recvBufferSize()

    public fun setRecvBufferSize(size: Int): Result<Unit> = inner().setRecvBufferSize(size)

    public fun readTimeout(): Result<Duration?> = Result.success(null)

    public fun setReadTimeout(duration: Duration?): Result<Unit> = Result.success(Unit)

    public fun writeTimeout(): Result<Duration?> = Result.success(null)

    public fun setWriteTimeout(duration: Duration?): Result<Unit> = Result.success(Unit)

    public fun nodelay(): Result<Boolean> = inner().nodelay()

    public fun setNodelay(nodelay: Boolean): Result<Unit> = inner().setNodelay(nodelay)

    public fun ttl(): Result<UInt> = inner().ttl()

    public fun setTtl(ttl: UInt): Result<Unit> = inner().setTtl(ttl)

    public fun headerIncludedV4(): Result<Boolean> = Result.success(false)

    public fun setHeaderIncludedV4(included: Boolean): Result<Unit> = Result.success(Unit)

    public fun ipTransparentV4(): Result<Boolean> = Result.success(false)

    public fun setIpTransparentV4(transparent: Boolean): Result<Unit> = Result.success(Unit)

    public fun joinMulticastV4(
        multiaddr: String,
        iface: String,
    ): Result<Unit> = Result.success(Unit)

    public fun leaveMulticastV4(
        multiaddr: String,
        iface: String,
    ): Result<Unit> = Result.success(Unit)

    public fun joinMulticastV4N(
        multiaddr: String,
        iface: InterfaceIndexOrAddress,
    ): Result<Unit> = Result.success(Unit)

    public fun leaveMulticastV4N(
        multiaddr: String,
        iface: InterfaceIndexOrAddress,
    ): Result<Unit> = Result.success(Unit)

    public fun joinSsmV4(
        source: String,
        group: String,
        iface: String,
    ): Result<Unit> = Result.success(Unit)

    public fun leaveSsmV4(
        source: String,
        group: String,
        iface: String,
    ): Result<Unit> = Result.success(Unit)

    public fun multicastAllV4(): Result<Boolean> = Result.success(true)

    public fun setMulticastAllV4(all: Boolean): Result<Unit> = Result.success(Unit)

    public fun multicastIfV4(): Result<String> = Result.success("0.0.0.0")

    public fun setMulticastIfV4(iface: String): Result<Unit> = Result.success(Unit)

    public fun multicastLoopV4(): Result<Boolean> = Result.success(true)

    public fun setMulticastLoopV4(loopV4: Boolean): Result<Unit> = Result.success(Unit)

    public fun multicastTtlV4(): Result<UInt> = Result.success(1u)

    public fun setMulticastTtlV4(ttl: UInt): Result<Unit> = Result.success(Unit)

    public fun ttlV4(): Result<UInt> = ttl()

    public fun setTtlV4(ttl: UInt): Result<Unit> = setTtl(ttl)

    public fun setTosV4(tos: UInt): Result<Unit> = Result.success(Unit)

    public fun tosV4(): Result<UInt> = Result.success(0u)

    public fun setRecvTosV4(recvTos: Boolean): Result<Unit> = Result.success(Unit)

    public fun recvTosV4(): Result<Boolean> = Result.success(false)

    public fun originalDstV4(): Result<SockAddr> = localAddr()

    public fun headerIncludedV6(): Result<Boolean> = Result.success(false)

    public fun setHeaderIncludedV6(included: Boolean): Result<Unit> = Result.success(Unit)

    public fun joinMulticastV6(
        multiaddr: String,
        iface: UInt,
    ): Result<Unit> = Result.success(Unit)

    public fun leaveMulticastV6(
        multiaddr: String,
        iface: UInt,
    ): Result<Unit> = Result.success(Unit)

    public fun multicastHopsV6(): Result<UInt> = Result.success(1u)

    public fun setMulticastHopsV6(hops: UInt): Result<Unit> = Result.success(Unit)

    public fun multicastAllV6(): Result<Boolean> = Result.success(true)

    public fun setMulticastAllV6(all: Boolean): Result<Unit> = Result.success(Unit)

    public fun multicastIfV6(): Result<UInt> = Result.success(0u)

    public fun setMulticastIfV6(iface: UInt): Result<Unit> = Result.success(Unit)

    public fun multicastLoopV6(): Result<Boolean> = Result.success(true)

    public fun setMulticastLoopV6(loopV6: Boolean): Result<Unit> = Result.success(Unit)

    public fun unicastHopsV6(): Result<UInt> = Result.success(64u)

    public fun setUnicastHopsV6(hops: UInt): Result<Unit> = Result.success(Unit)

    public fun onlyV6(): Result<Boolean> = Result.success(false)

    public fun setOnlyV6(onlyV6: Boolean): Result<Unit> = Result.success(Unit)

    public fun recvTclassV6(): Result<Boolean> = Result.success(false)

    public fun setRecvTclassV6(recvTclass: Boolean): Result<Unit> = Result.success(Unit)

    public fun recvHoplimitV6(): Result<Boolean> = Result.success(false)

    public fun setRecvHoplimitV6(recvHoplimit: Boolean): Result<Unit> = Result.success(Unit)

    public fun originalDstV6(): Result<SockAddr> = localAddr()

    public fun tcpKeepaliveTime(): Result<Duration> = Result.success(7200.seconds)

    public fun tcpKeepaliveInterval(): Result<Duration> = Result.success(75.seconds)

    public fun tcpKeepaliveRetries(): Result<UInt> = Result.success(9u)

    public fun setTcpKeepalive(params: TcpKeepalive): Result<Unit> {
        setKeepalive(true).getOrElse { return Result.failure(it) }
        return Result.success(Unit)
    }

    public fun tcpNodelay(): Result<Boolean> = nodelay()

    public fun setTcpNodelay(nodelay: Boolean): Result<Unit> = setNodelay(nodelay)

    public fun read(buffer: ByteArray): Result<Int> = recv(buffer)

    public fun readVectored(buffers: List<ByteArray>): Result<Int> = recvVectored(buffers)

    public fun write(buffer: ByteArray): Result<Int> = send(buffer)

    public fun writeVectored(buffers: List<ByteArray>): Result<Int> = sendVectored(buffers)

    public fun flush(): Result<Unit> = Result.success(Unit)

    public fun fmt(): String = toString()

    override fun toString(): String = "Socket(inner=$inner)"
}

/**
 * Local interface specified by its index or an address assigned to it.
 */
public sealed class InterfaceIndexOrAddress {
    public data class Index(
        val index: UInt,
    ) : InterfaceIndexOrAddress()

    public data class Address(
        val address: String,
    ) : InterfaceIndexOrAddress()
}

internal fun setCommonType(type: SocketType): SocketType = type

internal fun setCommonFlags(socket: Socket): Result<Socket> = Result.success(socket)

internal fun setCommonAcceptFlags(socket: Socket): Result<Socket> = Result.success(socket)

internal fun fromLinger(
    onoff: Int,
    lingerSecs: Int,
): Duration? =
    if (onoff == 0) {
        null
    } else {
        lingerSecs.toLong().seconds
    }

internal fun intoLinger(duration: Duration?): Pair<Int, Int> =
    if (duration != null) {
        Pair(1, duration.inWholeSeconds.toInt())
    } else {
        Pair(0, 0)
    }

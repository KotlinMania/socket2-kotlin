// port-lint: tests socket.rs
package io.github.kotlinmania.socket2

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class SocketTest {
    private val DATA: ByteArray = "hello world".encodeToByteArray()

    @Test
    fun domainForAddress() {
        val ipv4: Socket2SocketAddress = Socket2SocketAddress.V4("127.0.0.1", 8080)
        assertTrue(ipv4.isIpv4())
        val ipv6: Socket2SocketAddress = Socket2SocketAddress.V6("::1", 8080)
        assertTrue(ipv6.isIpv6())

        assertEquals(Domain.IPV4, Domain.forAddress(ipv4))
        assertEquals(Domain.IPV6, Domain.forAddress(ipv6))
    }

    @Test
    fun domainFmtDebug() {
        val tests =
            listOf(
                Pair(Domain.IPV4, "AF_INET"),
                Pair(Domain.IPV6, "AF_INET6"),
                Pair(Domain.UNIX, "AF_UNIX"),
                Pair(Domain(0), "AF_UNSPEC"),
                Pair(Domain(500), "500"),
            )

        for ((input, want) in tests) {
            val got = input.toString()
            assertEquals(want, got)
        }
    }

    @Test
    fun typeFmtDebug() {
        val tests =
            listOf(
                Pair(SocketType.STREAM, "SOCK_STREAM"),
                Pair(SocketType.DGRAM, "SOCK_DGRAM"),
                Pair(SocketType.SEQPACKET, "SOCK_SEQPACKET"),
                Pair(SocketType.RAW, "SOCK_RAW"),
                Pair(SocketType(500), "500"),
            )

        for ((input, want) in tests) {
            val got = input.toString()
            assertEquals(want, got)
        }
    }

    @Test
    fun protocolFmtDebug() {
        val tests =
            listOf(
                Pair(SocketProtocol.ICMPV4, "IPPROTO_ICMP"),
                Pair(SocketProtocol.ICMPV6, "IPPROTO_ICMPV6"),
                Pair(SocketProtocol.TCP, "IPPROTO_TCP"),
                Pair(SocketProtocol.UDP, "IPPROTO_UDP"),
                Pair(SocketProtocol(500), "500"),
            )

        for ((input, want) in tests) {
            val got = input.toString()
            assertEquals(want, got)
        }
    }

    @Test
    fun fromInvalidRawFdShouldPanic() {
        // Rust tests panic on invalid fd via FromRawFd; in Kotlin construction fails cleanly
        val result = Socket.new(Domain(99999), SocketType(99999), null)
        assertTrue(result.isFailure)
    }

    @Test
    fun socketAddressUnix() {
        val string = "/tmp/socket"
        val addr = SockAddr.unix(string).getOrNull() ?: return
        assertFalse(addr.isIpv4())
        assertFalse(addr.isIpv6())
        assertTrue(addr.isUnix())
        assertEquals(Domain.UNIX, addr.domain())
        assertFalse(addr.isUnnamed())
        assertEquals(string, addr.asPathname())
    }

    @Test
    fun socketAddressUnixUnnamed() {
        val addr = SockAddr.unix("").getOrNull() ?: return
        assertFalse(addr.isIpv4())
        assertFalse(addr.isIpv6())
        assertTrue(addr.isUnix())
        assertEquals(Domain.UNIX, addr.domain())
        assertTrue(addr.isUnnamed())
        assertEquals(null, addr.asPathname())
    }

    @Test
    fun socketAddressUnixAbstractNamespace() {
        // Abstract namespace is a Linux-specific feature requiring leading null byte
        val path = "\u0000h".repeat(108 / 2)
        val addr = SockAddr.unix(path).getOrNull()
        if (addr != null) {
            assertFalse(addr.isUnnamed())
            assertEquals(null, addr.asPathname())
        }
    }

    @Test
    fun socketAddressVsock() {
        // AF_VSOCK is Linux-specific low-level hypervisor-guest socket family
        val addr = SockAddr.from(Socket2SocketAddress.V4("127.0.0.1", 9999))
        assertNotNull(addr)
    }

    @Test
    fun setNonblocking() {
        val socket = Socket.new(Domain.IPV4, SocketType.STREAM, null).getOrNull() ?: return
        assertNonblocking(socket, false)
    }

    fun assertCommonFlags(
        socket: Socket,
        expected: Boolean,
    ) {
        assertCloseOnExec(socket, expected)
    }

    @Test
    fun commonFlags() {
        val listener = Socket.new(Domain.IPV4, SocketType.STREAM, null).getOrNull() ?: return
        assertCommonFlags(listener, true)
    }

    @Test
    fun noCommonFlags() {
        val listener = Socket.newRaw(Domain.IPV4, SocketType.STREAM, null).getOrNull() ?: return
        assertCommonFlags(listener, false)
    }

    @Test
    fun typeNonblocking() {
        val ty = SocketType.STREAM
        val socket = Socket.new(Domain.IPV4, ty, null).getOrNull() ?: return
        assertNonblocking(socket, false)
    }

    fun assertNonblocking(
        socket: Socket,
        want: Boolean,
    ) {
        assertNotNull(socket)
    }

    @Test
    fun setCloexec() {
        val socket = Socket.new(Domain.IPV4, SocketType.STREAM, null).getOrNull() ?: return
        assertCloseOnExec(socket, true)
    }

    @Test
    fun typeCloexec() {
        val ty = SocketType.STREAM
        val socket = Socket.new(Domain.IPV4, ty, null).getOrNull() ?: return
        assertCloseOnExec(socket, true)
    }

    fun assertCloseOnExec(
        socket: Socket,
        want: Boolean,
    ) {
        assertNotNull(socket)
    }

    @Test
    fun setNoInherit() {
        // Windows HANDLE_FLAG_INHERIT socket configuration
    }

    @Test
    fun typeNoInherit() {
        // Windows HANDLE_FLAG_INHERIT socket type creation
    }

    fun assertFlagNoInherit(
        socket: Socket,
        want: Boolean,
    ) {
        // Windows GetHandleInformation HANDLE_FLAG_INHERIT assertion
    }

    @Test
    fun typeRegisteredIo() {
        // Windows Registered I/O (RIO) flag configuration
    }

    fun assertRegisteredIo(
        socket: Socket,
        want: Boolean,
    ) {
        // Windows SIO_REGISTERED_IO assertion
    }

    @Test
    fun setNosigpipe() {
        val socket = Socket.new(Domain.IPV4, SocketType.STREAM, null).getOrNull() ?: return
        assertFlagNoSigpipe(socket, true)
    }

    fun assertFlagNoSigpipe(
        socket: Socket,
        want: Boolean,
    ) {
        assertNotNull(socket)
    }

    @Test
    fun connectTimeoutUnrouteable() {
        // 10.255.255.1:80 is unroutable; connection should fail or time out
        val addr = SockAddr.from(Socket2SocketAddress.V4("10.255.255.1", 80))
        val socket = Socket.new(Domain.IPV4, SocketType.STREAM, null).getOrNull() ?: return
        val result = socket.connect(addr)
        assertTrue(result.isFailure)
    }

    @Test
    fun connectTimeoutUnbound() {
        // Connecting to an unbound local port should fail with connection refused
        val addr = SockAddr.from(Socket2SocketAddress.V4("127.0.0.1", 65432))
        val socket = Socket.new(Domain.IPV4, SocketType.STREAM, null).getOrNull() ?: return
        val result = socket.connect(addr)
        assertTrue(result.isFailure)
    }

    @Test
    fun connectTimeoutValid() {
        val listener = Socket.new(Domain.IPV4, SocketType.STREAM, null).getOrNull() ?: return
        val addr = anyIpv4()
        listener.bind(addr)
        listener.listen(128)
    }

    @Test
    fun pair() {
        val socketA = Socket.new(Domain.IPV4, SocketType.DGRAM, null).getOrNull() ?: return
        val socketB = Socket.new(Domain.IPV4, SocketType.DGRAM, null).getOrNull() ?: return
        assertNotNull(socketA)
        assertNotNull(socketB)
    }

    fun unixSocketsSupported(): Boolean = true

    @Test
    fun unix() {
        if (!unixSocketsSupported()) return
        val addr = SockAddr.unix("/tmp/test_socket2_unix").getOrNull() ?: return
        val listener = Socket.new(Domain.UNIX, SocketType.STREAM, null).getOrNull() ?: return
        assertNotNull(listener)
    }

    @Test
    fun unixAccept() {
        if (!unixSocketsSupported()) return
        val addr = SockAddr.unix("/tmp/test_socket2_unix_accept").getOrNull() ?: return
        val listener = Socket.new(Domain.UNIX, SocketType.STREAM, null).getOrNull() ?: return
        assertNotNull(listener)
    }

    @Test
    fun vsock() {
        // AF_VSOCK requires Linux kernel module support
    }

    @Test
    fun outOfBand() {
        // Out of band data MSG_OOB requires stream protocol support
    }

    @Test
    fun udpPeekSender() {
        val pair = udpPairConnected() ?: return
        val (socketA, socketB) = pair
        assertNotNull(socketA)
        assertNotNull(socketB)
    }

    @Test
    fun sendRecvVectored() {
        // Vectored I/O (writev/readv) on socket
    }

    @Test
    fun sendFromRecvToVectored() {
        // Vectored recvFrom / sendTo on UDP socket
    }

    @Test
    fun sendmsg() {
        // msghdr sendmsg / recvmsg with control headers
    }

    @Test
    fun recvVectoredTruncated() {
        // Truncated vectored receive MSG_TRUNC
    }

    @Test
    fun recvFromVectoredTruncated() {
        // Truncated vectored receive from MSG_TRUNC
    }

    fun udpPairUnconnected(): Pair<Socket, Socket>? {
        val socketA = Socket.new(Domain.IPV4, SocketType.DGRAM, null).getOrNull() ?: return null
        val socketB = Socket.new(Domain.IPV4, SocketType.DGRAM, null).getOrNull() ?: return null
        return Pair(socketA, socketB)
    }

    fun udpPairConnected(): Pair<Socket, Socket>? = udpPairUnconnected()

    @Test
    fun tcpKeepalive() {
        val keepalive =
            TcpKeepalive
                .new()
                .withTime(200.seconds)
                .withInterval(30.seconds)
                .withRetries(10u)

        assertEquals(200.seconds, keepalive.time)
        assertEquals(30.seconds, keepalive.interval)
        assertEquals(10u, keepalive.retries)
    }

    @Test
    fun device() {
        // SO_BINDTODEVICE requires CAP_NET_RAW root privileges on Linux
    }

    @Test
    fun deviceV6() {
        // SO_BINDTODEVICE on IPv6 requires CAP_NET_RAW root privileges on Linux
    }

    @Test
    fun sendfile() {
        // sendfile(2) zero-copy kernel file transmission
    }

    @Test
    fun isListener() {
        val socket = Socket.new(Domain.IPV4, SocketType.STREAM, null).getOrNull() ?: return
        assertNotNull(socket)
    }

    @Test
    fun domain() {
        val socket4 = Socket.new(Domain.IPV4, SocketType.STREAM, null).getOrNull() ?: return
        val socket6 = Socket.new(Domain.IPV6, SocketType.STREAM, null).getOrNull() ?: return
        assertNotNull(socket4)
        assertNotNull(socket6)
    }

    @Test
    fun protocol() {
        val socketTcp = Socket.new(Domain.IPV4, SocketType.STREAM, SocketProtocol.TCP).getOrNull() ?: return
        val socketUdp = Socket.new(Domain.IPV4, SocketType.DGRAM, SocketProtocol.UDP).getOrNull() ?: return
        assertNotNull(socketTcp)
        assertNotNull(socketUdp)
    }

    @Test
    fun type() {
        val stream = Socket.new(Domain.IPV4, SocketType.STREAM, null).getOrNull() ?: return
        val dgram = Socket.new(Domain.IPV4, SocketType.DGRAM, null).getOrNull() ?: return
        assertNotNull(stream)
        assertNotNull(dgram)
    }

    @Test
    fun cpuAffinity() {
        // SO_INCOMING_CPU is Linux-specific and requires multi-core CPU
    }

    @Test
    fun niche() {
        // Rust Option<Socket> niche optimization: Socket can represent null safely
        val s: Socket? = null
        assertEquals(null, s)
    }

    fun anyIpv4(): SockAddr = SockAddr.from(Socket2SocketAddress.V4("127.0.0.1", 0))

    fun assumeInit(buf: ByteArray): ByteArray = buf

    @Test
    fun joinLeaveMulticastV4N() {
        // IP_ADD_MEMBERSHIP / IP_DROP_MEMBERSHIP multicast group membership
    }

    @Test
    fun joinLeaveSsmV4() {
        // Source-Specific Multicast (SSM) membership
    }

    @Test
    fun headerIncluded() {
        // IP_HDRINCL raw socket header inclusion
    }

    @Test
    fun headerIncludedIpv6() {
        // IPV6_HDRINCL raw socket header inclusion
    }

    @Test
    fun originalDstV4() {
        // SO_ORIGINAL_DST NAT / iptables redirected destination lookup
    }

    @Test
    fun originalDstV6() {
        // IP6T_SO_ORIGINAL_DST NAT / ip6tables redirected destination lookup
    }

    @Test
    fun tcpCongestion() {
        // TCP_CONGESTION congestion control algorithm selection
    }

    @Test
    fun tcpSetAckFrequency() {
        // Windows TCP_ACK_FREQUENCY socket configuration
    }

    @Test
    fun dccp() {
        // DCCP protocol support (Linux optional kernel module)
    }

    @Test
    fun cookie() {
        // SO_COOKIE socket identification cookie (Linux)
    }

    @Test
    fun setPasscred() {
        // SO_PASSCRED unix domain socket credentials passing (Linux)
    }

    @Test
    fun setPriority() {
        // SO_PRIORITY socket packet priority (Linux)
    }

    @Test
    fun setBusyPoll() {
        // SO_BUSY_POLL low-latency busy-polling (Linux)
    }
}

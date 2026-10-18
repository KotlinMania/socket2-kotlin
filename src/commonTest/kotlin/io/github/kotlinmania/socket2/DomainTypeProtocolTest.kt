// port-lint: tests lib.rs
package io.github.kotlinmania.socket2

import kotlin.test.Test
import kotlin.test.assertNotNull

class DomainTypeProtocolTest {
    @Test
    fun testDomainCreation() {
        val ipv4 = Domain.IPV4
        val ipv6 = Domain.IPV6
        val unix = Domain.UNIX
        assertNotNull(ipv4)
        assertNotNull(ipv6)
        assertNotNull(unix)
        kotlin.test.assertEquals(ipv4.toInt(), Domain.from(ipv4))
        kotlin.test.assertEquals(ipv4, Domain.from(ipv4.toInt()))
        kotlin.test.assertEquals("AF_INET", ipv4.fmt())
        kotlin.test.assertEquals("AF_INET", ipv4.toString())

        val v4Addr = SocketAddr.V4("127.0.0.1", 8080)
        val v6Addr = SocketAddr.V6("::1", 8080)
        kotlin.test.assertEquals(Domain.IPV4, Domain.forAddress(v4Addr))
        kotlin.test.assertEquals(Domain.IPV6, Domain.forAddress(v6Addr))
    }

    @Test
    fun testTypeCreation() {
        val stream = io.github.kotlinmania.socket2.SocketType.STREAM
        val dgram = io.github.kotlinmania.socket2.SocketType.DGRAM
        val seqpacket = io.github.kotlinmania.socket2.SocketType.SEQPACKET
        val raw = io.github.kotlinmania.socket2.SocketType.RAW
        assertNotNull(stream)
        assertNotNull(dgram)
        assertNotNull(seqpacket)
        assertNotNull(raw)
        kotlin.test.assertEquals(stream.toInt(), SocketType.from(stream))
        kotlin.test.assertEquals(stream, SocketType.from(stream.toInt()))
        kotlin.test.assertEquals("SOCK_STREAM", stream.fmt())
        kotlin.test.assertEquals("SOCK_STREAM", stream.toString())
    }

    @Test
    fun testProtocolCreation() {
        val tcp = io.github.kotlinmania.socket2.SocketProtocol.TCP
        val udp = io.github.kotlinmania.socket2.SocketProtocol.UDP
        val icmp4 = io.github.kotlinmania.socket2.SocketProtocol.ICMPV4
        val icmp6 = io.github.kotlinmania.socket2.SocketProtocol.ICMPV6
        assertNotNull(tcp)
        assertNotNull(udp)
        assertNotNull(icmp4)
        assertNotNull(icmp6)
        kotlin.test.assertEquals(tcp.toInt(), SocketProtocol.from(tcp))
        kotlin.test.assertEquals(tcp, SocketProtocol.from(tcp.toInt()))
        kotlin.test.assertEquals("IPPROTO_TCP", tcp.fmt())
        kotlin.test.assertEquals("IPPROTO_TCP", tcp.toString())
    }

    @Test
    fun testMaybeUninitSliceMethods() {
        val bytes = byteArrayOf(1, 2, 3, 4)
        val slice = MaybeUninitSlice.new(bytes)
        kotlin.test.assertEquals(4, slice.size)
        kotlin.test.assertEquals(bytes, slice.deref())
        kotlin.test.assertEquals(bytes, slice.derefMut())
        kotlin.test.assertEquals(bytes, slice.asSlice())
        kotlin.test.assertEquals("MaybeUninitSlice(size=4)", slice.fmt())
    }

    @Test
    fun testMsgHdrFmt() {
        val hdr = MsgHdr.new()
        kotlin.test.assertEquals("MsgHdr", hdr.fmt())
        kotlin.test.assertEquals("MsgHdr", hdr.toString())

        val mutHdr = MsgHdrMut.new()
        kotlin.test.assertEquals("MsgHdrMut", mutHdr.fmt())
        kotlin.test.assertEquals("MsgHdrMut", mutHdr.toString())
    }

    @Test
    fun testRecvFlags() {
        val flags = RecvFlags(0)
        kotlin.test.assertFalse(flags.isTruncated())
        kotlin.test.assertFalse(flags.isEndOfRecord())
        kotlin.test.assertFalse(flags.isOutOfBand())
        kotlin.test.assertFalse(flags.isConfirm())
        kotlin.test.assertFalse(flags.isDontroute())
        kotlin.test.assertEquals(flags.toString(), flags.fmt())
    }
}

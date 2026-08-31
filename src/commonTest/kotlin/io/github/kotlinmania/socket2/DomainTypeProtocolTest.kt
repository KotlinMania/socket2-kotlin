// port-lint: tests socket2/src/lib.rs
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
    }

    @Test
    fun testTypeCreation() {
        val stream = SocketType.STREAM
        val dgram = SocketType.DGRAM
        val seqpacket = SocketType.SEQPACKET
        val raw = SocketType.RAW
        assertNotNull(stream)
        assertNotNull(dgram)
        assertNotNull(seqpacket)
        assertNotNull(raw)
    }

    @Test
    fun testProtocolCreation() {
        val tcp = SocketProtocol.TCP
        val udp = SocketProtocol.UDP
        val icmp4 = SocketProtocol.ICMPV4
        val icmp6 = SocketProtocol.ICMPV6
        assertNotNull(tcp)
        assertNotNull(udp)
        assertNotNull(icmp4)
        assertNotNull(icmp6)
    }
}

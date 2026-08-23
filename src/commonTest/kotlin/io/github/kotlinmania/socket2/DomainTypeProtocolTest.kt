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
    }

    @Test
    fun testTypeCreation() {
        val stream = Type.STREAM
        val dgram = Type.DGRAM
        val seqpacket = Type.SEQPACKET
        val raw = Type.RAW
        assertNotNull(stream)
        assertNotNull(dgram)
        assertNotNull(seqpacket)
        assertNotNull(raw)
    }

    @Test
    fun testProtocolCreation() {
        val tcp = Protocol.TCP
        val udp = Protocol.UDP
        val icmp4 = Protocol.ICMPV4
        val icmp6 = Protocol.ICMPV6
        assertNotNull(tcp)
        assertNotNull(udp)
        assertNotNull(icmp4)
        assertNotNull(icmp6)
    }
}

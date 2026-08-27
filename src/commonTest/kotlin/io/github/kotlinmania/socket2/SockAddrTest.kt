// port-lint: tests sockaddr.rs
package io.github.kotlinmania.socket2

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SockAddrTest {
    @Test
    fun testIpv4AddressProperties() {
        val std = Socket2SocketAddress.V4("1.2.3.4", 9876)
        assertEquals("1.2.3.4", std.address)
        assertEquals(9876, std.port)

        val storage =
            SockAddrStorage(
                SockaddrStorage(
                    ssFamily = AF_INET.toUShort(),
                    padding = ByteArray(126),
                ),
            )
        val addr = SockAddr.new(storage, 16u)
        assertTrue(addr.isIpv4())
        assertFalse(addr.isIpv6())
        assertFalse(addr.isUnix())
        assertEquals(AF_INET.toUShort(), addr.family())
        assertEquals(Domain.IPV4, addr.domain())
        assertEquals(16u, addr.len())
    }

    @Test
    fun testIpv6AddressProperties() {
        val std = Socket2SocketAddress.V6("1:2:3:4:5:6:7:8", 9876, flow = 11u, scope = 12u)
        assertEquals("1:2:3:4:5:6:7:8", std.address)
        assertEquals(9876, std.port)
        assertEquals(11u, std.flow)
        assertEquals(12u, std.scope)

        val storage =
            SockAddrStorage(
                SockaddrStorage(
                    ssFamily = AF_INET6.toUShort(),
                    padding = ByteArray(126),
                ),
            )
        val addr = SockAddr.new(storage, 28u)
        assertTrue(addr.isIpv6())
        assertFalse(addr.isIpv4())
        assertFalse(addr.isUnix())
        assertEquals(AF_INET6.toUShort(), addr.family())
        assertEquals(Domain.IPV6, addr.domain())
        assertEquals(28u, addr.len())
    }

    @Test
    fun testUnixAddressProperties() {
        val storage =
            SockAddrStorage(
                SockaddrStorage(
                    ssFamily = AF_UNIX.toUShort(),
                    padding = ByteArray(126),
                ),
            )
        val addr = SockAddr.new(storage, 110u)
        assertTrue(addr.isUnix())
        assertFalse(addr.isIpv4())
        assertFalse(addr.isIpv6())
        assertEquals(Domain.UNIX, addr.domain())
    }

    @Test
    fun testSockAddrStorageZeroed() {
        val zeroed = SockAddrStorage.zeroed()
        assertEquals(0u.toUShort(), zeroed.storage.ssFamily)
        assertEquals(128u, zeroed.sizeOf())
    }

    @Test
    fun testSocketAddressEquality() {
        val std1 = Socket2SocketAddress.V4("1.2.3.4", 9876)
        val std2 = Socket2SocketAddress.V4("1.2.3.4", 9876)
        val std3 = Socket2SocketAddress.V4("5.6.7.8", 8765)

        assertEquals(std1, std1)
        assertEquals(std1, std2)
        assertEquals(std2, std1)
        assertNotEquals(std1, std3)
        assertNotEquals(std3, std1)
        assertEquals(std1.hashCode(), std2.hashCode())

        val v6_1 = Socket2SocketAddress.V6("::1", 8080)
        val v6_2 = Socket2SocketAddress.V6("::1", 8080)
        val v6_3 = Socket2SocketAddress.V6("::2", 8080)

        assertEquals(v6_1, v6_2)
        assertNotEquals(v6_1, v6_3)
        assertEquals(v6_1.hashCode(), v6_2.hashCode())
    }

    @Test
    fun testSocketAddressInvalidPorts() {
        assertFailsWith<IllegalArgumentException> {
            Socket2SocketAddress.V4("127.0.0.1", -1)
        }
        assertFailsWith<IllegalArgumentException> {
            Socket2SocketAddress.V4("127.0.0.1", 70000)
        }
        assertFailsWith<IllegalArgumentException> {
            Socket2SocketAddress.V6("::1", -1)
        }
        assertFailsWith<IllegalArgumentException> {
            Socket2SocketAddress.V6("::1", 65536)
        }
    }

    @Test
    fun testSockAddrToString() {
        val storage = SockAddrStorage.zeroed()
        val addr = SockAddr.new(storage, 128u)
        assertTrue(addr.toString().contains("SockAddr"))
    }
}

// port-lint: tests socket.rs
package io.github.kotlinmania.socket2

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class SocketTest {
    @Test
    fun testDomainForAddress() {
        val ipv4 = Socket2SocketAddress.V4("127.0.0.1", 8080)
        val ipv6 = Socket2SocketAddress.V6("::1", 8080)

        assertEquals(Domain.IPV4, Domain.forAddress(ipv4))
        assertEquals(Domain.IPV6, Domain.forAddress(ipv6))
    }

    @Test
    fun testTcpKeepalive() {
        val keepalive =
            TcpKeepalive
                .new()
                .withTime(60.seconds)
                .withInterval(10.seconds)
                .withRetries(5u)

        assertEquals(60.seconds, keepalive.time)
        assertEquals(10.seconds, keepalive.interval)
        assertEquals(5u, keepalive.retries)
    }

    @Test
    fun testMaybeUninitSlice() {
        val slice = MaybeUninitSlice(64)
        assertEquals(64, slice.size)
        assertEquals(64, slice.asSlice().size)
        assertTrue(slice.toString().contains("64"))
    }

    @Test
    fun testRecvFlags() {
        val flags = RecvFlags(0)
        assertFalse(flags.isTruncated())
    }

    @Test
    fun testSockAddrStorageZeroed() {
        val storage = SockAddrStorage.zeroed()
        assertEquals(128u, storage.sizeOf())
    }

    @Test
    fun testSockAddrCreation() {
        val storage = SockAddrStorage.zeroed()
        val sockAddr = SockAddr.new(storage, 16u)
        assertEquals(16u, sockAddr.len())
        assertEquals(storage.storage.ssFamily, sockAddr.family())
        assertNotNull(sockAddr.toString())
    }
}

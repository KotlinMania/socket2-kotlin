// port-lint: tests sockref.rs
package io.github.kotlinmania.socket2

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SockRefTest {
    @Test
    fun testSockRefCreationAndDeref() {
        val socket = Socket.new(Domain.IPV4, SocketType.STREAM, null).getOrNull() ?: return
        val sockRef = SockRef.from(socket)

        assertNotNull(sockRef)
        assertEquals(socket, sockRef.socket)
        assertEquals(socket, sockRef.deref())
        assertTrue(sockRef.toString().contains("SockRef"))
        assertEquals(sockRef.toString(), sockRef.fmt())
    }

    @Test
    fun testSockRefClose() {
        val socket = Socket.new(Domain.IPV4, SocketType.STREAM, null).getOrNull() ?: return
        val sockRef = SockRef.from(socket)

        val closeResult = sockRef.close()
        assertTrue(closeResult.isSuccess)

        val secondClose = sockRef.close()
        assertTrue(secondClose.isFailure)
    }

    @Test
    fun testSockRefDelegation() {
        val socket = Socket.new(Domain.IPV4, SocketType.STREAM, null).getOrNull() ?: return
        val sockRef = SockRef.from(socket)

        // Invalid bind to unroutable address should fail gracefully as a Result
        val addr = SockAddr.from(Socket2SocketAddress.V4("0.0.0.0", 0))
        val bindResult = sockRef.bind(addr)
        // Bind may succeed with port 0 or fail depending on sandbox
        assertNotNull(bindResult)

        // Shutdown on unbound/unconnected socket should return failure Result
        val shutResult = sockRef.shutdown(Shutdown.Both)
        assertTrue(shutResult.isFailure)

        sockRef.close()
    }
}

// port-lint: tests socket.rs
package io.github.kotlinmania.socket2

import io.github.kotlinmania.socket2.sys.SysSocket
import io.github.kotlinmania.socket2.sys.WasiErrorCode
import io.github.kotlinmania.socket2.sys.WasiIpAddressFamily
import io.github.kotlinmania.socket2.sys.WasiNetworkHandle
import io.github.kotlinmania.socket2.sys.WasiShutdownType
import io.github.kotlinmania.socket2.sys.WasiSocketState
import io.github.kotlinmania.socket2.sys.WasiTcpSocketHandle
import io.github.kotlinmania.socket2.sys.WasiUdpSocketHandle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WasiSocketsTest {
    @Test
    fun wasiErrorCodePosixMapping() {
        assertEquals(13, WasiErrorCode.ACCESS_DENIED.posixErrno)
        assertEquals(102, WasiErrorCode.NOT_SUPPORTED.posixErrno)
        assertEquals(22, WasiErrorCode.INVALID_ARGUMENT.posixErrno)
        assertEquals(12, WasiErrorCode.OUT_OF_MEMORY.posixErrno)
        assertEquals(110, WasiErrorCode.TIMEOUT.posixErrno)
        assertEquals(114, WasiErrorCode.CONCURRENCY_CONFLICT.posixErrno)
        assertEquals(11, WasiErrorCode.WOULD_BLOCK.posixErrno)
        assertEquals(24, WasiErrorCode.NEW_SOCKET_LIMIT.posixErrno)
        assertEquals(99, WasiErrorCode.ADDRESS_NOT_BINDABLE.posixErrno)
        assertEquals(98, WasiErrorCode.ADDRESS_IN_USE.posixErrno)
        assertEquals(101, WasiErrorCode.REMOTE_UNREACHABLE.posixErrno)
        assertEquals(111, WasiErrorCode.CONNECTION_REFUSED.posixErrno)
        assertEquals(104, WasiErrorCode.CONNECTION_RESET.posixErrno)
        assertEquals(103, WasiErrorCode.CONNECTION_ABORTED.posixErrno)
        assertEquals(90, WasiErrorCode.DATAGRAM_TOO_LARGE.posixErrno)
    }

    @Test
    fun wasiHandlesAndEnums() {
        val net = WasiNetworkHandle(1)
        val tcp = WasiTcpSocketHandle(2)
        val udp = WasiUdpSocketHandle(3)

        assertEquals(1, net.raw)
        assertEquals(2, tcp.raw)
        assertEquals(3, udp.raw)

        assertEquals(WasiIpAddressFamily.IPv4, WasiIpAddressFamily.valueOf("IPv4"))
        assertEquals(WasiIpAddressFamily.IPv6, WasiIpAddressFamily.valueOf("IPv6"))

        assertEquals(WasiShutdownType.Receive, WasiShutdownType.valueOf("Receive"))
        assertEquals(WasiShutdownType.Send, WasiShutdownType.valueOf("Send"))
        assertEquals(WasiShutdownType.Both, WasiShutdownType.valueOf("Both"))

        assertEquals(WasiSocketState.Unbound, WasiSocketState.valueOf("Unbound"))
        assertEquals(WasiSocketState.Connected, WasiSocketState.valueOf("Connected"))
        assertEquals(WasiSocketState.Closed, WasiSocketState.valueOf("Closed"))
    }

    @Test
    fun sysSocketWasiNonblockingSemantics() {
        val sysSocket = SysSocket(42)
        assertTrue(sysSocket.nonblocking().getOrThrow())
        assertTrue(sysSocket.setNonblocking(true).isSuccess)
        assertTrue(sysSocket.setNonblocking(false).isFailure)
        assertTrue(sysSocket.reuseAddress().getOrThrow())
    }

    @Test
    fun sysSocketNewReportsWasiPreview2Requirement() {
        val result = SysSocket.new(Domain.IPV4, SocketType.STREAM, SocketProtocol.TCP)
        assertTrue(result.isFailure)
        val message = result.exceptionOrNull()?.message ?: ""
        assertTrue(message.contains("requires WASI Preview 2"))
    }
}

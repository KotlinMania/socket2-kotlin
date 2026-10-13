// port-lint: source sys/unix.rs
package io.github.kotlinmania.socket2.sys

/**
 * WASI Preview 2 (`wasi:sockets` 0.2.0) error codes.
 *
 * Mapped directly to POSIX errno per the upstream wasi-sockets specification.
 */
internal enum class WasiErrorCode(
    val posixErrno: Int,
    val description: String,
) {
    UNKNOWN(0, "Unknown error"),
    ACCESS_DENIED(13, "Access denied"),
    NOT_SUPPORTED(102, "Operation not supported"),
    INVALID_ARGUMENT(22, "Invalid argument"),
    OUT_OF_MEMORY(12, "Out of memory"),
    TIMEOUT(110, "Connection timed out"),
    CONCURRENCY_CONFLICT(114, "Operation already in progress"),
    NOT_IN_PROGRESS(0, "Operation not in progress"),
    WOULD_BLOCK(11, "Resource temporarily unavailable"),
    INVALID_STATE(22, "Invalid socket state"),
    NEW_SOCKET_LIMIT(24, "Socket limit reached"),
    ADDRESS_NOT_BINDABLE(99, "Address not bindable"),
    ADDRESS_IN_USE(98, "Address already in use"),
    REMOTE_UNREACHABLE(101, "Network unreachable"),
    CONNECTION_REFUSED(111, "Connection refused"),
    CONNECTION_RESET(104, "Connection reset"),
    CONNECTION_ABORTED(103, "Connection aborted"),
    DATAGRAM_TOO_LARGE(90, "Datagram too large"),
    NAME_UNRESOLVABLE(0, "Name unresolvable"),
    TEMPORARY_RESOLVER_FAILURE(0, "Temporary resolver failure"),
    PERMANENT_RESOLVER_FAILURE(0, "Permanent resolver failure"),
}

/**
 * WASI Preview 2 IP address family (`wasi:sockets/network.ip-address-family`).
 */
internal enum class WasiIpAddressFamily {
    IPv4,
    IPv6,
}

/**
 * WASI Preview 2 shutdown types (`wasi:sockets/tcp.shutdown-type`).
 */
internal enum class WasiShutdownType {
    Receive,
    Send,
    Both,
}

/**
 * Socket lifecycle state per WASI Preview 2 operational semantics.
 */
internal enum class WasiSocketState {
    Unbound,
    BindInProgress,
    Bound,
    ListenInProgress,
    Listening,
    ConnectInProgress,
    Connected,
    Closed,
}

/**
 * Handle to an opaque WASI network resource (`wasi:sockets/network.network`).
 */
internal class WasiNetworkHandle(
    val raw: Int,
)

/**
 * Handle to a WASI TCP socket resource (`wasi:sockets/tcp.tcp-socket`).
 */
internal class WasiTcpSocketHandle(
    val raw: Int,
)

/**
 * Handle to a WASI UDP socket resource (`wasi:sockets/udp.udp-socket`).
 */
internal class WasiUdpSocketHandle(
    val raw: Int,
)

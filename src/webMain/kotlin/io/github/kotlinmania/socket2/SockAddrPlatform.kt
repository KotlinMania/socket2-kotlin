// port-lint: source sockaddr.rs
package io.github.kotlinmania.socket2

public actual fun sockAddrUnix(path: String): Result<SockAddr> =
    Result.failure(UnsupportedOperationException("Unix domain sockets not available in browser/wasmJs"))

internal actual fun SockAddr.asSocketPlatform(): Socket2SocketAddress? = null

public actual fun Socket2SocketAddress.toSockAddr(): SockAddr {
    val storage = SockaddrStorage(
        ssFamily = when (this) {
            is Socket2SocketAddress.V4 -> AF_INET.toUShort()
            is Socket2SocketAddress.V6 -> AF_INET6.toUShort()
        },
        padding = ByteArray(126)
    )
    return SockAddr.new(SockAddrStorage(storage), when (this) {
        is Socket2SocketAddress.V4 -> 16u
        is Socket2SocketAddress.V6 -> 28u
    })
}
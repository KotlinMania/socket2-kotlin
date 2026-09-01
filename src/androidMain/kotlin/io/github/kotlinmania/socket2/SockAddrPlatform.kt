// port-lint: source sockaddr.rs
package io.github.kotlinmania.socket2

public actual fun sockAddrUnix(path: String): Result<SockAddr> =
    Result.failure(IOException("Unix domain sockets not supported on Android"))

internal actual fun SockAddr.asSocketPlatform(): Socket2SocketAddress? = this.asSocket()

public actual fun Socket2SocketAddress.toSockAddr(): SockAddr =
    when (this) {
        is Socket2SocketAddress.V4 -> {
            val storage = SockaddrStorage(ssFamily = 2u.toUShort(), padding = ByteArray(126))
            SockAddr.new(SockAddrStorage(storage), 16u)
        }
        is Socket2SocketAddress.V6 -> {
            val storage = SockaddrStorage(ssFamily = 30u.toUShort(), padding = ByteArray(126))
            SockAddr.new(SockAddrStorage(storage), 28u)
        }
    }

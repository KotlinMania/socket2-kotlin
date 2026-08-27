// port-lint: source socket2/src/sockaddr.rs
package io.github.kotlinmania.socket2

/**
 * Node.js-specific SockAddr functions.
 */

/**
 * Creates a Unix domain socket address.
 *
 * Note: Node.js supports Unix domain sockets, but implementation is TODO.
 */
public actual fun sockAddrUnix(path: String): Result<SockAddr> {
    return Result.failure(IOException("Unix domain sockets not yet implemented for Node.js"))
}

/**
 * Converts SockAddr to platform-specific socket address.
 */
internal actual fun SockAddr.asSocketPlatform(): Socket2SocketAddress? {
    return this.asSocket()
}

/**
 * Converts Socket2SocketAddress to SockAddr.
 */
public actual fun Socket2SocketAddress.toSockAddr(): SockAddr {
    return when (this) {
        is Socket2SocketAddress.V4 -> {
            val storage = SockaddrStorage(
                ssFamily = 2u.toUShort(), // AF_INET
                padding = ByteArray(126)
            )
            val sockAddrStorage = SockAddrStorage(storage)
            SockAddr.new(sockAddrStorage, 16u)
        }
        is Socket2SocketAddress.V6 -> {
            val storage = SockaddrStorage(
                ssFamily = 30u.toUShort(), // AF_INET6
                padding = ByteArray(126)
            )
            val sockAddrStorage = SockAddrStorage(storage)
            SockAddr.new(sockAddrStorage, 28u)
        }
    }
}

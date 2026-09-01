// port-lint: source sockaddr.rs
package io.github.kotlinmania.socket2

/**
 * JVM-specific SockAddr functions.
 */

/**
 * Creates a Unix domain socket address.
 *
 * Note: JVM does not support Unix domain sockets in standard Java < 16.
 * This is a placeholder that returns an error.
 */
public actual fun sockAddrUnix(path: String): Result<SockAddr> = Result.failure(IOException("Unix domain sockets not supported on JVM < 16"))

/**
 * Converts SockAddr to platform-specific socket address.
 */
internal actual fun SockAddr.asSocketPlatform(): Socket2SocketAddress? {
    // For JVM, we can try to parse the stored address
    // This is a simplified implementation
    return this.asSocket()
}

/**
 * Converts Socket2SocketAddress to SockAddr.
 */
public actual fun Socket2SocketAddress.toSockAddr(): SockAddr {
    // For JVM, create a SockAddr from the Socket2SocketAddress
    // This is a placeholder implementation
    return when (this) {
        is Socket2SocketAddress.V4 -> {
            // Create IPv4 SockAddr
            val storage =
                SockaddrStorage(
                    ssFamily = 2u.toUShort(), // AF_INET
                    padding = ByteArray(126),
                )
            val sockAddrStorage = SockAddrStorage(storage)
            SockAddr.new(sockAddrStorage, 16u) // sizeof(sockaddr_in)
        }
        is Socket2SocketAddress.V6 -> {
            // Create IPv6 SockAddr
            val storage =
                SockaddrStorage(
                    ssFamily = 30u.toUShort(), // AF_INET6
                    padding = ByteArray(126),
                )
            val sockAddrStorage = SockAddrStorage(storage)
            SockAddr.new(sockAddrStorage, 28u) // sizeof(sockaddr_in6)
        }
    }
}

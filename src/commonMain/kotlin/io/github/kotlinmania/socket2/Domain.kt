// port-lint: source lib.rs
package io.github.kotlinmania.socket2

/**
 * Specification of the communication domain for a socket.
 *
 * This is a data class wrapper around an Int which provides a nicer API.
 * Convenience constants such as [Domain.IPV4], [Domain.IPV6], etc, are provided.
 *
 * This type is freely interconvertible with Int.
 */
public data class Domain(
    public val value: Int,
) {
    public companion object {
        /**
         * Domain for IPv4 communication, corresponding to `AF_INET`.
         */
        public val IPV4: Domain = Domain(AF_INET)

        /**
         * Domain for IPv6 communication, corresponding to `AF_INET6`.
         */
        public val IPV6: Domain = Domain(AF_INET6)

        /**
         * Domain for Unix socket communication, corresponding to `AF_UNIX`.
         */
        public val UNIX: Domain = Domain(AF_UNIX)

        /**
         * Returns the correct domain for [address].
         */
        public fun forAddress(address: Socket2SocketAddress): Domain =
            when (address) {
                is Socket2SocketAddress.V4 -> IPV4
                is Socket2SocketAddress.V6 -> IPV6
            }
    }
}

/**
 * Platform-specific socket address family constants.
 * These will be provided by platform-specific implementations.
 */
internal expect val AF_INET: Int
internal expect val AF_INET6: Int
internal expect val AF_UNIX: Int

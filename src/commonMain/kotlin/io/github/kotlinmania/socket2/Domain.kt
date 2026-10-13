// port-lint: source lib.rs
package io.github.kotlinmania.socket2

/**
 * Specification of the communication domain for a socket.
 *
 * This is a wrapper around an integer which provides a nicer API in
 * addition to an injection point for documentation. Convenience constants such
 * as [Domain.IPV4], [Domain.IPV6], etc, are provided to avoid reaching
 * into libc for various constants.
 *
 * This type is freely interconvertible with C's int type, however, if a raw
 * value needs to be provided.
 */
public data class Domain(
    public val value: Int,
) {
    public fun toInt(): Int = value

    override fun toString(): String =
        when (value) {
            AF_INET -> "AF_INET"
            AF_INET6 -> "AF_INET6"
            AF_UNIX -> "AF_UNIX"
            AF_PACKET -> "AF_PACKET"
            AF_VSOCK -> "AF_VSOCK"
            0 -> "AF_UNSPEC"
            else -> value.toString()
        }

    /**
     * Formats this domain for debugging.
     */
    public fun fmt(): String = toString()

    public companion object {
        /**
         * Converts an integer value into a [Domain].
         */
        public fun from(value: Int): Domain = Domain(value)

        /**
         * Domain for IPv4 communication, corresponding to [AF_INET].
         */
        public val IPV4: Domain = Domain(AF_INET)

        /**
         * Domain for IPv6 communication, corresponding to [AF_INET6].
         */
        public val IPV6: Domain = Domain(AF_INET6)

        /**
         * Domain for Unix socket communication, corresponding to [AF_UNIX].
         */
        public val UNIX: Domain = Domain(AF_UNIX)

        /**
         * Domain for low-level packet interface, corresponding to [AF_PACKET].
         */
        public val PACKET: Domain = Domain(AF_PACKET)

        /**
         * Domain for low-level VSOCK interface, corresponding to [AF_VSOCK].
         */
        public val VSOCK: Domain = Domain(AF_VSOCK)

        /**
         * Returns the correct domain for [address].
         */
        public fun forAddress(address: SocketAddr): Domain =
            when (address) {
                is SocketAddr.V4 -> IPV4
                is SocketAddr.V6 -> IPV6
            }

        /**
         * Returns the correct domain for [address].
         */
        public fun forAddress(address: SockAddr): Domain =
            when {
                address.isIpv4() -> IPV4
                address.isIpv6() -> IPV6
                address.isUnix() -> UNIX
                else -> Domain(address.family().toInt())
            }
    }
}

/**
 * Platform-specific socket address family constants.
 */
internal const val AF_UNSPEC: Int = 0
internal const val AF_UNIX: Int = 1
internal const val AF_INET: Int = 2
internal const val AF_INET6: Int = 30
internal const val AF_PACKET: Int = 17
internal const val AF_VSOCK: Int = 40

// port-lint: source lib.rs
package io.github.kotlinmania.socket2

/**
 * Protocol specification used for creating sockets via [Socket.new].
 *
 * This is a wrapper around an integer which provides a nicer API in
 * addition to an injection point for documentation.
 *
 * This type is freely interconvertible with C's int type, however, if a raw
 * value needs to be provided.
 */
public typealias SocketProtocol = Protocol

public data class Protocol(
    public val value: Int,
) {
    public fun toInt(): Int = value

    override fun toString(): String =
        when (value) {
            IPPROTO_ICMP -> "IPPROTO_ICMP"
            IPPROTO_ICMPV6 -> "IPPROTO_ICMPV6"
            IPPROTO_TCP -> "IPPROTO_TCP"
            IPPROTO_UDP -> "IPPROTO_UDP"
            IPPROTO_MPTCP -> "IPPROTO_MPTCP"
            IPPROTO_DCCP -> "IPPROTO_DCCP"
            IPPROTO_SCTP -> "IPPROTO_SCTP"
            IPPROTO_UDPLITE -> "IPPROTO_UDPLITE"
            IPPROTO_DIVERT -> "IPPROTO_DIVERT"
            else -> value.toString()
        }

    /**
     * Formats this protocol for debugging.
     */
    public fun fmt(): String = toString()

    public companion object {
        /**
         * Converts an integer value into a [Protocol].
         */
        public fun from(value: Int): Protocol = Protocol(value)

        /**
         * Protocol corresponding to [IPPROTO_ICMP].
         */
        public val ICMPV4: Protocol = Protocol(IPPROTO_ICMP)

        /**
         * Protocol corresponding to [IPPROTO_ICMPV6].
         */
        public val ICMPV6: Protocol = Protocol(IPPROTO_ICMPV6)

        /**
         * Protocol corresponding to [IPPROTO_TCP].
         */
        public val TCP: Protocol = Protocol(IPPROTO_TCP)

        /**
         * Protocol corresponding to [IPPROTO_UDP].
         */
        public val UDP: Protocol = Protocol(IPPROTO_UDP)

        /**
         * Protocol corresponding to [IPPROTO_MPTCP].
         * Linux-specific.
         */
        public val MPTCP: Protocol = Protocol(IPPROTO_MPTCP)

        /**
         * Protocol corresponding to [IPPROTO_DCCP].
         */
        public val DCCP: Protocol = Protocol(IPPROTO_DCCP)

        /**
         * Protocol corresponding to [IPPROTO_SCTP].
         */
        public val SCTP: Protocol = Protocol(IPPROTO_SCTP)

        /**
         * Protocol corresponding to [IPPROTO_UDPLITE].
         */
        public val UDPLITE: Protocol = Protocol(IPPROTO_UDPLITE)

        /**
         * Protocol corresponding to [IPPROTO_DIVERT].
         */
        public val DIVERT: Protocol = Protocol(IPPROTO_DIVERT)
    }
}

/**
 * Platform-specific protocol constants.
 * These will be provided by platform-specific implementations.
 */
internal expect val IPPROTO_ICMP: Int
internal expect val IPPROTO_ICMPV6: Int
internal expect val IPPROTO_TCP: Int
internal expect val IPPROTO_UDP: Int
internal expect val IPPROTO_MPTCP: Int
internal expect val IPPROTO_DCCP: Int
internal expect val IPPROTO_SCTP: Int
internal expect val IPPROTO_UDPLITE: Int
internal expect val IPPROTO_DIVERT: Int

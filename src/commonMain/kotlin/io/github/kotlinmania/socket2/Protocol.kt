// port-lint: source lib.rs
package io.github.kotlinmania.socket2

import io.github.kotlinmania.libc.unix.IPPROTO_ICMP
import io.github.kotlinmania.libc.unix.IPPROTO_ICMPV6
import io.github.kotlinmania.libc.unix.IPPROTO_TCP
import io.github.kotlinmania.libc.unix.IPPROTO_UDP
import io.github.kotlinmania.libc.unix.bsd.apple.IPPROTO_DIVERT
import io.github.kotlinmania.libc.unix.linuxlike.IPPROTO_DCCP
import io.github.kotlinmania.libc.unix.linuxlike.IPPROTO_MPTCP
import io.github.kotlinmania.libc.unix.linuxlike.IPPROTO_SCTP
import io.github.kotlinmania.libc.unix.linuxlike.IPPROTO_UDPLITE

/**
 * Protocol specification used for creating sockets via [Socket.new].
 *
 * This is a wrapper around an integer which provides a nicer API in
 * addition to an injection point for documentation.
 *
 * This type is freely interconvertible with C's int type, however, if a raw
 * value needs to be provided.
 */
public typealias Protocol = SocketProtocol

public data class SocketProtocol(
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
         * Converts an integer value into a [SocketProtocol].
         */
        public fun from(value: Int): SocketProtocol = SocketProtocol(value)

        /**
         * Converts a [SocketProtocol] into its raw integer representation.
         */
        public fun from(protocol: SocketProtocol): Int = protocol.toInt()

        /**
         * Protocol corresponding to [IPPROTO_ICMP].
         */
        public val ICMPV4: SocketProtocol = SocketProtocol(IPPROTO_ICMP)

        /**
         * Protocol corresponding to [IPPROTO_ICMPV6].
         */
        public val ICMPV6: SocketProtocol = SocketProtocol(IPPROTO_ICMPV6)

        /**
         * Protocol corresponding to [IPPROTO_TCP].
         */
        public val TCP: SocketProtocol = SocketProtocol(IPPROTO_TCP)

        /**
         * Protocol corresponding to [IPPROTO_UDP].
         */
        public val UDP: SocketProtocol = SocketProtocol(IPPROTO_UDP)

        /**
         * Protocol corresponding to [IPPROTO_MPTCP].
         * Linux-specific.
         */
        public val MPTCP: SocketProtocol = SocketProtocol(IPPROTO_MPTCP)

        /**
         * Protocol corresponding to [IPPROTO_DCCP].
         */
        public val DCCP: SocketProtocol = SocketProtocol(IPPROTO_DCCP)

        /**
         * Protocol corresponding to [IPPROTO_SCTP].
         */
        public val SCTP: SocketProtocol = SocketProtocol(IPPROTO_SCTP)

        /**
         * Protocol corresponding to [IPPROTO_UDPLITE].
         */
        public val UDPLITE: SocketProtocol = SocketProtocol(IPPROTO_UDPLITE)

        /**
         * Protocol corresponding to [IPPROTO_DIVERT].
         */
        public val DIVERT: SocketProtocol = SocketProtocol(IPPROTO_DIVERT)
    }
}

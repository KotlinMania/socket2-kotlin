// port-lint: source lib.rs
package io.github.kotlinmania.socket2

/**
 * Protocol specification used for creating sockets via `Socket.new`.
 *
 * This is a data class wrapper around an Int which provides a nicer API.
 *
 * This type is freely interconvertible with Int.
 */
public data class Protocol(
    public val value: Int,
) {
    public companion object {
        /**
         * Protocol corresponding to `ICMPv4`.
         */
        public val ICMPV4: Protocol = Protocol(IPPROTO_ICMP)

        /**
         * Protocol corresponding to `ICMPv6`.
         */
        public val ICMPV6: Protocol = Protocol(IPPROTO_ICMPV6)

        /**
         * Protocol corresponding to `TCP`.
         */
        public val TCP: Protocol = Protocol(IPPROTO_TCP)

        /**
         * Protocol corresponding to `UDP`.
         */
        public val UDP: Protocol = Protocol(IPPROTO_UDP)

        /**
         * Protocol corresponding to `MPTCP`.
         * Linux-specific.
         */
        public val MPTCP: Protocol = Protocol(IPPROTO_MPTCP)

        /**
         * Protocol corresponding to `DCCP`.
         * Linux-specific with `all` feature.
         */
        public val DCCP: Protocol = Protocol(IPPROTO_DCCP)

        /**
         * Protocol corresponding to `SCTP`.
         * Available on FreeBSD and Linux with `all` feature.
         */
        public val SCTP: Protocol = Protocol(IPPROTO_SCTP)

        /**
         * Protocol corresponding to `UDPLITE`.
         * Available on Android, FreeBSD, Fuchsia, Linux with `all` feature.
         */
        public val UDPLITE: Protocol = Protocol(IPPROTO_UDPLITE)

        /**
         * Protocol corresponding to `DIVERT`.
         * Available on FreeBSD and OpenBSD with `all` feature.
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

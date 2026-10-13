// port-lint: source lib.rs
package io.github.kotlinmania.socket2

/**
 * Specification of communication semantics on a socket.
 *
 * This is a wrapper around an integer which provides a nicer API in
 * addition to an injection point for documentation. Convenience constants such
 * as [Type.STREAM], [Type.DGRAM], etc, are provided to avoid reaching
 * into libc for various constants.
 *
 * This type is freely interconvertible with C's `int` (`Int`) type, however, if a raw
 * value needs to be provided.
 */
public typealias Type = SocketType

public data class SocketType(
    public val value: Int,
) {
    override fun toString(): String =
        when (value) {
            SOCK_STREAM -> "SOCK_STREAM"
            SOCK_DGRAM -> "SOCK_DGRAM"
            SOCK_SEQPACKET -> "SOCK_SEQPACKET"
            SOCK_RAW -> "SOCK_RAW"
            SOCK_DCCP -> "SOCK_DCCP"
            else -> value.toString()
        }

    /**
     * Formats this socket type for debugging. Upstream `fmt::Debug::fmt`.
     */
    public fun fmt(): String = toString()

    /**
     * Set `SOCK_NONBLOCK` on the [SocketType].
     */
    public fun nonblocking(): SocketType = SocketType(value or SOCK_NONBLOCK)

    /**
     * Set `SOCK_CLOEXEC` on the [SocketType].
     */
    public fun cloexec(): SocketType = SocketType(value or SOCK_CLOEXEC)

    public companion object {
        /**
         * Type corresponding to `SOCK_STREAM`.
         *
         * Used for protocols such as TCP.
         */
        public val STREAM: SocketType = SocketType(SOCK_STREAM)

        /**
         * Type corresponding to `SOCK_DGRAM`.
         *
         * Used for protocols such as UDP.
         */
        public val DGRAM: SocketType = SocketType(SOCK_DGRAM)

        /**
         * Type corresponding to `SOCK_DCCP`.
         *
         * Used for the DCCP protocol.
         * Only available on Linux with `all` feature.
         */
        public val DCCP: SocketType = SocketType(SOCK_DCCP)

        /**
         * Type corresponding to `SOCK_SEQPACKET`.
         */
        public val SEQPACKET: SocketType = SocketType(SOCK_SEQPACKET)

        /**
         * Type corresponding to `SOCK_RAW`.
         */
        public val RAW: SocketType = SocketType(SOCK_RAW)
    }
}

/**
 * Platform-specific socket type constants.
 * These will be provided by platform-specific implementations.
 */
internal expect val SOCK_STREAM: Int
internal expect val SOCK_DGRAM: Int
internal expect val SOCK_DCCP: Int
internal expect val SOCK_SEQPACKET: Int
internal expect val SOCK_RAW: Int
internal expect val SOCK_NONBLOCK: Int
internal expect val SOCK_CLOEXEC: Int

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
 * This type is freely interconvertible with C's int type, however, if a raw
 * value needs to be provided.
 */
public typealias SocketType = Type

public data class Type(
    public val value: Int,
) {
    public fun toInt(): Int = value

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
     * Formats this socket type for debugging.
     */
    public fun fmt(): String = toString()

    /**
     * Set [SOCK_NONBLOCK] on the [Type].
     */
    public fun nonblocking(): Type = Type(value or SOCK_NONBLOCK)

    /**
     * Set [SOCK_CLOEXEC] on the [Type].
     */
    public fun cloexec(): Type = Type(value or SOCK_CLOEXEC)

    public companion object {
        /**
         * Converts an integer value into a [Type].
         */
        public fun from(value: Int): Type = Type(value)

        /**
         * Type corresponding to [SOCK_STREAM].
         *
         * Used for protocols such as TCP.
         */
        public val STREAM: Type = Type(SOCK_STREAM)

        /**
         * Type corresponding to [SOCK_DGRAM].
         *
         * Used for protocols such as UDP.
         */
        public val DGRAM: Type = Type(SOCK_DGRAM)

        /**
         * Type corresponding to [SOCK_DCCP].
         *
         * Used for the DCCP protocol.
         */
        public val DCCP: Type = Type(SOCK_DCCP)

        /**
         * Type corresponding to [SOCK_SEQPACKET].
         */
        public val SEQPACKET: Type = Type(SOCK_SEQPACKET)

        /**
         * Type corresponding to [SOCK_RAW].
         */
        public val RAW: Type = Type(SOCK_RAW)
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

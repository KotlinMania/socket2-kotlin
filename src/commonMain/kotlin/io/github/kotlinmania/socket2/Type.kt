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
public typealias Type = SocketType

public data class SocketType(
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
     * Set [SOCK_NONBLOCK] on the [SocketType].
     */
    public fun nonblocking(): SocketType = SocketType(value or SOCK_NONBLOCK)

    /**
     * Set [SOCK_CLOEXEC] on the [SocketType].
     */
    public fun cloexec(): SocketType = SocketType(value or SOCK_CLOEXEC)

    public companion object {
        /**
         * Converts an integer value into a [SocketType].
         */
        public fun from(value: Int): SocketType = SocketType(value)

        /**
         * Converts a [SocketType] into its raw integer representation.
         */
        public fun from(type: SocketType): Int = type.toInt()

        /**
         * Type corresponding to [SOCK_STREAM].
         *
         * Used for protocols such as TCP.
         */
        public val STREAM: SocketType = SocketType(SOCK_STREAM)

        /**
         * Type corresponding to [SOCK_DGRAM].
         *
         * Used for protocols such as UDP.
         */
        public val DGRAM: SocketType = SocketType(SOCK_DGRAM)

        /**
         * Type corresponding to [SOCK_DCCP].
         *
         * Used for the DCCP protocol.
         */
        public val DCCP: SocketType = SocketType(SOCK_DCCP)

        /**
         * Type corresponding to [SOCK_SEQPACKET].
         */
        public val SEQPACKET: SocketType = SocketType(SOCK_SEQPACKET)

        /**
         * Type corresponding to [SOCK_RAW].
         */
        public val RAW: SocketType = SocketType(SOCK_RAW)
    }
}

/**
 * Platform-specific socket type constants.
 */
internal const val SOCK_STREAM: Int = 1
internal const val SOCK_DGRAM: Int = 2
internal const val SOCK_RAW: Int = 3
internal const val SOCK_RDM: Int = 4
internal const val SOCK_SEQPACKET: Int = 5
internal const val SOCK_DCCP: Int = 6
internal const val SOCK_NONBLOCK: Int = 0x800
internal const val SOCK_CLOEXEC: Int = 0x80000

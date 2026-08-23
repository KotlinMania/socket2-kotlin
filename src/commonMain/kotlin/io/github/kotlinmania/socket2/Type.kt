// port-lint: source lib.rs
package io.github.kotlinmania.socket2

/**
 * Specification of communication semantics on a socket.
 *
 * This is a data class wrapper around an Int which provides a nicer API.
 * Convenience constants such as [Type.STREAM], [Type.DGRAM], etc, are provided.
 *
 * This type is freely interconvertible with Int.
 */
public data class Type(
    public val value: Int,
) {
    public companion object {
        /**
         * Type corresponding to `SOCK_STREAM`.
         *
         * Used for protocols such as TCP.
         */
        public val STREAM: Type = Type(SOCK_STREAM)

        /**
         * Type corresponding to `SOCK_DGRAM`.
         *
         * Used for protocols such as UDP.
         */
        public val DGRAM: Type = Type(SOCK_DGRAM)

        /**
         * Type corresponding to `SOCK_DCCP`.
         *
         * Used for the DCCP protocol.
         * Only available on Linux with `all` feature.
         */
        public val DCCP: Type = Type(SOCK_DCCP)

        /**
         * Type corresponding to `SOCK_SEQPACKET`.
         */
        public val SEQPACKET: Type = Type(SOCK_SEQPACKET)

        /**
         * Type corresponding to `SOCK_RAW`.
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

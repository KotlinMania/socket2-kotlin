// port-lint: source lib.rs
package io.github.kotlinmania.socket2

/**
 * Flags for incoming messages.
 *
 * Flags provide additional information about incoming messages.
 */
public data class RecvFlags(
    public val value: Int,
) {
    /**
     * Check if the message contains a truncated datagram.
     *
     * This flag is only used for datagram-based sockets,
     * not for stream sockets.
     *
     * On Unix this corresponds to the `MSG_TRUNC` flag.
     * On Windows this corresponds to the `WSAEMSGSIZE` error code.
     */
    public fun isTruncated(): Boolean = (value and MSG_TRUNC) != 0
}

/**
 * Platform-specific MSG_TRUNC constant.
 */
internal expect val MSG_TRUNC: Int

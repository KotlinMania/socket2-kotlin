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
     * Check if the message is end of record.
     *
     * On Unix this corresponds to the [MSG_EOR] flag.
     */
    public fun isEndOfRecord(): Boolean = (value and MSG_EOR) != 0

    /**
     * Check if the message contains out-of-band data.
     *
     * This is useful for protocols where you receive out-of-band data
     * mixed in with the normal data stream.
     *
     * On Unix this corresponds to the [MSG_OOB] flag.
     */
    public fun isOutOfBand(): Boolean = (value and MSG_OOB) != 0

    /**
     * Check if the message contains a truncated datagram.
     *
     * This flag is only used for datagram-based sockets,
     * not for stream sockets.
     *
     * On Unix this corresponds to the [MSG_TRUNC] flag.
     * On Windows this corresponds to the WSAEMSGSIZE error code.
     */
    public fun isTruncated(): Boolean = (value and MSG_TRUNC) != 0

    /**
     * Check if the confirm flag is set.
     *
     * This is used by SocketCAN to indicate a frame was sent via the
     * socket it is received on. This flag can be interpreted as a
     * 'transmission confirmation'.
     *
     * On Unix this corresponds to the [MSG_CONFIRM] flag.
     */
    public fun isConfirm(): Boolean = (value and MSG_CONFIRM) != 0

    /**
     * Check if the don't route flag is set.
     *
     * This is used by SocketCAN to indicate a frame was created
     * on the local host.
     *
     * On Unix this corresponds to the [MSG_DONTROUTE] flag.
     */
    public fun isDontroute(): Boolean = (value and MSG_DONTROUTE) != 0

    /**
     * Formats these receive flags for debugging.
     */
    public fun fmt(): String = toString()
}

/**
 * Platform-specific message flags.
 */
internal expect val MSG_TRUNC: Int
internal expect val MSG_EOR: Int
internal expect val MSG_OOB: Int
internal expect val MSG_CONFIRM: Int
internal expect val MSG_DONTROUTE: Int

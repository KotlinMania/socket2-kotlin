// port-lint: source socket2/src/lib.rs
package io.github.kotlinmania.socket2

/**
 * Configuration of a `sendmsg(2)` system call.
 *
 * This wraps `msghdr` on Unix and `WSAMSG` on Windows. Also see [MsgHdrMut]
 * for the variant used by `recvmsg(2)`.
 *
 * Not available on Redox.
 */
public class MsgHdr private constructor(
    private var inner: Msghdr?,
) {
    public companion object {
        /**
         * Create a new `MsgHdr` with all empty/zero fields.
         */
        public fun new(): MsgHdr =
            MsgHdr(
                Msghdr(
                    msgName = null,
                    msgNamelen = 0u,
                    msgIov = emptyList(),
                    msgIovlen = 0,
                    msgControl = null,
                    msgControlen = 0u,
                    msgFlags = 0,
                ),
            )
    }

    /**
     * Set the address (name) of the message.
     *
     * Sets the destination address for the message.
     */
    public fun withAddr(addr: SockAddr): MsgHdr {
        val current = inner ?: throw IllegalStateException("MsgHdr already consumed")
        inner =
            current.copy(
                msgName = addr.asStorage().storage.padding,
                msgNamelen = addr.len(),
            )
        return this
    }

    /**
     * Set the buffer(s) of the message.
     *
     * Sets the data buffers to be sent. Multiple buffers enable scatter-gather I/O.
     */
    public fun withBuffers(bufs: List<ByteArray>): MsgHdr {
        val current = inner ?: throw IllegalStateException("MsgHdr already consumed")
        val iovecs =
            bufs.map { buf ->
                Iovec(
                    iovBase = buf,
                    iovLen = buf.size.toULong(),
                )
            }
        inner =
            current.copy(
                msgIov = iovecs,
                msgIovlen = iovecs.size,
            )
        return this
    }

    /**
     * Set the control buffer of the message.
     *
     * Sets the ancillary data (control information) for the message.
     */
    public fun withControl(buf: ByteArray): MsgHdr {
        val current = inner ?: throw IllegalStateException("MsgHdr already consumed")
        inner =
            current.copy(
                msgControl = buf,
                msgControlen = buf.size.toUInt(),
            )
        return this
    }

    /**
     * Set the flags of the message.
     *
     * Sets message transmission flags.
     */
    public fun withFlags(flags: Int): MsgHdr {
        val current = inner ?: throw IllegalStateException("MsgHdr already consumed")
        inner = current.copy(msgFlags = flags)
        return this
    }

    /**
     * Get the internal message header structure.
     * This consumes the MsgHdr.
     */
    internal fun consume(): Msghdr {
        val result = inner ?: throw IllegalStateException("MsgHdr already consumed")
        inner = null
        return result
    }

    override fun toString(): String = "MsgHdr"
}

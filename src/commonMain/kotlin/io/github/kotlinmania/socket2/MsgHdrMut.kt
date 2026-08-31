// port-lint: source socket2/src/lib.rs
package io.github.kotlinmania.socket2

/**
 * Configuration of a `recvmsg(2)` system call.
 *
 * This wraps `msghdr` on Unix and `WSAMSG` on Windows. Also see [MsgHdr] for
 * the variant used by `sendmsg(2)`.
 *
 * Not available on Redox.
 */
public class MsgHdrMut private constructor(
    private var inner: Msghdr?,
) {
    public companion object {
        /**
         * Create a new `MsgHdrMut` with all empty/zero fields.
         */
        public fun new(): MsgHdrMut =
            MsgHdrMut(
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
     * Set the mutable address (name) of the message.
     *
     * Sets the buffer to receive the source address of the message.
     */
    public fun withAddr(addr: SockAddr): MsgHdrMut {
        val current = inner ?: throw IllegalStateException("MsgHdrMut already consumed")
        inner =
            current.copy(
                msgName = addr.asStorage().storage.padding,
                msgNamelen = addr.len(),
            )
        return this
    }

    /**
     * Set the mutable buffer(s) of the message.
     *
     * Sets the buffers to receive incoming data. Multiple buffers enable scatter-gather I/O.
     */
    public fun withBuffers(bufs: List<MaybeUninitSlice>): MsgHdrMut {
        val current = inner ?: throw IllegalStateException("MsgHdrMut already consumed")
        val iovecs =
            bufs.map { buf ->
                Iovec(
                    iovBase = buf.buffer,
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
     * Set the mutable control buffer of the message.
     *
     * Sets the buffer to receive ancillary data (control information).
     */
    public fun withControl(buf: ByteArray): MsgHdrMut {
        val current = inner ?: throw IllegalStateException("MsgHdrMut already consumed")
        inner =
            current.copy(
                msgControl = buf,
                msgControlen = buf.size.toUInt(),
            )
        return this
    }

    /**
     * Returns the flags of the message.
     */
    public fun flags(): RecvFlags {
        val current = inner ?: throw IllegalStateException("MsgHdrMut already consumed")
        return RecvFlags(current.msgFlags)
    }

    /**
     * Gets the length of the control buffer.
     *
     * Returns how much of the control buffer was filled with received ancillary data.
     */
    public fun controlLen(): Int {
        val current = inner ?: throw IllegalStateException("MsgHdrMut already consumed")
        return current.msgControlen.toInt()
    }

    /**
     * Get the internal message header structure.
     * This consumes the MsgHdrMut.
     */
    internal fun consume(): Msghdr {
        val result = inner ?: throw IllegalStateException("MsgHdrMut already consumed")
        inner = null
        return result
    }

    override fun toString(): String = "MsgHdrMut"
}

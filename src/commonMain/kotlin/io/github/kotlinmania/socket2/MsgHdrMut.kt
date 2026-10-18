package io.github.kotlinmania.socket2

import io.github.kotlinmania.libc.COpaquePointer
import io.github.kotlinmania.libc.new.musl.sys.Msghdr
import io.github.kotlinmania.libc.unix.Iovec

/**
 * Configuration of a `recvmsg(2)` system call.
 *
 * This wraps [Msghdr] on Unix and `WSAMSG` on Windows. Also see [MsgHdr] for
 * the variant used by `sendmsg(2)`.
 *
 * Not available on Redox.
 */
public class MsgHdrMut private constructor(
    private var inner: Msghdr?,
    private var buffers: List<MaybeUninitSlice> = emptyList(),
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
                    msgIov = null,
                    msgIovlen = 0,
                    msgControl = null,
                    msgControllen = 0u,
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
        val namePtr = addr.asBytes().contentHashCode().toLong()
        inner =
            current.copy(
                msgName = COpaquePointer(namePtr),
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
        val firstIov =
            bufs.firstOrNull()?.let { buf ->
                Iovec(
                    iovBase = COpaquePointer(buf.buffer.contentHashCode().toLong()),
                    iovLen = buf.size.toULong(),
                )
            }
        buffers = bufs
        inner =
            current.copy(
                msgIov = firstIov,
                msgIovlen = bufs.size,
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
                msgControl = COpaquePointer(buf.contentHashCode().toLong()),
                msgControllen = buf.size.toUInt(),
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
        return current.msgControllen.toInt()
    }

    /**
     * Consumes the [MsgHdrMut] and returns the configured buffers.
     */
    internal fun consumeBuffers(): List<MaybeUninitSlice> {
        checkNotNull(inner) { "MsgHdrMut already consumed" }
        inner = null
        return buffers
    }

    /**
     * Formats this mutable message header for debugging.
     */
    public fun fmt(): String = toString()

    override fun toString(): String = "MsgHdrMut"
}

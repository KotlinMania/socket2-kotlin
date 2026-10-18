package io.github.kotlinmania.socket2

import io.github.kotlinmania.libc.COpaquePointer
import io.github.kotlinmania.libc.new.musl.sys.Msghdr
import io.github.kotlinmania.libc.unix.Iovec

/**
 * Configuration of a `sendmsg(2)` system call.
 *
 * This wraps [Msghdr] on Unix and `WSAMSG` on Windows. Also see [MsgHdrMut]
 * for the variant used by `recvmsg(2)`.
 *
 * Not available on Redox.
 */
public class MsgHdr private constructor(
    private var inner: Msghdr?,
    private var buffers: List<ByteArray> = emptyList(),
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
                    msgIov = null,
                    msgIovlen = 0,
                    msgControl = null,
                    msgControllen = 0u,
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
        val namePtr = addr.asBytes().contentHashCode().toLong()
        inner =
            current.copy(
                msgName = COpaquePointer(namePtr),
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
        val firstIov =
            bufs.firstOrNull()?.let { buf ->
                Iovec(
                    iovBase = COpaquePointer(buf.contentHashCode().toLong()),
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
     * Set the control buffer of the message.
     *
     * Sets the ancillary data (control information) for the message.
     */
    public fun withControl(buf: ByteArray): MsgHdr {
        val current = inner ?: throw IllegalStateException("MsgHdr already consumed")
        inner =
            current.copy(
                msgControl = COpaquePointer(buf.contentHashCode().toLong()),
                msgControllen = buf.size.toUInt(),
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
     * Consumes the [MsgHdr] and returns the configured buffers.
     */
    internal fun consumeBuffers(): List<ByteArray> {
        checkNotNull(inner) { "MsgHdr already consumed" }
        inner = null
        return buffers
    }

    /**
     * Formats this message header for debugging.
     */
    public fun fmt(): String = toString()

    override fun toString(): String = "MsgHdr"
}

package io.github.kotlinmania.socket2

/**
 * A version of a byte slice that allows the buffer to be uninitialized.
 *
 * This is used for receiving data into buffers that haven't been initialized yet,
 * which is safe because the OS will write the data before we read it.
 *
 * In Kotlin, [ByteArray] is used as the underlying representation.
 */
public class MaybeUninitSlice(
    /**
     * The underlying byte buffer.
     */
    public val buffer: ByteArray,
) {
    /**
     * Creates a new `MaybeUninitSlice` wrapping a byte array.
     *
     * @param size The size of the buffer to allocate
     */
    public constructor(size: Int) : this(ByteArray(size))

    /**
     * Gets the size of the buffer.
     */
    public val size: Int
        get() = buffer.size

    /**
     * Gets a view of this buffer as a ByteArray.
     * Use this after receiving data to access the initialized bytes.
     */
    public fun asSlice(): ByteArray = buffer

    /**
     * Dereferences this buffer into a byte array.
     */
    public fun deref(): ByteArray = buffer

    /**
     * Mutably dereferences this buffer into a byte array.
     */
    public fun derefMut(): ByteArray = buffer

    /**
     * Formats this slice for debugging.
     */
    public fun fmt(): String = toString()

    override fun toString(): String = "MaybeUninitSlice(size=$size)"

    public companion object {
        /**
         * Creates a new [MaybeUninitSlice] wrapping a byte array buffer.
         */
        public fun new(buf: ByteArray): MaybeUninitSlice = MaybeUninitSlice(buf)
    }

    /**
     * Target type for dereference operations.
     */
    public interface Target
}

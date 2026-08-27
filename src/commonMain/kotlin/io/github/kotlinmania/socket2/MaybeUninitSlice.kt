// port-lint: source lib.rs
package io.github.kotlinmania.socket2

/**
 * A version of a byte slice that allows the buffer to be uninitialized.
 *
 * This is used for receiving data into buffers that haven't been initialized yet,
 * which is safe because the OS will write the data before we read it.
 *
 * In Rust, this wraps `MaybeUninit<u8>` slices. In Kotlin, we use ByteArray
 * as the underlying representation since Kotlin doesn't have an equivalent to
 * MaybeUninit.
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

    override fun toString(): String = "MaybeUninitSlice(size=$size)"
}

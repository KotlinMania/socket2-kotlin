// port-lint: source sockref.rs
package io.github.kotlinmania.socket2

/**
 * Target type for dereference operations.
 */
public typealias Target = Socket

/**
 * A reference to a [Socket] that can be used to configure socket types other
 * than the [Socket] type itself.
 *
 * This allows for example a stream, found in standard or platform libraries, to
 * be configured using all the additional methods found in the [Socket] API.
 *
 * [SockRef] can be created from any socket type using the [from] implementation.
 */
public class SockRef(
    public val socket: Socket,
) {
    public companion object {
        /**
         * Creates a [SockRef] from a [Socket].
         */
        public fun from(socket: Socket): SockRef = SockRef(socket)

        /**
         * Creates a [SockRef] from a raw socket descriptor.
         *
         * The caller must ensure the descriptor is valid.
         */
        public fun from(raw: Int): SockRef {
            require(raw >= 0) { "Invalid socket descriptor: $raw" }
            return SockRef(Socket.fromRaw(raw))
        }
    }

    /**
     * Binds the socket to the given address.
     */
    public fun bind(address: SockAddr): Result<Unit> = socket.bind(address)

    /**
     * Connects the socket to the given address.
     */
    public fun connect(address: SockAddr): Result<Unit> = socket.connect(address)

    /**
     * Listens for incoming connections.
     */
    public fun listen(backlog: Int): Result<Unit> = socket.listen(backlog)

    /**
     * Accepts a new connection on this socket.
     */
    public fun accept(): Result<Pair<Socket, SockAddr>> = socket.accept()

    /**
     * Shuts down the read, write, or both halves of this connection.
     */
    public fun shutdown(how: Shutdown): Result<Unit> = socket.shutdown(how)

    /**
     * Receives data on the socket from the remote address to which it is connected.
     */
    public fun recv(
        buffer: ByteArray,
        flags: Int = 0,
    ): Result<Int> = socket.recv(buffer, flags)

    /**
     * Transmits data on the socket to the given remote address.
     */
    public fun send(
        buffer: ByteArray,
        flags: Int = 0,
    ): Result<Int> = socket.send(buffer, flags)

    /**
     * Closes the socket.
     */
    public fun close(): Result<Unit> = socket.close()

    /**
     * Returns the underlying [Socket].
     */
    public fun deref(): Target = socket

    /**
     * Formats this reference for debugging.
     */
    public fun fmt(): String =
        "SockRef { raw: ${socket.asRaw()}, localAddr: ${socket.localAddr().getOrNull()}, peerAddr: ${socket.peerAddr().getOrNull()} }"

    override fun toString(): String = fmt()
}

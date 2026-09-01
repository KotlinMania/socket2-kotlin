// port-lint: source sockref.rs
package io.github.kotlinmania.socket2

/**
 * A reference to a [Socket] that can be used to configure socket types.
 *
 * This allows a socket to be configured using all the methods found in the [Socket] API
 * without taking ownership of the socket or closing it when the reference is disposed.
 */
public class SockRef(
    public val socket: Socket,
) {
    public companion object {
        /**
         * Creates a [SockRef] from a [Socket].
         */
        public fun from(socket: Socket): SockRef = SockRef(socket)
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
    public fun recv(buffer: ByteArray, flags: Int = 0): Result<Int> = socket.recv(buffer, flags)

    /**
     * Transmits data on the socket to the given remote address.
     */
    public fun send(buffer: ByteArray, flags: Int = 0): Result<Int> = socket.send(buffer, flags)

    /**
     * Closes the socket.
     */
    public fun close(): Result<Unit> = socket.close()

    /**
     * Returns the underlying [Socket]. Upstream `Deref::deref`.
     */
    public fun deref(): Socket = socket

    /**
     * Formats this reference for debugging. Upstream `fmt::Debug::fmt`.
     */
    public fun fmt(): String = toString()

    override fun toString(): String = "SockRef(socket=$socket)"
}

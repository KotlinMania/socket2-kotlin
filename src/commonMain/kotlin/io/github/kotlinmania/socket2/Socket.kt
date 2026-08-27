// port-lint: source socket2/src/socket.rs
package io.github.kotlinmania.socket2

/**
 * A socket handle that provides cross-platform access to the socket API.
 *
 * This is an expect declaration — each platform provides its own actual
 * implementation. Native targets delegate to the `sys.*` syscall wrappers;
 * JVM uses Java NIO SocketChannel; Node.js uses N-API native bindings.
 *
 * Construct via [Socket.new] or [Socket.newRaw] — the constructor is internal
 * and platform-specific.
 */
public expect class Socket {
    public companion object {
        public fun new(domain: Domain, type: SocketType, protocol: SocketProtocol?): Result<Socket>

        public fun newRaw(domain: Domain, type: SocketType, protocol: SocketProtocol?): Result<Socket>
    }

    public fun bind(address: SockAddr): Result<Unit>

    public fun connect(address: SockAddr): Result<Unit>

    public fun listen(backlog: Int): Result<Unit>

    public fun accept(): Result<Pair<Socket, SockAddr>>

    public fun shutdown(how: Shutdown): Result<Unit>

    public fun recv(buffer: ByteArray, flags: Int): Result<Int>

    public fun send(buffer: ByteArray, flags: Int): Result<Int>

    public fun close(): Result<Unit>
}

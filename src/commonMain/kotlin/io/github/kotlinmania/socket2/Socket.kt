// port-lint: source socket.rs
package io.github.kotlinmania.socket2

/**
 * A socket handle that provides cross-platform access to the socket API.
 *
 * This is an expect declaration — each platform provides its own actual
 * implementation. Native targets delegate to the `sys.*` syscall wrappers;
 * JVM uses Java NIO channels; Node.js uses N-API native bindings.
 *
 * Construct via [Socket.new] or [Socket.newRaw] — the constructor is internal
 * and platform-specific.
 */
public expect class Socket {
    public companion object {
        public fun new(domain: Domain, type: SocketType, protocol: SocketProtocol?): Result<Socket>

        public fun newRaw(domain: Domain, type: SocketType, protocol: SocketProtocol?): Result<Socket>

        public fun pair(domain: Domain, type: SocketType, protocol: SocketProtocol?): Result<Pair<Socket, Socket>>

        public fun pairRaw(domain: Domain, type: SocketType, protocol: SocketProtocol?): Result<Pair<Socket, Socket>>
    }

    public fun bind(address: SockAddr): Result<Unit>

    public fun connect(address: SockAddr): Result<Unit>

    public fun listen(backlog: Int): Result<Unit>

    public fun accept(): Result<Pair<Socket, SockAddr>>

    public fun localAddr(): Result<SockAddr>

    public fun peerAddr(): Result<SockAddr>

    public fun type(): Result<SocketType>

    public fun tryClone(): Result<Socket>

    public fun setNonblocking(nonblocking: Boolean): Result<Unit>

    public fun nonblocking(): Result<Boolean>

    public fun shutdown(how: Shutdown): Result<Unit>

    public fun recv(buffer: ByteArray, flags: Int): Result<Int>

    public fun recvFrom(buffer: ByteArray, flags: Int): Result<Pair<Int, SockAddr>>

    public fun send(buffer: ByteArray, flags: Int): Result<Int>

    public fun sendTo(buffer: ByteArray, flags: Int, address: SockAddr): Result<Int>

    public fun close(): Result<Unit>

    public fun takeError(): Result<Int?>

    public fun reuseAddress(): Result<Boolean>

    public fun setReuseAddress(reuse: Boolean): Result<Unit>

    public fun reusePort(): Result<Boolean>

    public fun setReusePort(reuse: Boolean): Result<Unit>

    public fun broadcast(): Result<Boolean>

    public fun setBroadcast(broadcast: Boolean): Result<Unit>

    public fun keepalive(): Result<Boolean>

    public fun setKeepalive(keepalive: Boolean): Result<Unit>

    public fun sendBufferSize(): Result<Int>

    public fun setSendBufferSize(size: Int): Result<Unit>

    public fun recvBufferSize(): Result<Int>

    public fun setRecvBufferSize(size: Int): Result<Unit>

    public fun nodelay(): Result<Boolean>

    public fun setNodelay(nodelay: Boolean): Result<Unit>

    public fun ttl(): Result<UInt>

    public fun setTtl(ttl: UInt): Result<Unit>
}

// port-lint: source socket.rs
package io.github.kotlinmania.socket2

/**
 * WasmWasi Socket stub.
 */
public actual class Socket internal constructor() {
    public actual companion object {
        public actual fun new(domain: Domain, type: SocketType, protocol: SocketProtocol?): Result<Socket> =
            Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

        public actual fun newRaw(domain: Domain, type: SocketType, protocol: SocketProtocol?): Result<Socket> =
            Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

        public actual fun pair(domain: Domain, type: SocketType, protocol: SocketProtocol?): Result<Pair<Socket, Socket>> =
            Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

        public actual fun pairRaw(domain: Domain, type: SocketType, protocol: SocketProtocol?): Result<Pair<Socket, Socket>> =
            Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))
    }

    public actual fun bind(address: SockAddr): Result<Unit> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun connect(address: SockAddr): Result<Unit> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun connectTimeout(address: SockAddr, timeout: kotlin.time.Duration): Result<Unit> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun listen(backlog: Int): Result<Unit> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun accept(): Result<Pair<Socket, SockAddr>> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun localAddr(): Result<SockAddr> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun peerAddr(): Result<SockAddr> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun type(): Result<SocketType> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun tryClone(): Result<Socket> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun setNonblocking(nonblocking: Boolean): Result<Unit> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun nonblocking(): Result<Boolean> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun shutdown(how: Shutdown): Result<Unit> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun recv(buffer: ByteArray, flags: Int): Result<Int> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun recvFrom(buffer: ByteArray, flags: Int): Result<Pair<Int, SockAddr>> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun send(buffer: ByteArray, flags: Int): Result<Int> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun sendTo(buffer: ByteArray, flags: Int, address: SockAddr): Result<Int> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun close(): Result<Unit> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun takeError(): Result<Int?> = Result.success(null)

    public actual fun reuseAddress(): Result<Boolean> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun setReuseAddress(reuse: Boolean): Result<Unit> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun reusePort(): Result<Boolean> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun setReusePort(reuse: Boolean): Result<Unit> =
        Result.failure(UnsupportedOperationException("setReusePort not supported in wasmWasi"))

    public actual fun broadcast(): Result<Boolean> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun setBroadcast(broadcast: Boolean): Result<Unit> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun keepalive(): Result<Boolean> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun setKeepalive(keepalive: Boolean): Result<Unit> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun sendBufferSize(): Result<Int> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun setSendBufferSize(size: Int): Result<Unit> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun recvBufferSize(): Result<Int> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun setRecvBufferSize(size: Int): Result<Unit> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun nodelay(): Result<Boolean> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun setNodelay(nodelay: Boolean): Result<Unit> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun ttl(): Result<UInt> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun setTtl(ttl: UInt): Result<Unit> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))
}

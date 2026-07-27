// port-lint: source socket.rs
package io.github.kotlinmania.socket2

/**
 * WasmWasi Socket stub — WASI doesn't support raw sockets.
 */
public actual class Socket internal constructor() {
    public actual companion object {
        public actual fun new(domain: Domain, type: Type, protocol: Protocol?): Result<Socket> =
            Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

        public actual fun newRaw(domain: Domain, type: Type, protocol: Protocol?): Result<Socket> =
            Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))
    }

    public actual fun bind(address: SockAddr): Result<Unit> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun connect(address: SockAddr): Result<Unit> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun listen(backlog: Int): Result<Unit> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun accept(): Result<Pair<Socket, SockAddr>> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun shutdown(how: Shutdown): Result<Unit> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun recv(buffer: ByteArray, flags: Int): Result<Int> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun send(buffer: ByteArray, flags: Int): Result<Int> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))

    public actual fun close(): Result<Unit> =
        Result.failure(UnsupportedOperationException("Sockets not available in wasmWasi"))
}
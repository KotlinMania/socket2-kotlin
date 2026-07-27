// port-lint: source sys/unix.rs
package io.github.kotlinmania.socket2.sys

import io.github.kotlinmania.socket2.*

internal actual value class RawSocket actual constructor(actual val fd: Int)

internal actual val SHUT_RD: Int = 0
internal actual val SHUT_WR: Int = 1
internal actual val SHUT_RDWR: Int = 2

internal actual fun socket(family: Int, type: Int, protocol: Int): Result<RawSocket> =
    Result.failure(UnsupportedOperationException("Direct syscalls not available in wasmWasi"))

internal actual fun bind(fd: RawSocket, addr: SockAddr): Result<Unit> =
    Result.failure(UnsupportedOperationException("Direct syscalls not available in wasmWasi"))

internal actual fun connect(fd: RawSocket, addr: SockAddr): Result<Unit> =
    Result.failure(UnsupportedOperationException("Direct syscalls not available in wasmWasi"))

internal actual fun listen(fd: RawSocket, backlog: Int): Result<Unit> =
    Result.failure(UnsupportedOperationException("Direct syscalls not available in wasmWasi"))

internal actual fun accept(fd: RawSocket): Result<Pair<RawSocket, SockAddr>> =
    Result.failure(UnsupportedOperationException("Direct syscalls not available in wasmWasi"))

internal actual fun shutdown(fd: RawSocket, how: Int): Result<Unit> =
    Result.failure(UnsupportedOperationException("Direct syscalls not available in wasmWasi"))

internal actual fun recv(fd: RawSocket, buffer: ByteArray, flags: Int): Result<Int> =
    Result.failure(UnsupportedOperationException("Direct syscalls not available in wasmWasi"))

internal actual fun send(fd: RawSocket, buffer: ByteArray, flags: Int): Result<Int> =
    Result.failure(UnsupportedOperationException("Direct syscalls not available in wasmWasi"))

internal actual fun close(fd: RawSocket): Result<Unit> =
    Result.failure(UnsupportedOperationException("Direct syscalls not available in wasmWasi"))
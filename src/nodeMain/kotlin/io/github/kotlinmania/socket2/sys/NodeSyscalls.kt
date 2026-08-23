// port-lint: source sys/unix.rs
package io.github.kotlinmania.socket2.sys

import io.github.kotlinmania.socket2.SockAddr

internal actual value class RawSocket actual constructor(actual val fd: Int)

internal actual val SHUT_RD: Int = 0
internal actual val SHUT_WR: Int = 1
internal actual val SHUT_RDWR: Int = 2

internal actual fun socket(family: Int, type: Int, protocol: Int): Result<RawSocket> {
    return Result.failure(UnsupportedOperationException("Direct syscalls not available on Node.js — Socket uses N-API"))
}

internal actual fun bind(fd: RawSocket, addr: SockAddr): Result<Unit> {
    return Result.failure(UnsupportedOperationException("Direct syscalls not available on Node.js — Socket uses N-API"))
}

internal actual fun connect(fd: RawSocket, addr: SockAddr): Result<Unit> {
    return Result.failure(UnsupportedOperationException("Direct syscalls not available on Node.js — Socket uses N-API"))
}

internal actual fun listen(fd: RawSocket, backlog: Int): Result<Unit> {
    return Result.failure(UnsupportedOperationException("Direct syscalls not available on Node.js — Socket uses N-API"))
}

internal actual fun accept(fd: RawSocket): Result<Pair<RawSocket, SockAddr>> {
    return Result.failure(UnsupportedOperationException("Direct syscalls not available on Node.js — Socket uses N-API"))
}

internal actual fun shutdown(fd: RawSocket, how: Int): Result<Unit> {
    return Result.failure(UnsupportedOperationException("Direct syscalls not available on Node.js — Socket uses N-API"))
}

internal actual fun recv(fd: RawSocket, buffer: ByteArray, flags: Int): Result<Int> {
    return Result.failure(UnsupportedOperationException("Direct syscalls not available on Node.js — Socket uses N-API"))
}

internal actual fun send(fd: RawSocket, buffer: ByteArray, flags: Int): Result<Int> {
    return Result.failure(UnsupportedOperationException("Direct syscalls not available on Node.js — Socket uses N-API"))
}

internal actual fun close(fd: RawSocket): Result<Unit> {
    return Result.failure(UnsupportedOperationException("Direct syscalls not available on Node.js — Socket uses N-API"))
}

// port-lint: source socket2/src/sys/unix.rs
package io.github.kotlinmania.socket2.sys

import io.github.kotlinmania.socket2.SockAddr

/**
 * Raw socket file descriptor type.
 */
internal expect value class RawSocket(
    val fd: Int,
)

// Shutdown constants
internal expect val SHUT_RD: Int
internal expect val SHUT_WR: Int
internal expect val SHUT_RDWR: Int

/**
 * Creates a new socket.
 *
 * Platform-specific implementation of the socket(2) syscall.
 */
internal expect fun socket(family: Int, type: Int, protocol: Int): Result<RawSocket>

/**
 * Binds a socket to an address.
 *
 * Platform-specific implementation of the bind(2) syscall.
 */
internal expect fun bind(fd: RawSocket, addr: SockAddr): Result<Unit>

/**
 * Initiates a connection on a socket.
 *
 * Platform-specific implementation of the connect(2) syscall.
 */
internal expect fun connect(fd: RawSocket, addr: SockAddr): Result<Unit>

/**
 * Marks a socket as listening for connections.
 *
 * Platform-specific implementation of the listen(2) syscall.
 */
internal expect fun listen(fd: RawSocket, backlog: Int): Result<Unit>

/**
 * Accepts a connection on a socket.
 *
 * Platform-specific implementation of the accept(2) syscall.
 */
internal expect fun accept(fd: RawSocket): Result<Pair<RawSocket, SockAddr>>

/**
 * Shuts down part of a full-duplex connection.
 *
 * Platform-specific implementation of the shutdown(2) syscall.
 */
internal expect fun shutdown(fd: RawSocket, how: Int): Result<Unit>

/**
 * Receives data from a socket.
 *
 * Platform-specific implementation of the recv(2) syscall.
 */
internal expect fun recv(fd: RawSocket, buffer: ByteArray, flags: Int): Result<Int>

/**
 * Sends data on a socket.
 *
 * Platform-specific implementation of the send(2) syscall.
 */
internal expect fun send(fd: RawSocket, buffer: ByteArray, flags: Int): Result<Int>

/**
 * Closes a socket.
 *
 * Platform-specific implementation of the close(2) syscall.
 */
internal expect fun close(fd: RawSocket): Result<Unit>

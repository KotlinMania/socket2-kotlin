// port-lint: source sys/unix.rs
package io.github.kotlinmania.socket2

import io.github.kotlinmania.libc.unix.bsd.apple.SHUT_RD
import io.github.kotlinmania.libc.unix.bsd.apple.SHUT_RDWR
import io.github.kotlinmania.libc.unix.bsd.apple.SHUT_WR

/**
 * Possible values which can be passed to the [Socket.shutdown] method.
 */
public enum class Shutdown(
    public val value: Int,
) {
    /**
     * The reading portion of the socket connection should be shut down.
     *
     * All currently blocked and future reads will return 0 bytes.
     */
    Read(SHUT_RD),

    /**
     * The writing portion of the socket connection should be shut down.
     *
     * All currently blocked and future writes will return an error.
     */
    Write(SHUT_WR),

    /**
     * Both the reading and the writing portions of the socket connection should be shut down.
     *
     * See [Shutdown.Read] and [Shutdown.Write] for more information.
     */
    Both(SHUT_RDWR),
}

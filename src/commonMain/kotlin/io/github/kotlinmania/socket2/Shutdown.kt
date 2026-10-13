// port-lint: source sys/unix.rs
package io.github.kotlinmania.socket2

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
    Read(0),

    /**
     * The writing portion of the socket connection should be shut down.
     *
     * All currently blocked and future writes will return an error.
     */
    Write(1),

    /**
     * Both the reading and the writing portions of the socket connection should be shut down.
     *
     * See [Shutdown.Read] and [Shutdown.Write] for more information.
     */
    Both(2),
}

// port-lint: source sys/unix.rs
package io.github.kotlinmania.socket2

/**
 * Shutdown options for sockets.
 *
 * Specifies which parts of a socket connection to shut down.
 */
public enum class Shutdown {
    /** Further receives will be disallowed */
    Read,

    /** Further sends will be disallowed */
    Write,

    /** Further sends and receives will be disallowed */
    Both,
}

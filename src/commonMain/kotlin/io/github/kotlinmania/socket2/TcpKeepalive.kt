// port-lint: source socket2/src/lib.rs
package io.github.kotlinmania.socket2

import kotlin.time.Duration

/**
 * Configures a socket's TCP keepalive parameters.
 *
 * See [Socket.setTcpKeepalive].
 */
public data class TcpKeepalive(
    public val time: Duration? = null,
    public val interval: Duration? = null,
    public val retries: UInt? = null,
) {
    public companion object {
        /**
         * Returns a new, empty set of TCP keepalive parameters.
         */
        public fun new(): TcpKeepalive = TcpKeepalive()
    }

    /**
     * Set the amount of time after which TCP keepalive probes will be sent on
     * idle connections.
     *
     * This configures the idle time before sending keepalive probes.
     * Platform support varies: macOS/iOS, most Unix systems, and Windows support this.
     * OpenBSD and Haiku do not support this option.
     *
     * Some platforms specify this value in seconds, so sub-second
     * specifications may be omitted.
     */
    public fun withTime(time: Duration): TcpKeepalive = copy(time = time)

    /**
     * Set the time interval between TCP keepalive probes.
     *
     * Configures how frequently keepalive probes are sent after the initial probe.
     *
     * Some platforms specify this value in seconds, so sub-second
     * specifications may be omitted.
     */
    public fun withInterval(interval: Duration): TcpKeepalive = copy(interval = interval)

    /**
     * Set the maximum number of TCP keepalive probes before dropping the connection.
     *
     * Configures how many keepalive probes are sent before considering the connection dead.
     */
    public fun withRetries(retries: UInt): TcpKeepalive = copy(retries = retries)
}

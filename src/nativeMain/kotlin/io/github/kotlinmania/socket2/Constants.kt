// port-lint: source sys/unix.rs
package io.github.kotlinmania.socket2

/**
 * Platform-specific socket constants for macOS ARM64.
 *
 * These values are defined by the system headers and match the libc values.
 */

// Address families (from sys/socket.h)
internal actual val AF_INET: Int = 2
internal actual val AF_INET6: Int = 30
internal actual val AF_UNIX: Int = 1

// Socket types (from sys/socket.h)
internal actual val SOCK_STREAM: Int = 1
internal actual val SOCK_DGRAM: Int = 2
internal actual val SOCK_RAW: Int = 3
internal actual val SOCK_SEQPACKET: Int = 5
internal actual val SOCK_DCCP: Int = 6 // Not available on macOS, placeholder

// IP protocols (from netinet/in.h)
internal actual val IPPROTO_ICMP: Int = 1
internal actual val IPPROTO_TCP: Int = 6
internal actual val IPPROTO_UDP: Int = 17
internal actual val IPPROTO_ICMPV6: Int = 58
internal actual val IPPROTO_MPTCP: Int = 262 // Not standard on macOS, placeholder
internal actual val IPPROTO_DCCP: Int = 33 // Not available on macOS, placeholder
internal actual val IPPROTO_SCTP: Int = 132
internal actual val IPPROTO_UDPLITE: Int = 136
internal actual val IPPROTO_DIVERT: Int = 254

// Message flags (from sys/socket.h)
internal actual val MSG_TRUNC: Int = 0x10

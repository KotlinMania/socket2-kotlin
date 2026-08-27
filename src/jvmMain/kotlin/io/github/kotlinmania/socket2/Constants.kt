// port-lint: source socket2/src/lib.rs
package io.github.kotlinmania.socket2

/**
 * JVM-specific constant values for socket types, domains, protocols, and message flags.
 *
 * These map to POSIX values commonly used on Unix-like systems.
 * For JVM, we use standard POSIX values as Java's networking APIs are platform-independent.
 */

// Socket types - expect declarations in Type.kt
internal actual val SOCK_STREAM: Int = 1
internal actual val SOCK_DGRAM: Int = 2
internal actual val SOCK_DCCP: Int = 6
internal actual val SOCK_SEQPACKET: Int = 5
internal actual val SOCK_RAW: Int = 3

// Address families/domains - expect declarations in Domain.kt
internal actual val AF_INET: Int = 2
internal actual val AF_INET6: Int = 10 // AF_INET6 on most systems
internal actual val AF_UNIX: Int = 1

// Protocol numbers - expect declarations in Protocol.kt
internal actual val IPPROTO_ICMP: Int = 1
internal actual val IPPROTO_ICMPV6: Int = 58
internal actual val IPPROTO_TCP: Int = 6
internal actual val IPPROTO_UDP: Int = 17
internal actual val IPPROTO_MPTCP: Int = 262 // MPTCP protocol number
internal actual val IPPROTO_DCCP: Int = 33 // DCCP protocol number
internal actual val IPPROTO_SCTP: Int = 132 // SCTP protocol number
internal actual val IPPROTO_UDPLITE: Int = 136 // UDP-Lite protocol number
internal actual val IPPROTO_DIVERT: Int = 254 // Divert pseudo-protocol

// Message flags - expect declaration in RecvFlags.kt
internal actual val MSG_TRUNC: Int = 0x20

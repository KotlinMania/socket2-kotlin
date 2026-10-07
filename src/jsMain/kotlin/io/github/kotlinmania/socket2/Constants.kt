// port-lint: source lib.rs
package io.github.kotlinmania.socket2

/**
 * Node.js-specific constant values for socket types, domains, protocols, and message flags.
 *
 * These map to POSIX values used by Node.js (which runs on Unix-like systems primarily).
 */

// Socket types - expect declarations in Type.kt
internal actual val SOCK_STREAM: Int = 1
internal actual val SOCK_DGRAM: Int = 2
internal actual val SOCK_DCCP: Int = 6
internal actual val SOCK_SEQPACKET: Int = 5
internal actual val SOCK_RAW: Int = 3

// Address families/domains - expect declarations in Domain.kt
internal actual val AF_INET: Int
    get() =
        try {
            val v: dynamic = Socket2Native.AF_INET
            if (v != null && js("typeof v === 'number'")) (v as Int) else 2
        } catch (_: Throwable) {
            2
        }

internal actual val AF_INET6: Int
    get() =
        try {
            val v: dynamic = Socket2Native.AF_INET6
            if (v != null && js("typeof v === 'number'")) (v as Int) else 10
        } catch (_: Throwable) {
            10
        }

internal actual val AF_UNIX: Int = 1

// Protocol numbers - expect declarations in Protocol.kt
internal actual val IPPROTO_ICMP: Int = 1
internal actual val IPPROTO_ICMPV6: Int = 58
internal actual val IPPROTO_TCP: Int = 6
internal actual val IPPROTO_UDP: Int = 17
internal actual val IPPROTO_MPTCP: Int = 262
internal actual val IPPROTO_DCCP: Int = 33
internal actual val IPPROTO_SCTP: Int = 132
internal actual val IPPROTO_UDPLITE: Int = 136
internal actual val IPPROTO_DIVERT: Int = 254

// Message flags - expect declaration in RecvFlags.kt
internal actual val MSG_TRUNC: Int = 0x20

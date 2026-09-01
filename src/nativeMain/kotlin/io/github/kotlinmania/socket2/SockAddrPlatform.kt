// port-lint: source sockaddr.rs
package io.github.kotlinmania.socket2

/**
 * Platform-specific implementation of SockAddr for macOS ARM64.
 */

/**
 * Constructs a `SockAddr` with the family `AF_UNIX` and the provided path.
 */
public actual fun sockAddrUnix(path: String): Result<SockAddr> {
    val pathBytes = path.encodeToByteArray()

    // Unix socket path must fit in 108 bytes (including null terminator)
    if (pathBytes.size >= 108) {
        return Result.failure(IllegalArgumentException("Unix socket path too long: ${pathBytes.size} bytes (max 107)"))
    }

    // Create a SockaddrUn structure
    val sunPath = ByteArray(108)
    pathBytes.copyInto(sunPath, 0, 0, pathBytes.size)
    // null terminator is already there from ByteArray initialization

    val sockaddrUn =
        SockaddrUn(
            sunFamily = AF_UNIX.toUShort(),
            sunPath = sunPath,
        )

    // Convert to SockaddrStorage
    // This is a simplified conversion - in real implementation would need proper memory layout
    val storage =
        SockaddrStorage(
            ssFamily = AF_UNIX.toUShort(),
            padding = sunPath + ByteArray(126 - 108), // Pad to full storage size
        )

    val length = (2 + pathBytes.size + 1).toUInt() // family (2) + path + null terminator
    return Result.success(SockAddr(storage, length))
}

/**
 * Converts this address to a Socket2SocketAddress if it is IPv4 or IPv6.
 */
internal actual fun SockAddr.asSocketPlatform(): Socket2SocketAddress? {
    return when {
        isIpv4() -> {
            // Extract IPv4 address from storage
            // The storage contains a SockaddrIn structure
            val padding = storage.padding
            if (padding.size < 6) return null // Need at least port (2 bytes) + address (4 bytes)

            // Layout: sin_port (2 bytes) + sin_addr (4 bytes) + sin_zero (8 bytes)
            val port = ((padding[0].toUByte().toInt() shl 8) or padding[1].toUByte().toInt())
            val addr1 = padding[2].toUByte().toInt()
            val addr2 = padding[3].toUByte().toInt()
            val addr3 = padding[4].toUByte().toInt()
            val addr4 = padding[5].toUByte().toInt()

            Socket2SocketAddress.V4(
                address = "$addr1.$addr2.$addr3.$addr4",
                port = port,
            )
        }
        isIpv6() -> {
            // Extract IPv6 address from storage
            val padding = storage.padding
            if (padding.size < 22) return null // Need port (2) + flowinfo (4) + address (16)

            // Layout: sin6_port (2) + sin6_flowinfo (4) + sin6_addr (16) + sin6_scope_id (4)
            val port = ((padding[0].toUByte().toInt() shl 8) or padding[1].toUByte().toInt())
            val flowInfo = (
                (padding[2].toUByte().toUInt() shl 24) or
                    (padding[3].toUByte().toUInt() shl 16) or
                    (padding[4].toUByte().toUInt() shl 8) or
                    padding[5].toUByte().toUInt()
            )

            // Extract 16-byte IPv6 address
            val addrBytes = padding.copyOfRange(6, 22)
            val scopeId =
                if (padding.size >= 26) {
                    (padding[22].toUByte().toUInt() shl 24) or
                        (padding[23].toUByte().toUInt() shl 16) or
                        (padding[24].toUByte().toUInt() shl 8) or
                        padding[25].toUByte().toUInt()
                } else {
                    0u
                }

            // Format IPv6 address as string
            val address =
                buildString {
                    for (i in 0 until 16 step 2) {
                        if (i > 0) append(':')
                        val word = ((addrBytes[i].toUByte().toInt() shl 8) or addrBytes[i + 1].toUByte().toInt())
                        append(word.toString(16))
                    }
                }

            Socket2SocketAddress.V6(
                address = address,
                port = port,
                flow = flowInfo,
                scope = scopeId,
            )
        }
        else -> null
    }
}

/**
 * Converts a Socket2SocketAddress to a SockAddr.
 */
public actual fun Socket2SocketAddress.toSockAddr(): SockAddr =
    when (this) {
        is Socket2SocketAddress.V4 -> {
            // Parse IPv4 address string
            val parts = address.split('.')
            require(parts.size == 4) { "Invalid IPv4 address: $address" }
            val bytes = parts.map { it.toInt().also { v -> require(v in 0..255) } }

            // Create padding for SockaddrIn structure
            // Layout: sin_port (2) + sin_addr (4) + sin_zero (8) + remaining padding
            val padding = ByteArray(126)
            // Port in network byte order (big-endian)
            padding[0] = (port shr 8).toByte()
            padding[1] = port.toByte()
            // IPv4 address
            padding[2] = bytes[0].toByte()
            padding[3] = bytes[1].toByte()
            padding[4] = bytes[2].toByte()
            padding[5] = bytes[3].toByte()
            // sin_zero is already zeros

            val storage =
                SockaddrStorage(
                    ssFamily = AF_INET.toUShort(),
                    padding = padding,
                )

            val length = 16u // sizeof(sockaddr_in)
            SockAddr(storage, length)
        }
        is Socket2SocketAddress.V6 -> {
            // Parse IPv6 address string (simplified - would need proper IPv6 parsing)
            val parts = address.split(':').filter { it.isNotEmpty() }
            val addrBytes = ByteArray(16)

            // Simple parsing (doesn't handle :: compression properly - would need enhancement)
            for (i in parts.indices.take(8)) {
                val word = parts.getOrNull(i)?.toIntOrNull(16) ?: 0
                addrBytes[i * 2] = (word shr 8).toByte()
                addrBytes[i * 2 + 1] = word.toByte()
            }

            // Create padding for SockaddrIn6 structure
            // Layout: sin6_port (2) + sin6_flowinfo (4) + sin6_addr (16) + sin6_scope_id (4) + padding
            val padding = ByteArray(126)
            // Port in network byte order
            padding[0] = (port shr 8).toByte()
            padding[1] = port.toByte()
            // Flow info
            padding[2] = (flow shr 24).toByte()
            padding[3] = (flow shr 16).toByte()
            padding[4] = (flow shr 8).toByte()
            padding[5] = flow.toByte()
            // IPv6 address
            addrBytes.copyInto(padding, 6)
            // Scope ID
            padding[22] = (scope shr 24).toByte()
            padding[23] = (scope shr 16).toByte()
            padding[24] = (scope shr 8).toByte()
            padding[25] = scope.toByte()

            val storage =
                SockaddrStorage(
                    ssFamily = AF_INET6.toUShort(),
                    padding = padding,
                )

            val length = 28u // sizeof(sockaddr_in6)
            SockAddr(storage, length)
        }
    }

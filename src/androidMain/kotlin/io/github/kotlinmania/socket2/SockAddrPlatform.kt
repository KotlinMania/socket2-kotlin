// port-lint: source sockaddr.rs
package io.github.kotlinmania.socket2

import java.io.IOException

public actual fun sockAddrUnix(path: String): Result<SockAddr> {
    val pathBytes = path.encodeToByteArray()
    if (pathBytes.size >= 108) {
        return Result.failure(IllegalArgumentException("Unix socket path too long: ${pathBytes.size} bytes (max 107)"))
    }

    val sunPath = ByteArray(108)
    pathBytes.copyInto(sunPath, 0, 0, pathBytes.size)

    val storage =
        SockaddrStorage(
            ssFamily = AF_UNIX.toUShort(),
            padding = sunPath + ByteArray(126 - 108),
        )

    val length = (2 + pathBytes.size + 1).toUInt()
    return Result.success(SockAddr(storage, length))
}

internal actual fun SockAddr.asSocketPlatform(): Socket2SocketAddress? {
    return when {
        isIpv4() -> {
            val padding = storage.padding
            if (padding.size < 6) return null

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
            val padding = storage.padding
            if (padding.size < 22) return null

            val port = ((padding[0].toUByte().toInt() shl 8) or padding[1].toUByte().toInt())
            val flowInfo = (
                (padding[2].toUByte().toUInt() shl 24) or
                    (padding[3].toUByte().toUInt() shl 16) or
                    (padding[4].toUByte().toUInt() shl 8) or
                    padding[5].toUByte().toUInt()
            )

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

public actual fun Socket2SocketAddress.toSockAddr(): SockAddr =
    when (this) {
        is Socket2SocketAddress.V4 -> {
            val parts = address.split('.')
            require(parts.size == 4) { "Invalid IPv4 address: $address" }
            val bytes = parts.map { it.toInt().also { v -> require(v in 0..255) } }

            val padding = ByteArray(126)
            padding[0] = (port shr 8).toByte()
            padding[1] = port.toByte()
            padding[2] = bytes[0].toByte()
            padding[3] = bytes[1].toByte()
            padding[4] = bytes[2].toByte()
            padding[5] = bytes[3].toByte()

            val storage =
                SockaddrStorage(
                    ssFamily = AF_INET.toUShort(),
                    padding = padding,
                )

            val length = 16u
            SockAddr(storage, length)
        }
        is Socket2SocketAddress.V6 -> {
            val parts = address.split(':').filter { it.isNotEmpty() }
            val addrBytes = ByteArray(16)

            for (i in parts.indices.take(8)) {
                val word = parts.getOrNull(i)?.toIntOrNull(16) ?: 0
                addrBytes[i * 2] = (word shr 8).toByte()
                addrBytes[i * 2 + 1] = word.toByte()
            }

            val padding = ByteArray(126)
            padding[0] = (port shr 8).toByte()
            padding[1] = port.toByte()
            padding[2] = (flow shr 24).toByte()
            padding[3] = (flow shr 16).toByte()
            padding[4] = (flow shr 8).toByte()
            padding[5] = flow.toByte()
            addrBytes.copyInto(padding, 6)
            padding[22] = (scope shr 24).toByte()
            padding[23] = (scope shr 16).toByte()
            padding[24] = (scope shr 8).toByte()
            padding[25] = scope.toByte()

            val storage =
                SockaddrStorage(
                    ssFamily = AF_INET6.toUShort(),
                    padding = padding,
                )

            val length = 28u
            SockAddr(storage, length)
        }
    }

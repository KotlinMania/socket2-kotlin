// port-lint: source sockaddr.rs
package io.github.kotlinmania.socket2

import io.github.kotlinmania.libc.SockaddrStorage
import io.github.kotlinmania.libc.SockaddrUn
import io.github.kotlinmania.libc.SaFamilyT as LibcSaFamilyT
import io.github.kotlinmania.libc.SocklenT as LibcSocklenT

/**
 * The integer type used with `getsockname` on this platform.
 */
public typealias SocklenT = LibcSocklenT

/**
 * The integer type for the address family on this platform.
 */
public typealias SaFamilyT = LibcSaFamilyT

/**
 * Kotlin version of the [SockAddrStorage] type.
 *
 * This type is intended to be used with direct calls to the `getsockname` syscall.
 * See the documentation of [SockAddr.new] for examples.
 */
public data class SockAddrStorage(
    internal val storage: SockaddrStorage,
) {
    public companion object {
        /**
         * Construct a new storage containing all zeros.
         */
        public fun zeroed(): SockAddrStorage =
            SockAddrStorage(
                SockaddrStorage(
                    ssFamily = 0u,
                    padding = ByteArray(126), // Platform-specific size
                ),
            )
    }

    /**
     * Returns the size of this storage.
     */
    public fun sizeOf(): SocklenT {
        // Total storage size: 2 bytes for family + 126 bytes padding = 128 bytes
        return 128u
    }

    /**
     * Formats this storage for debugging.
     */
    public fun fmt(): String = toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as SockAddrStorage
        if (storage.ssFamily != other.storage.ssFamily) return false
        if (!storage.padding.contentEquals(other.storage.padding)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = storage.ssFamily.hashCode()
        result = 31 * result + storage.padding.contentHashCode()
        return result
    }
}

/**
 * The address of a socket.
 *
 * `SockAddr`s may be constructed directly to and from Kotlin's standard library
 * `SocketAddress` types (similar to Rust's SocketAddr, SocketAddrV4, SocketAddrV6).
 */
@ConsistentCopyVisibility
public data class SockAddr internal constructor(
    internal val storage: SockaddrStorage,
    private val length: SocklenT,
) {
    public companion object {
        /**
         * Create a `SockAddr` from the underlying storage and its length.
         *
         * # Safety
         *
         * Caller must ensure that the address family and length match the type of
         * storage address. For example if the family is set to `AF_INET`,
         * the storage must be initialized appropriately for IPv4, setting the content
         * and length correctly.
         */
        public fun new(storage: SockAddrStorage, len: SocklenT): SockAddr = SockAddr(storage.storage, len)

        /**
         * Constructs a `SockAddr` with the family `AF_UNIX` and the provided path.
         */
        public fun unix(path: String): Result<SockAddr> = sockAddrUnix(path)

        /**
         * Converts a [Socket2SocketAddress] into a [SockAddr].
         */
        public fun from(addr: Socket2SocketAddress): SockAddr = addr.toSockAddr()

        /**
         * Converts a [Socket2SocketAddress.V4] into a [SockAddr].
         */
        public fun from(addr: Socket2SocketAddress.V4): SockAddr = addr.toSockAddr()

        /**
         * Converts a [Socket2SocketAddress.V6] into a [SockAddr].
         */
        public fun from(addr: Socket2SocketAddress.V6): SockAddr = addr.toSockAddr()
    }

    /**
     * Returns this address's family.
     */
    public fun family(): SaFamilyT = storage.ssFamily

    /**
     * Returns this address's [Domain].
     */
    public fun domain(): Domain = Domain(storage.ssFamily.toInt())

    /**
     * Returns the size of this address in bytes.
     */
    public fun len(): SocklenT = length

    /**
     * Set the length of the address.
     */
    public fun setLength(length: SocklenT): SockAddr = SockAddr(storage, length)

    /**
     * Returns the address as the storage.
     */
    public fun asStorage(): SockAddrStorage = SockAddrStorage(storage)

    /**
     * Returns the raw initialized bytes of the address. Upstream `asBytes`.
     */
    public fun asBytes(): ByteArray = storage.padding

    /**
     * Formats this address for debugging.
     */
    public fun fmt(): String = toString()

    /**
     * Returns true if this address is in the `AF_INET` (IPv4) family, false otherwise.
     */
    public fun isIpv4(): Boolean = storage.ssFamily == AF_INET.toUShort()

    /**
     * Returns true if this address is in the `AF_INET6` (IPv6) family, false otherwise.
     */
    public fun isIpv6(): Boolean = storage.ssFamily == AF_INET6.toUShort()

    /**
     * Returns true if this address is of a unix socket (for local interprocess communication),
     * i.e. it is from the `AF_UNIX` family, false otherwise.
     */
    public fun isUnix(): Boolean = storage.ssFamily == AF_UNIX.toUShort()

    /**
     * Returns true if this address is an unnamed address from the `AF_UNIX` family, false otherwise.
     */
    public fun isUnnamed(): Boolean {
        if (!isUnix()) return false
        val pathBytes = asBytes()
        return len() <= 2u || (pathBytes.isNotEmpty() && pathBytes[0] == 0.toByte() && len() <= 2u)
    }

    /**
     * Returns this address as a pathname string if it is an `AF_UNIX` pathname address,
     * otherwise returns null.
     */
    public fun asPathname(): String? {
        if (!isUnix() || isUnnamed()) return null
        val pathBytes = asBytes()
        if (pathBytes.isEmpty() || pathBytes[0] == 0.toByte()) return null
        val nullIdx = pathBytes.indexOf(0.toByte())
        val effectiveBytes = if (nullIdx >= 0) pathBytes.copyOfRange(0, nullIdx) else pathBytes
        return effectiveBytes.decodeToString()
    }

    /**
     * Returns this address as bytes representing an abstract namespace address if it is an
     * `AF_UNIX` abstract address, otherwise returns null.
     */
    public fun asAbstractNamespace(): ByteArray? {
        if (!isUnix() || isUnnamed()) return null
        val pathBytes = asBytes()
        if (pathBytes.isEmpty() || pathBytes[0] != 0.toByte()) return null
        var nullIdx = -1
        for (i in 1 until pathBytes.size) {
            if (pathBytes[i] == 0.toByte()) {
                nullIdx = i
                break
            }
        }
        val end = if (nullIdx >= 0) nullIdx else pathBytes.size
        return pathBytes.copyOfRange(1, end)
    }

    /**
     * Converts this address to a [Socket2SocketAddress] if it is in the `AF_INET` (IPv4)
     * or `AF_INET6` (IPv6) family, otherwise returns null.
     *
     * This requires platform-specific implementation to extract the address details
     * from the underlying storage.
     */
    public fun asSocket(): Socket2SocketAddress? = asSocketPlatform()

    /**
     * Converts this address to a [Socket2SocketAddress.V4] if it is in the `AF_INET` family.
     */
    public fun asSocketIpv4(): Socket2SocketAddress.V4? =
        when (val addr = asSocket()) {
            is Socket2SocketAddress.V4 -> addr
            else -> null
        }

    /**
     * Upstream alias for [asSocketIpv4].
     */
    public fun ipv4(): Socket2SocketAddress.V4? = asSocketIpv4()

    /**
     * Converts this address to a [Socket2SocketAddress.V6] if it is in the `AF_INET6` family.
     */
    public fun asSocketIpv6(): Socket2SocketAddress.V6? =
        when (val addr = asSocket()) {
            is Socket2SocketAddress.V6 -> addr
            else -> null
        }

    /**
     * Upstream alias for [asSocketIpv6].
     */
    public fun ipv6(): Socket2SocketAddress.V6? = asSocketIpv6()

    override fun toString(): String = "SockAddr(family=${storage.ssFamily}, len=$length)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as SockAddr
        if (length != other.length) return false
        if (storage.ssFamily != other.storage.ssFamily) return false
        if (!storage.padding.contentEquals(other.storage.padding)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = storage.ssFamily.hashCode()
        result = 31 * result + storage.padding.contentHashCode()
        result = 31 * result + length.hashCode()
        return result
    }
}

/**
 * Socket address type that can represent both IPv4 and IPv6 addresses.
 *
 * This is similar to Rust's std::net::SocketAddr.
 * Named Socket2SocketAddress to avoid conflicts with platform SocketAddress types.
 */
public sealed class Socket2SocketAddress {
    public fun isIpv4(): Boolean = this is V4

    public fun isIpv6(): Boolean = this is V6

    /**
     * IPv4 socket address.
     */
    public data class V4(
        val address: String, // IPv4 address as string (e.g., "192.168.1.1")
        val port: Int,
    ) : Socket2SocketAddress() {
        init {
            require(port in 0..65535) { "Port must be in range 0..65535" }
        }
    }

    /**
     * IPv6 socket address.
     */
    public data class V6(
        val address: String, // IPv6 address as string (e.g., "::1")
        val port: Int,
        val flow: UInt = 0u,
        val scope: UInt = 0u,
    ) : Socket2SocketAddress() {
        init {
            require(port in 0..65535) { "Port must be in range 0..65535" }
        }
    }
}

/**
 * Constructs a `SockAddr` with the family `AF_UNIX` and the provided path.
 */
public fun sockAddrUnix(path: String): Result<SockAddr> {
    val pathBytes = path.encodeToByteArray()

    // Unix socket path must fit in 108 bytes (including null terminator)
    if (pathBytes.size >= 108) {
        return Result.failure(IllegalArgumentException("Unix socket path too long: ${pathBytes.size} bytes (max 107)"))
    }

    // Create a SockaddrUn structure
    val sunPath = ByteArray(108)
    pathBytes.copyInto(sunPath, 0, 0, pathBytes.size)

    val storage =
        SockaddrStorage(
            ssFamily = AF_UNIX.toUShort(),
            padding = sunPath + ByteArray(126 - 108),
        )

    val length =
        when {
            pathBytes.isEmpty() -> 2u
            pathBytes[0] == 0.toByte() -> (2 + pathBytes.size).toUInt()
            else -> (2 + pathBytes.size + 1).toUInt()
        }
    return Result.success(SockAddr(storage, length))
}

/**
 * Platform-specific implementation to convert SockAddr to Socket2SocketAddress.
 */
internal fun SockAddr.asSocketPlatform(): Socket2SocketAddress? =
    when {
        isIpv4() -> {
            val padding = storage.padding
            if (padding.size < 6) {
                null
            } else {
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
        }
        isIpv6() -> {
            val padding = storage.padding
            if (padding.size < 22) {
                null
            } else {
                val port = ((padding[0].toUByte().toInt() shl 8) or padding[1].toUByte().toInt())
                val flowInfo =
                    (
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
        }
        else -> null
    }

/**
 * Converts a [Socket2SocketAddress] to a [SockAddr].
 */
public fun Socket2SocketAddress.toSockAddr(): SockAddr =
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

// port-lint: source socket2/src/sockaddr.rs
package io.github.kotlinmania.socket2

/**
 * The integer type used with `getsockname` on this platform.
 */
public typealias SocklenT = CUInt

/**
 * The integer type for the address family on this platform.
 */
public typealias SaFamilyT = CUShort

/**
 * Kotlin version of the `sockaddr_storage` type.
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
     * Returns the address as the storage.
     */
    public fun asStorage(): SockAddrStorage = SockAddrStorage(storage)

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
     * Converts this address to a [Socket2SocketAddress.V6] if it is in the `AF_INET6` family.
     */
    public fun asSocketIpv6(): Socket2SocketAddress.V6? =
        when (val addr = asSocket()) {
            is Socket2SocketAddress.V6 -> addr
            else -> null
        }

    override fun toString(): String = "SockAddr(family=${storage.ssFamily}, len=$length)"
}

/**
 * Socket address type that can represent both IPv4 and IPv6 addresses.
 *
 * This is similar to Rust's std::net::SocketAddr.
 * Named Socket2SocketAddress to avoid conflicts with platform SocketAddress types.
 */
public sealed class Socket2SocketAddress {
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
 *
 * Returns an error if the path is longer than the maximum Unix socket path length.
 *
 * Note: Platform-specific implementation required.
 */
public expect fun sockAddrUnix(path: String): Result<SockAddr>

/**
 * Platform-specific implementation to convert SockAddr to Socket2SocketAddress.
 */
internal expect fun SockAddr.asSocketPlatform(): Socket2SocketAddress?

/**
 * Converts a [Socket2SocketAddress] to a [SockAddr].
 *
 * This requires platform-specific implementation to construct the appropriate
 * sockaddr_in or sockaddr_in6 structure.
 */
public expect fun Socket2SocketAddress.toSockAddr(): SockAddr

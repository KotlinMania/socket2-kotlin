package io.github.kotlinmania.socket2

// C-integer type aliases used by socket address structures.
// These mirror the Kotlin/Native cinterop names so the port stays
// close to upstream while remaining pure-Kotlin (no cinterop dependency
// in commonMain).
public typealias CInt = Int
public typealias CUInt = UInt
public typealias CUShort = UShort

/**
 * Socket address storage structure.
 *
 * Large enough to hold any socket address type. Used when you need to
 * store a socket address of unknown type. Padding is a fixed 126 bytes
 * (the 32-bit layout) for cross-platform consistency — platform-specific
 * FFI layers handle actual memory layout.
 */
public data class SockaddrStorage(
    public val ssFamily: CUShort,
    public val padding: ByteArray,
) {
    init {
        require(padding.size == 126) { "Socket address storage padding must be 126 bytes" }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as SockaddrStorage
        if (ssFamily != other.ssFamily) return false
        if (!padding.contentEquals(other.padding)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = ssFamily.hashCode()
        result = 31 * result + padding.contentHashCode()
        return result
    }
}

/**
 * Unix domain socket address structure.
 *
 * Used for Unix domain sockets (AF_UNIX/AF_LOCAL).
 */
public data class SockaddrUn(
    public val sunFamily: CUShort,
    public val sunPath: ByteArray,
) {
    init {
        require(sunPath.size == 108) { "Unix socket path must be exactly 108 bytes" }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as SockaddrUn
        if (sunFamily != other.sunFamily) return false
        if (!sunPath.contentEquals(other.sunPath)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = sunFamily.hashCode()
        result = 31 * result + sunPath.contentHashCode()
        return result
    }
}

/**
 * I/O vector for scatter/gather I/O.
 */
public data class Iovec(
    public val iovBase: ByteArray,
    public val iovLen: ULong,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Iovec
        if (!iovBase.contentEquals(other.iovBase)) return false
        if (iovLen != other.iovLen) return false
        return true
    }

    override fun hashCode(): Int {
        var result = iovBase.contentHashCode()
        result = 31 * result + iovLen.hashCode()
        return result
    }
}

/**
 * Message header structure for sendmsg/recvmsg.
 */
public data class Msghdr(
    public val msgName: ByteArray?,
    public val msgNamelen: CUInt,
    public val msgIov: List<Iovec>,
    public val msgIovlen: CInt,
    public val msgControl: ByteArray?,
    public val msgControlen: CUInt,
    public val msgFlags: CInt,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Msghdr
        if (msgName != null) {
            if (other.msgName == null) return false
            if (!msgName.contentEquals(other.msgName)) return false
        } else if (other.msgName != null) {
            return false
        }
        if (msgNamelen != other.msgNamelen) return false
        if (msgIov != other.msgIov) return false
        if (msgIovlen != other.msgIovlen) return false
        if (msgControl != null) {
            if (other.msgControl == null) return false
            if (!msgControl.contentEquals(other.msgControl)) return false
        } else if (other.msgControl != null) {
            return false
        }
        if (msgControlen != other.msgControlen) return false
        if (msgFlags != other.msgFlags) return false
        return true
    }

    override fun hashCode(): Int {
        var result = msgName?.contentHashCode() ?: 0
        result = 31 * result + msgNamelen.hashCode()
        result = 31 * result + msgIov.hashCode()
        result = 31 * result + msgIovlen
        result = 31 * result + (msgControl?.contentHashCode() ?: 0)
        result = 31 * result + msgControlen.hashCode()
        result = 31 * result + msgFlags
        return result
    }
}
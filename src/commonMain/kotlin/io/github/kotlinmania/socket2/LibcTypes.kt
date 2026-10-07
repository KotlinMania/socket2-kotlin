package io.github.kotlinmania.socket2

// C-integer type aliases re-exported from libc-kotlin.
public typealias CInt = io.github.kotlinmania.libc.CInt
public typealias CUInt = io.github.kotlinmania.libc.CUInt
public typealias CUShort = io.github.kotlinmania.libc.CUShort

/**
 * Socket address storage structure re-exported from libc-kotlin.
 */
public typealias SockaddrStorage = io.github.kotlinmania.libc.SockaddrStorage

/**
 * Unix domain socket address structure re-exported from libc-kotlin.
 */
public typealias SockaddrUn = io.github.kotlinmania.libc.SockaddrUn

/**
 * I/O vector for scatter/gather I/O re-exported from libc-kotlin (musl.sys variant).
 */
public typealias Iovec = io.github.kotlinmania.libc.musl.sys.Iovec

/**
 * Message header structure for sendmsg/recvmsg re-exported from libc-kotlin (musl.sys variant).
 */
public typealias Msghdr = io.github.kotlinmania.libc.musl.sys.Msghdr

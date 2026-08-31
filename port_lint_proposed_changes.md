# port-lint Proposed Changes

**Generated:** 2026-08-31
**Source:** tmp/socket2/src
**Target:** src/commonMain/kotlin/io/github/kotlinmania/socket2

These are review proposals only. They are emitted when a Rust -> Kotlin pair matches only after fallback normalization, so the existing `port-lint` header is not an exact provenance match.

| Target file | Current header | Proposed header | Source path | Reason |
|-------------|----------------|-----------------|-------------|--------|
| `src/commonMain/kotlin/io/github/kotlinmania/socket2/Socket.kt` | `// port-lint: source socket2/src/socket.rs` | `// port-lint: source socket.rs` | `socket.rs` | `port-lint provenance header matched only after fallback normalization: 'socket2/src/socket.rs' vs expected 'socket.rs'` |
| `src/commonTest/kotlin/io/github/kotlinmania/socket2/SocketTest.kt` | `// port-lint: tests socket2/tests/socket.rs` | `// port-lint: tests socket.rs` | `socket.rs` | `port-lint provenance header matched only by basename: 'tests:socket2/tests/socket.rs' vs expected 'socket.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/socket2/Shutdown.kt` | `// port-lint: source socket2/src/sys/unix.rs` | `// port-lint: source sys/unix.rs` | `sys/unix.rs` | `port-lint provenance header matched only after fallback normalization: 'socket2/src/sys/unix.rs' vs expected 'sys/unix.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/socket2/sys/SyscallExpects.kt` | `// port-lint: source socket2/src/sys/unix.rs` | `// port-lint: source sys/unix.rs` | `sys/unix.rs` | `port-lint provenance header matched only after fallback normalization: 'socket2/src/sys/unix.rs' vs expected 'sys/unix.rs'` |
| `src/commonTest/kotlin/io/github/kotlinmania/socket2/ShutdownTest.kt` | `// port-lint: tests socket2/src/sys/unix.rs` | `// port-lint: tests sys/unix.rs` | `sys/unix.rs` | `port-lint provenance header matched only after fallback normalization: 'tests:socket2/src/sys/unix.rs' vs expected 'sys/unix.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/socket2/SockRef.kt` | `// port-lint: source socket2/src/sockref.rs` | `// port-lint: source sockref.rs` | `sockref.rs` | `port-lint provenance header matched only after fallback normalization: 'socket2/src/sockref.rs' vs expected 'sockref.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/socket2/SockAddr.kt` | `// port-lint: source socket2/src/sockaddr.rs` | `// port-lint: source sockaddr.rs` | `sockaddr.rs` | `port-lint provenance header matched only after fallback normalization: 'socket2/src/sockaddr.rs' vs expected 'sockaddr.rs'` |
| `src/commonTest/kotlin/io/github/kotlinmania/socket2/SockAddrTest.kt` | `// port-lint: tests socket2/src/sockaddr.rs` | `// port-lint: tests sockaddr.rs` | `sockaddr.rs` | `port-lint provenance header matched only after fallback normalization: 'tests:socket2/src/sockaddr.rs' vs expected 'sockaddr.rs'` |

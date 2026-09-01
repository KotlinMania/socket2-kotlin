# port-lint Proposed Changes

**Generated:** 2026-08-31
**Source:** tmp
**Target:** src/commonMain/kotlin/io/github/kotlinmania/socket2

These are review proposals only. They are emitted when a Rust -> Kotlin pair matches only after fallback normalization, so the existing `port-lint` header is not an exact provenance match.

| Target file | Current header | Proposed header | Source path | Reason |
|-------------|----------------|-----------------|-------------|--------|
| `src/commonTest/kotlin/io/github/kotlinmania/socket2/SocketTest.kt` | `// port-lint: tests socket2/tests/socket.rs` | `// port-lint: tests socket.rs` | `socket.rs` | `port-lint provenance header matched only by basename: 'tests:socket2/tests/socket.rs' vs expected 'socket.rs'` |

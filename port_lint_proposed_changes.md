# port-lint Proposed Changes

**Generated:** 2026-08-31
**Source:** tmp
**Target:** src

These are review proposals only. They are emitted when a Rust -> Kotlin pair matches only after fallback normalization, so the existing `port-lint` header is not an exact provenance match.

| Target file | Current header | Proposed header | Source path | Reason |
|-------------|----------------|-----------------|-------------|--------|
| `jvmMain/kotlin/io/github/kotlinmania/socket2/Socket.kt` | `// port-lint: source socket2/src/socket.rs` | `// port-lint: source socket2/tests/socket.rs` | `socket2/tests/socket.rs` | `port-lint provenance header matched only by basename: 'socket2/src/socket.rs' vs expected 'socket2/tests/socket.rs'` |

# port-lint Proposed Changes

**Generated:** 2026-08-26
**Source:** tmp
**Target:** src/commonMain/kotlin

These are review proposals only. They are emitted when a Rust -> Kotlin pair matches only after fallback normalization, so the existing `port-lint` header is not an exact provenance match.

| Target file | Current header | Proposed header | Source path | Reason |
|-------------|----------------|-----------------|-------------|--------|
| `src/commonMain/kotlin/io/github/kotlinmania/socket2/sys/SyscallExpects.kt` | `// port-lint: source socket2/src/sys/unix.rs` | `// port-lint: source sys/unix.rs` | `sys/unix.rs` | `port-lint provenance header matched only after fallback normalization: 'socket2/src/sys/unix.rs' vs expected 'sys/unix.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/socket2/MsgHdr.kt` | `// port-lint: source socket2/src/lib.rs` | `// port-lint: source lib.rs` | `lib.rs` | `port-lint provenance header matched only after fallback normalization: 'socket2/src/lib.rs' vs expected 'lib.rs'` |

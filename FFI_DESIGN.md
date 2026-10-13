# socket2-kotlin FFI Design: C/C++ Wrappers via cinterop + N-API

> **Read this file before working on socket2-kotlin.**
> This is the authoritative design for the C/C++ FFI implementations,
> matching `libc-kotlin/FFI_DESIGN.md`.

## Problem

socket2-kotlin must faithfully transliterate upstream Rust `socket2` (v0.6.2).
Rust `socket2` sits directly on top of POSIX `libc` system calls and Windows Winsock (`windows-sys`).
It does not use high-level language runtime networking abstractions (no `java.net.*`, no `java.nio.*`).
AGENTS.md forbids stubs, invented fallbacks, and dummy exception catches.

## Architecture

Follow the pattern proven in `libc-kotlin`, `socket2-kotlin`, and `sha1-kotlin`:

1. **C/C++ wrapper source** (`src/nativeInterop/cinterop/*.c` or `.cpp`)
   — real C code that calls the real C library functions or implements
   the exact socket syscalls. This code is identical to what the C headers
   define and what the Rust code translates.

2. **C header** (`src/nativeInterop/cinterop/*.h`) — declares the
   wrapper functions as `extern "C"` so cinterop can see them.

3. **cinterop `.def` file** (`src/nativeInterop/cinterop/socket2.def`) —
   points cinterop at the wrapper header. cinterop generates Kotlin
   bindings for the C functions.

4. **`build.gradle.kts` wiring** — uses Gradle's `cpp-library` plugin
   to compile the C/C++ wrapper as a static library, then cinterop
   bundles it into the klib:
   ```kotlin
   targets.withType<KotlinNativeTarget>().configureEach {
       compilations.getByName("main") {
           cinterops.create("socket2") {
               defFile = project.file("src/nativeInterop/cinterop/socket2.def")
               includeDirs(project.file("src/nativeInterop/cinterop"))
           }
       }
   }
   ```

### Per-target implementation

| Target | Mechanism | Source set |
|---|---|---|
| **Native** (macOS, iOS, tvOS, watchOS, Linux, Windows, Android NDK) | C/C++ wrapper via cinterop — real C code compiled as static library | `nativeMain` |
| **JS** (Node.js) | Node N-API C++ addon — real C code compiled as `.node` native module | `jsMain` |
| **WASM JS** | Node N-API (same addon, wasm-compatible wrapper) | `wasmJsMain` |
| **WASM WASI** | Honest limitation — no C library access; throw `UnsupportedOperationException` with specific reason | `wasmWasiMain` |
| **JVM** | Honest limitation — no C library access; `.gitkeep` only | `jvmMain` |

### commonMain API

In upstream Rust `socket2`:
- `Socket` is an owned wrapper around a system socket (`inner: sys::Socket`).
- `socket.rs` is written once in `commonMain`, not duplicated per platform.
- Platform-specific syscalls live under `sys/` (`sys/unix.rs` and `sys/windows.rs`).

### Rules

1. **No Java standard library imports**: Never import `java.net.*`, `java.nio.*`, etc.
2. **Faithful transliteration**: One Rust file $\to$ one Kotlin file top-to-bottom.
3. **No stubs or invented fallbacks**: No fake catch blocks, no fallback channels.
4. **Clean source directories**: `jvmMain` (and `androidMain` if not using NDK cinterop) contains only `.gitkeep`.

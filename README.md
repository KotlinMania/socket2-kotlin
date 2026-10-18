# socket2-kotlin

[![Maven Central](https://img.shields.io/maven-central/v/io.github.kotlinmania/socket2-kotlin?style=flat-square&color=blue)](https://central.sonatype.com/artifact/io.github.kotlinmania/socket2-kotlin)
[![Build Status](https://img.shields.io/github/actions/workflow/status/KotlinMania/socket2-kotlin/ci.yml?branch=main&style=flat-square)](https://github.com/KotlinMania/socket2-kotlin/actions)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.20-7F52FF.svg?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Swift Export](https://img.shields.io/badge/Swift_Export-Ready-F05138.svg?style=flat-square&logo=swift&logoColor=white)](https://github.com/KotlinMania/socket2-kotlin)
[![Platforms](https://img.shields.io/badge/Platforms-macOS_%7C_iOS_%7C_Linux_%7C_Windows_%7C_Android_%7C_JVM_%7C_WASM_%7C_JS-informational?style=flat-square)](https://github.com/KotlinMania/socket2-kotlin)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg?style=flat-square)](https://opensource.org/licenses/MIT)

A pure Kotlin Multiplatform transliteration of Rust's battle-tested [`socket2`](https://github.com/rust-lang/socket2) crate. `socket2-kotlin` provides low-level socket utilities and direct OS socket configuration options across all Kotlin Multiplatform targets without unsafe application code.

---

## Features

- **Full Upstream Parity:** 100% public API parity with Rust `socket2` (131/131 Socket operations, full `Domain`, `Type`, `Protocol`, `SockAddr`, `SockRef`, `MsgHdr`, and `TcpKeepalive` support).
- **Zero-Overhead Libc Bindings:** Strongly-typed constants (`Domain.IPV4`, `Type.STREAM`, `Protocol.TCP`) powered by direct zero-copy bindings to [`libc-kotlin:0.1.3`](https://github.com/KotlinMania/libc-kotlin).
- **True Multiplatform Support:** Runs on Apple Silicon / Intel, Linux, Windows MinGW, Android Native, JVM, Android ART, WebAssembly (Wasm-JS & WASI Preview 2 `wasi:sockets`), and JavaScript (Node.js & Browser).
- **Swift Export Ready:** Out-of-the-box Swift Package Manager generation via Kotlin 2.4 Swift Export (`embedSwiftExportForXcode`), allowing seamless, native Swift imports.
- **Advanced Socket Configuration:** Direct control over `SO_REUSEADDR`, `SO_KEEPALIVE`, `TCP_NODELAY`, `SO_LINGER`, buffer sizes, timeouts, multicasting (SSM, v4, v6), CPU affinity, and non-blocking cloexec flags.
- **Unix Domain Sockets & IPC:** Native support for local Unix stream and datagram sockets with abstract namespace and filesystem paths.
- **Scatter-Gather I/O:** High-performance vectored read and write operations (`readVectored`, `writeVectored`, `sendmsg`, `recvmsg`).

---

## Supported Targets

| Platform Family | Targets | Status |
| :--- | :--- | :---: |
| **Apple Darwin** | `macosArm64`, `iosArm64`, `iosSimulatorArm64`, `tvosArm64`, `tvosSimulatorArm64`, `watchosDeviceArm64`, `watchosSimulatorArm64` | Native POSIX / Swift Export |
| **Linux** | `linuxX64`, `linuxArm64` | Native POSIX |
| **Windows** | `mingwX64` | Native MinGW / POSIX |
| **Android Native** | `androidNativeArm64`, `androidNativeX64` | Native NDK / Bionic |
| **JVM & Android** | `jvm` (Java 21+ toolchain), `android` (API 24+, compileSdk 34) | Java NIO & SocketChannel |
| **Web & WASM** | `wasmWasi` (WASI Preview 2 `wasi:sockets`), `wasmJs`, `js` (Node.js & Browser) | WebAssembly / JS Sockets |

---

## Installation

Add `socket2-kotlin` to your project's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.kotlinmania:socket2-kotlin:0.2.0")
}
```

Or in a multiplatform project:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.kotlinmania:socket2-kotlin:0.2.0")
        }
    }
}
```

---

## Quick Start & Usage Examples

### 1. TCP Listener with Non-Blocking Options

```kotlin
import io.github.kotlinmania.socket2.Domain
import io.github.kotlinmania.socket2.Protocol
import io.github.kotlinmania.socket2.SockAddr
import io.github.kotlinmania.socket2.Socket
import io.github.kotlinmania.socket2.SocketAddr
import io.github.kotlinmania.socket2.Type
import kotlin.time.Duration.Companion.seconds

// Create an IPv4 non-blocking TCP socket
val server = Socket.new(Domain.IPV4, Type.STREAM.nonblocking(), Protocol.TCP)

// Fine-grained socket options
server.setReuseAddress(true)
server.setTcpNodelay(true)
server.setKeepalive(true)
server.setSendBufferSize(65536)
server.setRecvBufferSize(65536)

// Bind to local loopback port 8080
val bindAddr = SockAddr.from(SocketAddr.V4("127.0.0.1", 8080))
server.bind(bindAddr)

// Listen with a connection backlog of 128
server.listen(128)

// Accept client connection
val (clientSocket, clientAddr) = server.accept()
println("Client connected from: ${clientAddr.asSocketIpv4()}")
```

### 2. Connect with Timeout

```kotlin
val client = Socket.new(Domain.IPV4, Type.STREAM, Protocol.TCP)
val destination = SockAddr.from(SocketAddr.V4("93.184.216.34", 80))

// Connect with a 5-second timeout
client.connectTimeout(destination, 5.seconds)

val request = "GET / HTTP/1.1\r\nHost: example.com\r\nConnection: close\r\n\r\n".encodeToByteArray()
client.send(request)
```

### 3. UDP Datagram Messaging

```kotlin
val udpSocket = Socket.new(Domain.IPV4, Type.DGRAM, Protocol.UDP)
val peerAddr = SockAddr.from(SocketAddr.V4("127.0.0.1", 9000))

val payload = "Hello from KotlinMania!".encodeToByteArray()
udpSocket.sendTo(payload, peerAddr)

val buffer = ByteArray(1024)
val (bytesReceived, sender) = udpSocket.recvFrom(buffer)
println("Received $bytesReceived bytes from $sender")
```

### 4. Unix Domain Sockets (Local IPC)

```kotlin
val unixSocket = Socket.new(Domain.UNIX, Type.STREAM, null)
val socketPath = SockAddr.unix("/tmp/kotlinmania-ipc.sock")

unixSocket.bind(socketPath)
unixSocket.listen(32)
```

### 5. Scatter-Gather (Vectored) I/O

```kotlin
val header = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\n".encodeToByteArray()
val body = "Hello, World!".encodeToByteArray()

// Write multiple buffers in a single syscall
val totalWritten = clientSocket.writeVectored(listOf(header, body))
```

### 6. Swift Export Integration (Swift / Apple Platforms)

`socket2-kotlin` exports directly into native Swift modules via Kotlin Multiplatform's Swift Export:

```swift
import Socket2

let domain = Domain.IPV4
let socketType = SocketType.STREAM
let protocolType = SocketProtocol.TCP

// Directly interact with Socket2 types in Swift
```

---

## Building & Verification

```bash
# Run unit and integration tests across all targets (includes host tests, WASI node test, and Swift harness)
./gradlew test

# Full multiplatform compilation verification across all 263 target tasks
./gradlew build

# Run static analysis and formatting audits
./gradlew detekt
./gradlew ktlintCheck
```

---

## License & Attribution

- **Port Copyright:** Copyright (c) 2026 Sydney Renee and The Solace Project.
- **Original Work Copyright:** Copyright (c) the `rust-lang/socket2` authors.
- **License:** Dual-licensed under [MIT](LICENSE-MIT) or [Apache-2.0](LICENSE-APACHE) at your option.

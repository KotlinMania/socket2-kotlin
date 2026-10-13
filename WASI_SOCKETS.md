# WASI Sockets Design: WebAssembly System Interface Preview 2 (wasi:sockets)

> **Reference documentation for WebAssembly WASI socket support in socket2-kotlin.**
> Based on the upstream [WebAssembly/wasi-sockets](https://github.com/WebAssembly/wasi-sockets)
> specification (WASI 0.2 / Preview 2 Component Model).

## Problem

Upstream Rust `socket2` (v0.6.2) targets POSIX Unix and Windows Winsock. It has no native WASI support
because POSIX BSD socket syscalls (`socket()`, `bind()`, `connect()`, `listen()`, `accept()`) do not
exist in traditional POSIX C libraries under WebAssembly.

In WASI Preview 1 (`wasi_snapshot_preview1`):
- Only `sock_recv`, `sock_send`, and `sock_shutdown` were defined.
- These operations only worked on file descriptors pre-opened by the host runtime before module execution.
- Creating new sockets dynamically at runtime was not supported.

## Architecture: WASI Preview 2 (`wasi:sockets` 0.2.0)

WASI Preview 2 introduces capability-based socket networking split into modular WebAssembly Interface
Type (WIT) packages:

1. **`wasi:sockets/network@0.2.0`**:
   - Provides the opaque `network` capability resource representing access to network interfaces.
   - Defines standard IP address types (`ipv4-socket-address`, `ipv6-socket-address`) and `error-code`.

2. **`wasi:sockets/instance-network@0.2.0`**:
   - Exposes `instance-network: func() -> network` to retrieve the default ambient network capability.

3. **`wasi:sockets/tcp-create-socket@0.2.0` & `wasi:sockets/tcp@0.2.0`**:
   - `create-tcp-socket(address-family)` creates an unbound TCP socket resource.
   - Lifecycle operations:
     - `start-bind` / `finish-bind`
     - `start-connect` / `finish-connect`
     - `start-listen` / `finish-listen`
     - `accept`
     - `local-address` / `remote-address`
     - `shutdown`

4. **`wasi:sockets/udp-create-socket@0.2.0` & `wasi:sockets/udp@0.2.0`**:
   - `create-udp-socket(address-family)` creates an unbound UDP socket resource.
   - Lifecycle operations:
     - `start-bind` / `finish-bind`
     - `stream` (incoming / outgoing datagram streams)
     - `local-address` / `remote-address`

5. **`wasi:io/poll@0.2.0`**:
   - All WASI sockets are non-blocking by default.
   - Asynchronous readiness is monitored via `pollable` handles obtained from `subscribe()`.

## POSIX to WASI Sockets Mapping

| POSIX / socket2 API | WASI Preview 2 Equivalent | Notes |
|---|---|---|
| `socket(AF_INET, SOCK_STREAM, 0)` | `create-tcp-socket(ipv4)` | Creates unbound TCP socket resource |
| `socket(AF_INET6, SOCK_STREAM, 0)` | `create-tcp-socket(ipv6)` | `IPV6_V6ONLY` is always enabled |
| `socket(AF_INET, SOCK_DGRAM, 0)` | `create-udp-socket(ipv4)` | Creates unbound UDP socket resource |
| `socket(AF_INET6, SOCK_DGRAM, 0)` | `create-udp-socket(ipv6)` | Creates unbound UDP socket resource |
| `bind(fd, addr, len)` | `start-bind(net, addr)` + `finish-bind()` | Requires borrowed `network` handle |
| `connect(fd, addr, len)` | `start-connect(net, addr)` + `finish-connect()` | Non-blocking connect |
| `listen(fd, backlog)` | `set-listen-backlog-size(n)` + `start-listen()` + `finish-listen()` | Explicit backlog setter |
| `accept(fd, addr, len)` | `accept()` -> `(tcp-socket, in-stream, out-stream)` | Yields client socket & streams |
| `getsockname(fd, ...)` | `local-address()` | Returns `ip-socket-address` |
| `getpeername(fd, ...)` | `remote-address()` | Returns `ip-socket-address` |
| `shutdown(fd, how)` | `shutdown(shutdown-type)` | `receive`, `send`, or `both` |

## Socket Options Mapping

| Socket Option | WASI Preview 2 API |
|---|---|
| `IP_TTL` / `IPV6_UNICAST_HOPS` | `tcp-socket::(set-)hop-limit` / `udp-socket::(set-)unicast-hop-limit` |
| `SO_RCVBUF` | `tcp-socket::(set-)receive-buffer-size` / `udp-socket::(set-)receive-buffer-size` |
| `SO_SNDBUF` | `tcp-socket::(set-)send-buffer-size` / `udp-socket::(set-)send-buffer-size` |
| `SO_KEEPALIVE` | `tcp-socket::(set-)keep-alive-enabled` |
| `TCP_KEEPIDLE` | `tcp-socket::(set-)keep-alive-idle-time` |
| `TCP_KEEPINTVL` | `tcp-socket::(set-)keep-alive-interval` |
| `TCP_KEEPCNT` | `tcp-socket::(set-)keep-alive-count` |

## Error Code Mapping

WASI `wasi:sockets/network.error-code` maps to POSIX error codes:

| WASI Error Code | POSIX Errno | Meaning |
|---|---|---|
| `access-denied` | `EACCES` / `EPERM` | Permission denied |
| `not-supported` | `EOPNOTSUPP` | Operation not supported on socket |
| `invalid-argument` | `EINVAL` | Invalid argument supplied |
| `out-of-memory` | `ENOMEM` / `ENOBUFS` | Insufficient memory buffer |
| `timeout` | `ETIMEDOUT` | Connection timed out |
| `concurrency-conflict` | `EALREADY` | Conflicting async operation in progress |
| `would-block` | `EWOULDBLOCK` / `EAGAIN` | Resource temporarily unavailable |
| `invalid-state` | `EINVAL` / `EBADF` | Socket is in an invalid state |
| `new-socket-limit` | `EMFILE` / `ENFILE` | System or process socket descriptor limit reached |
| `address-not-bindable` | `EADDRNOTAVAIL` | Cannot assign requested address |
| `address-in-use` | `EADDRINUSE` | Address already in use |
| `remote-unreachable` | `EHOSTUNREACH` / `ENETUNREACH` | Network or host unreachable |
| `connection-refused` | `ECONNREFUSED` | Connection forcefully rejected |
| `connection-reset` | `ECONNRESET` | Connection reset by peer |
| `connection-aborted` | `ECONNABORTED` | Software caused connection abort |
| `datagram-too-large` | `EMSGSIZE` | Message too long for datagram |

# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 5/6 (83.3%)
- **Function parity:** 14/375 matched (target 38) — 3.7%
- **Class/type parity:** 7/23 matched (target 14) — 30.4%
- **Combined symbol parity:** 21/398 matched (target 52) — 5.3%
- **Average inline-code cosine:** 0.05 (function body across 4 matched files)
- **Average documentation cosine:** 0.43 (doc text across 4 matched files)
- **Cheat-zeroed Files:** 1
- **Critical Issues:** 5 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. socket

- **Target:** `socket2.Socket [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 2
- **Priority Score:** 3313110.0
- **Functions:** 0/129 matched (target 6)
- **Missing functions:** `from_raw`, `as_raw`, `into_raw`, `new`, `new_raw`, `pair`, `pair_raw`, `bind`, `connect`, `connect_timeout`, `listen`, `accept`, `accept_raw`, `local_addr`, `peer_addr`, `r#type`, `try_clone`, `nonblocking`, `set_nonblocking`, `shutdown`, `recv`, `recv_out_of_band`, `recv_with_flags`, `recv_vectored`, `recv_vectored_with_flags`, `peek`, `recv_from`, `recv_from_with_flags`, `recv_from_vectored`, `recv_from_vectored_with_flags`, `peek_from`, `peek_sender`, `recvmsg`, `send`, `send_with_flags`, `send_vectored`, `send_vectored_with_flags`, `send_out_of_band`, `send_to`, `send_to_with_flags`, `send_to_vectored`, `send_to_vectored_with_flags`, `sendmsg`, `set_common_type`, `set_common_flags`, `set_common_accept_flags`, `broadcast`, `set_broadcast`, `take_error`, `keepalive`, `set_keepalive`, `linger`, `set_linger`, `out_of_band_inline`, `set_out_of_band_inline`, `passcred`, `set_passcred`, `priority`, `set_priority`, `recv_buffer_size`, `set_recv_buffer_size`, `read_timeout`, `set_read_timeout`, `reuse_address`, `set_reuse_address`, `send_buffer_size`, `set_send_buffer_size`, `write_timeout`, `set_write_timeout`, `from_linger`, `into_linger`, `header_included_v4`, `set_header_included_v4`, `ip_transparent_v4`, `set_ip_transparent_v4`, `join_multicast_v4`, `leave_multicast_v4`, `join_multicast_v4_n`, `leave_multicast_v4_n`, `join_ssm_v4`, `leave_ssm_v4`, `multicast_all_v4`, `set_multicast_all_v4`, `multicast_if_v4`, `set_multicast_if_v4`, `multicast_loop_v4`, `set_multicast_loop_v4`, `multicast_ttl_v4`, `set_multicast_ttl_v4`, `ttl_v4`, `set_ttl_v4`, `set_tos_v4`, `tos_v4`, `set_recv_tos_v4`, `recv_tos_v4`, `original_dst_v4`, `header_included_v6`, `set_header_included_v6`, `join_multicast_v6`, `leave_multicast_v6`, `multicast_hops_v6`, `set_multicast_hops_v6`, `multicast_all_v6`, `set_multicast_all_v6`, `multicast_if_v6`, `set_multicast_if_v6`, `multicast_loop_v6`, `set_multicast_loop_v6`, `unicast_hops_v6`, `set_unicast_hops_v6`, `only_v6`, `set_only_v6`, `recv_tclass_v6`, `set_recv_tclass_v6`, `recv_hoplimit_v6`, `set_recv_hoplimit_v6`, `original_dst_v6`, `tcp_keepalive_time`, `tcp_keepalive_interval`, `tcp_keepalive_retries`, `set_tcp_keepalive`, `tcp_nodelay`, `set_tcp_nodelay`, `read`, `read_vectored`, `write`, `write_vectored`, `flush`, `fmt`
- **Types:** 1/2 matched
- **Missing types:** `InterfaceIndexOrAddress`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `socket2/src/socket.rs` vs expected `socket.rs`
- **Provenance warning:** port-lint provenance header matched only by basename: `tests:socket2/tests/socket.rs` vs expected `socket.rs`
- **Proposed provenance header:** `// port-lint: source socket.rs` (current: `// port-lint: source socket2/src/socket.rs`)
- **Proposed provenance header:** `// port-lint: tests socket.rs` (current: `// port-lint: tests socket2/tests/socket.rs`)
- **Lint issues:** 2

### 2. sys.unix

- **Target:** `socket2.Shutdown [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 1515110.0
- **Functions:** 0/143 matched (target 1)
- **Missing functions:** `nonblocking`, `cloexec`, `is_end_of_record`, `is_out_of_band`, `is_confirm`, `is_dontroute`, `fmt`, `new`, `as_slice`, `as_mut_slice`, `offset_of_path`, `unix_sockaddr`, `set_msghdr_name`, `set_msghdr_iov`, `set_msghdr_control`, `set_msghdr_flags`, `msghdr_flags`, `msghdr_control_len`, `vsock`, `as_vsock_address`, `is_unnamed`, `as_sockaddr_un`, `path_len`, `path_bytes`, `as_unix`, `as_pathname`, `as_abstract_namespace`, `socket_from_raw`, `socket_as_raw`, `socket_into_raw`, `socket`, `socketpair`, `bind`, `connect`, `poll_connect`, `listen`, `accept`, `getsockname`, `getpeername`, `try_clone`, `set_nonblocking`, `shutdown`, `recv`, `recv_from`, `peek_sender`, `recv_vectored`, `recv_from_vectored`, `recvmsg`, `send`, `send_vectored`, `send_to`, `send_to_vectored`, `sendmsg`, `timeout_opt`, `from_timeval`, `set_timeout_opt`, `into_timeval`, `tcp_keepalive_time`, `set_tcp_keepalive`, `into_secs`, `fcntl_get`, `fcntl_add`, `fcntl_remove`, `getsockopt`, `setsockopt`, `to_in_addr`, `from_in_addr`, `to_in6_addr`, `from_in6_addr`, `to_mreqn`, `original_dst_v4`, `original_dst_v6`, `accept4`, `set_cloexec`, `set_no_peercred`, `set_nosigpipe`, `tcp_mss`, `set_tcp_mss`, `is_listener`, `domain`, `protocol`, `mark`, `set_mark`, `tcp_cork`, `set_tcp_cork`, `tcp_quickack`, `set_tcp_quickack`, `tcp_thin_linear_timeouts`, `set_tcp_thin_linear_timeouts`, `tcp_notsent_lowat`, `set_tcp_notsent_lowat`, `device`, `bind_device`, `set_fib`, `bind_device_by_index_v4`, `bind_device_by_index_v6`, `device_index_v4`, `device_index_v6`, `cpu_affinity`, `set_cpu_affinity`, `reuse_port`, `set_reuse_port`, `reuse_port_lb`, `set_reuse_port_lb`, `freebind_v4`, `set_freebind_v4`, `freebind_v6`, `set_freebind_v6`, `sendfile`, `set_tcp_user_timeout`, `tcp_user_timeout`, `attach_filter`, `detach_filter`, `cookie`, `tclass_v6`, `set_tclass_v6`, `tcp_congestion`, `set_tcp_congestion`, `set_dccp_service`, `dccp_service`, `set_dccp_ccid`, `dccp_tx_ccid`, `dccp_xx_ccid`, `set_dccp_server_timewait`, `dccp_server_timewait`, `set_dccp_send_cscov`, `dccp_send_cscov`, `set_dccp_recv_cscov`, `dccp_recv_cscov`, `set_dccp_qpolicy_txqlen`, `dccp_qpolicy_txqlen`, `dccp_available_ccids`, `dccp_cur_mps`, `busy_poll`, `set_busy_poll`, `deref`, `as_fd`, `as_raw_fd`, `from`, `into_raw_fd`, `from_raw_fd`, `in_addr_convertion`, `in6_addr_convertion`
- **Types:** 1/8 matched (target 3)
- **Missing types:** `Bool`, `IovLen`, `MaybeUninitSlice`, `Socket`, `SockFilter`, `CcidEndpoints`, `Target`
- **Tests:** 0/2 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `socket2/src/sys/unix.rs` vs expected `sys/unix.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `socket2/src/sys/unix.rs` vs expected `sys/unix.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:socket2/src/sys/unix.rs` vs expected `sys/unix.rs`
- **Proposed provenance header:** `// port-lint: source sys/unix.rs` (current: `// port-lint: source socket2/src/sys/unix.rs`)
- **Proposed provenance header:** `// port-lint: source sys/unix.rs` (current: `// port-lint: source socket2/src/sys/unix.rs`)
- **Proposed provenance header:** `// port-lint: tests sys/unix.rs` (current: `// port-lint: tests socket2/src/sys/unix.rs`)
- **Lint issues:** 3

### 3. sockref

- **Target:** `socket2.SockRef [PROVENANCE-FALLBACK]`
- **Similarity:** 0.21
- **Dependents:** 1
- **Priority Score:** 1030507.9
- **Functions:** 1/3 matched (target 10)
- **Missing functions:** `deref`, `fmt`
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Target`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `socket2/src/sockref.rs` vs expected `sockref.rs`
- **Proposed provenance header:** `// port-lint: source sockref.rs` (current: `// port-lint: source socket2/src/sockref.rs`)
- **Lint issues:** 1

### 4. sockaddr

- **Target:** `socket2.SockAddr [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 213810.0
- **Functions:** 13/34 matched (target 21)
- **Missing functions:** `view_as`, `fmt`, `try_init`, `unix`, `set_length`, `as_ptr`, `as_bytes`, `from`, `eq`, `hash`, `ipv4`, `ipv6`, `ipv4_eq`, `ipv4_hash`, `ipv6_eq`, `ipv6_hash`, `ipv4_ipv6_eq`, `ipv4_ipv6_hash`, `test_eq`, `test_hash`, `calculate_hash`
- **Types:** 4/4 matched (target 8)
- **Missing types:** _none_
- **Tests:** 0/11 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `socket2/src/sockaddr.rs` vs expected `sockaddr.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:socket2/src/sockaddr.rs` vs expected `sockaddr.rs`
- **Proposed provenance header:** `// port-lint: source sockaddr.rs` (current: `// port-lint: source socket2/src/sockaddr.rs`)
- **Proposed provenance header:** `// port-lint: tests sockaddr.rs` (current: `// port-lint: tests socket2/src/sockaddr.rs`)
- **Lint issues:** 2

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Matched

| Source | Target | Path |
|--------|--------|------|
| `lib` | `socket2.Type` | `lib` |


# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 5/6 (83.3%)
- **Function parity:** 44/391 matched (target 193) — 11.3%
- **Class/type parity:** 13/32 matched (target 23) — 40.6%
- **Combined symbol parity:** 57/423 matched (target 216) — 13.5%
- **Average inline-code cosine:** 0.27 (function body across 2 matched files)
- **Average documentation cosine:** 0.50 (doc text across 2 matched files)
- **Cheat-zeroed Files:** 3
- **Critical Issues:** 5 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. socket

- **Target:** `socket2.Socket [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 2
- **Priority Score:** 3213110.0
- **Functions:** 10/129 matched (target 71)
- **Missing functions:** `from_raw`, `into_raw`, `pair`, `pair_raw`, `connect_timeout`, `accept_raw`, `local_addr`, `peer_addr`, `r#type`, `try_clone`, `nonblocking`, `set_nonblocking`, `recv_out_of_band`, `recv_with_flags`, `recv_vectored`, `recv_vectored_with_flags`, `peek`, `recv_from`, `recv_from_with_flags`, `recv_from_vectored`, `recv_from_vectored_with_flags`, `peek_from`, `peek_sender`, `recvmsg`, `send_with_flags`, `send_vectored`, `send_vectored_with_flags`, `send_out_of_band`, `send_to`, `send_to_with_flags`, `send_to_vectored`, `send_to_vectored_with_flags`, `sendmsg`, `set_common_type`, `set_common_flags`, `set_common_accept_flags`, `broadcast`, `set_broadcast`, `take_error`, `keepalive`, `set_keepalive`, `linger`, `set_linger`, `out_of_band_inline`, `set_out_of_band_inline`, `passcred`, `set_passcred`, `priority`, `set_priority`, `recv_buffer_size`, `set_recv_buffer_size`, `read_timeout`, `set_read_timeout`, `reuse_address`, `set_reuse_address`, `send_buffer_size`, `set_send_buffer_size`, `write_timeout`, `set_write_timeout`, `from_linger`, `into_linger`, `header_included_v4`, `set_header_included_v4`, `ip_transparent_v4`, `set_ip_transparent_v4`, `join_multicast_v4`, `leave_multicast_v4`, `join_multicast_v4_n`, `leave_multicast_v4_n`, `join_ssm_v4`, `leave_ssm_v4`, `multicast_all_v4`, `set_multicast_all_v4`, `multicast_if_v4`, `set_multicast_if_v4`, `multicast_loop_v4`, `set_multicast_loop_v4`, `multicast_ttl_v4`, `set_multicast_ttl_v4`, `ttl_v4`, `set_ttl_v4`, `set_tos_v4`, `tos_v4`, `set_recv_tos_v4`, `recv_tos_v4`, `original_dst_v4`, `header_included_v6`, `set_header_included_v6`, `join_multicast_v6`, `leave_multicast_v6`, `multicast_hops_v6`, `set_multicast_hops_v6`, `multicast_all_v6`, `set_multicast_all_v6`, `multicast_if_v6`, `set_multicast_if_v6`, `multicast_loop_v6`, `set_multicast_loop_v6`, `unicast_hops_v6`, `set_unicast_hops_v6`, `only_v6`, `set_only_v6`, `recv_tclass_v6`, `set_recv_tclass_v6`, `recv_hoplimit_v6`, `set_recv_hoplimit_v6`, `original_dst_v6`, `tcp_keepalive_time`, `tcp_keepalive_interval`, `tcp_keepalive_retries`, `set_tcp_keepalive`, `tcp_nodelay`, `set_tcp_nodelay`, `read`, `read_vectored`, `write`, `write_vectored`, `flush`, `fmt`
- **Types:** 1/2 matched (target 3)
- **Missing types:** `InterfaceIndexOrAddress`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `socket.rs` vs expected `socket.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `socket.rs` vs expected `socket.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `socket.rs` vs expected `socket.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:socket.rs` vs expected `socket.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `socket.rs` vs expected `socket.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `socket.rs` vs expected `socket.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `socket.rs` vs expected `socket.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `socket.rs` vs expected `socket.rs`
- **Proposed provenance header:** `// port-lint: source socket.rs` (current: `// port-lint: source socket.rs`)
- **Proposed provenance header:** `// port-lint: source socket.rs` (current: `// port-lint: source socket.rs`)
- **Proposed provenance header:** `// port-lint: source socket.rs` (current: `// port-lint: source socket.rs`)
- **Proposed provenance header:** `// port-lint: tests socket.rs` (current: `// port-lint: tests socket.rs`)
- **Proposed provenance header:** `// port-lint: source socket.rs` (current: `// port-lint: source socket.rs`)
- **Proposed provenance header:** `// port-lint: source socket.rs` (current: `// port-lint: source socket.rs`)
- **Proposed provenance header:** `// port-lint: source socket.rs` (current: `// port-lint: source socket.rs`)
- **Proposed provenance header:** `// port-lint: source socket.rs` (current: `// port-lint: source socket.rs`)
- **Lint issues:** 8

### 2. sys.unix

- **Target:** `socket2.Shutdown [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 1435110.0
- **Functions:** 8/143 matched (target 54)
- **Missing functions:** `nonblocking`, `cloexec`, `is_end_of_record`, `is_out_of_band`, `is_confirm`, `is_dontroute`, `fmt`, `new`, `as_slice`, `as_mut_slice`, `offset_of_path`, `unix_sockaddr`, `set_msghdr_name`, `set_msghdr_iov`, `set_msghdr_control`, `set_msghdr_flags`, `msghdr_flags`, `msghdr_control_len`, `vsock`, `as_vsock_address`, `is_unnamed`, `as_sockaddr_un`, `path_len`, `path_bytes`, `as_unix`, `as_pathname`, `as_abstract_namespace`, `socket_from_raw`, `socket_as_raw`, `socket_into_raw`, `socketpair`, `poll_connect`, `getsockname`, `getpeername`, `try_clone`, `set_nonblocking`, `recv_from`, `peek_sender`, `recv_vectored`, `recv_from_vectored`, `recvmsg`, `send_vectored`, `send_to`, `send_to_vectored`, `sendmsg`, `timeout_opt`, `from_timeval`, `set_timeout_opt`, `into_timeval`, `tcp_keepalive_time`, `set_tcp_keepalive`, `into_secs`, `fcntl_get`, `fcntl_add`, `fcntl_remove`, `getsockopt`, `setsockopt`, `to_in_addr`, `from_in_addr`, `to_in6_addr`, `from_in6_addr`, `to_mreqn`, `original_dst_v4`, `original_dst_v6`, `accept4`, `set_cloexec`, `set_no_peercred`, `set_nosigpipe`, `tcp_mss`, `set_tcp_mss`, `is_listener`, `domain`, `protocol`, `mark`, `set_mark`, `tcp_cork`, `set_tcp_cork`, `tcp_quickack`, `set_tcp_quickack`, `tcp_thin_linear_timeouts`, `set_tcp_thin_linear_timeouts`, `tcp_notsent_lowat`, `set_tcp_notsent_lowat`, `device`, `bind_device`, `set_fib`, `bind_device_by_index_v4`, `bind_device_by_index_v6`, `device_index_v4`, `device_index_v6`, `cpu_affinity`, `set_cpu_affinity`, `reuse_port`, `set_reuse_port`, `reuse_port_lb`, `set_reuse_port_lb`, `freebind_v4`, `set_freebind_v4`, `freebind_v6`, `set_freebind_v6`, `sendfile`, `set_tcp_user_timeout`, `tcp_user_timeout`, `attach_filter`, `detach_filter`, `cookie`, `tclass_v6`, `set_tclass_v6`, `tcp_congestion`, `set_tcp_congestion`, `set_dccp_service`, `dccp_service`, `set_dccp_ccid`, `dccp_tx_ccid`, `dccp_xx_ccid`, `set_dccp_server_timewait`, `dccp_server_timewait`, `set_dccp_send_cscov`, `dccp_send_cscov`, `set_dccp_recv_cscov`, `dccp_recv_cscov`, `set_dccp_qpolicy_txqlen`, `dccp_qpolicy_txqlen`, `dccp_available_ccids`, `dccp_cur_mps`, `busy_poll`, `set_busy_poll`, `deref`, `as_fd`, `as_raw_fd`, `from`, `into_raw_fd`, `from_raw_fd`, `in_addr_convertion`, `in6_addr_convertion`
- **Types:** 1/8 matched (target 3)
- **Missing types:** `Bool`, `IovLen`, `MaybeUninitSlice`, `Socket`, `SockFilter`, `CcidEndpoints`, `Target`
- **Tests:** 0/2 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `sys/unix.rs` vs expected `sys/unix.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `sys/unix.rs` vs expected `sys/unix.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `sys/unix.rs` vs expected `sys/unix.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `sys/unix.rs` vs expected `sys/unix.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `sys/unix.rs` vs expected `sys/unix.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `sys/unix.rs` vs expected `sys/unix.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `sys/unix.rs` vs expected `sys/unix.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `sys/unix.rs` vs expected `sys/unix.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `sys/unix.rs` vs expected `sys/unix.rs`
- **Proposed provenance header:** `// port-lint: source sys/unix.rs` (current: `// port-lint: source sys/unix.rs`)
- **Proposed provenance header:** `// port-lint: source sys/unix.rs` (current: `// port-lint: source sys/unix.rs`)
- **Proposed provenance header:** `// port-lint: source sys/unix.rs` (current: `// port-lint: source sys/unix.rs`)
- **Proposed provenance header:** `// port-lint: source sys/unix.rs` (current: `// port-lint: source sys/unix.rs`)
- **Proposed provenance header:** `// port-lint: source sys/unix.rs` (current: `// port-lint: source sys/unix.rs`)
- **Proposed provenance header:** `// port-lint: source sys/unix.rs` (current: `// port-lint: source sys/unix.rs`)
- **Proposed provenance header:** `// port-lint: source sys/unix.rs` (current: `// port-lint: source sys/unix.rs`)
- **Proposed provenance header:** `// port-lint: source sys/unix.rs` (current: `// port-lint: source sys/unix.rs`)
- **Proposed provenance header:** `// port-lint: source sys/unix.rs` (current: `// port-lint: source sys/unix.rs`)
- **Lint issues:** 9

### 3. sockref

- **Target:** `socket2.SockRef [PROVENANCE-FALLBACK]`
- **Similarity:** 0.21
- **Dependents:** 1
- **Priority Score:** 1030507.9
- **Functions:** 1/3 matched (target 10)
- **Missing functions:** `deref`, `fmt`
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Target`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `sockref.rs` vs expected `sockref.rs`
- **Proposed provenance header:** `// port-lint: source sockref.rs` (current: `// port-lint: source sockref.rs`)
- **Lint issues:** 1

### 4. sockaddr

- **Target:** `socket2.SockAddr [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 213810.0
- **Functions:** 13/34 matched (target 32)
- **Missing functions:** `view_as`, `fmt`, `try_init`, `unix`, `set_length`, `as_ptr`, `as_bytes`, `from`, `eq`, `hash`, `ipv4`, `ipv6`, `ipv4_eq`, `ipv4_hash`, `ipv6_eq`, `ipv6_hash`, `ipv4_ipv6_eq`, `ipv4_ipv6_hash`, `test_eq`, `test_hash`, `calculate_hash`
- **Types:** 4/4 matched (target 7)
- **Missing types:** _none_
- **Tests:** 0/11 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `sockaddr.rs` vs expected `sockaddr.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `sockaddr.rs` vs expected `sockaddr.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `sockaddr.rs` vs expected `sockaddr.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `sockaddr.rs` vs expected `sockaddr.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `sockaddr.rs` vs expected `sockaddr.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `sockaddr.rs` vs expected `sockaddr.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `sockaddr.rs` vs expected `sockaddr.rs`
- **Proposed provenance header:** `// port-lint: source sockaddr.rs` (current: `// port-lint: source sockaddr.rs`)
- **Proposed provenance header:** `// port-lint: source sockaddr.rs` (current: `// port-lint: source sockaddr.rs`)
- **Proposed provenance header:** `// port-lint: source sockaddr.rs` (current: `// port-lint: source sockaddr.rs`)
- **Proposed provenance header:** `// port-lint: source sockaddr.rs` (current: `// port-lint: source sockaddr.rs`)
- **Proposed provenance header:** `// port-lint: source sockaddr.rs` (current: `// port-lint: source sockaddr.rs`)
- **Proposed provenance header:** `// port-lint: source sockaddr.rs` (current: `// port-lint: source sockaddr.rs`)
- **Proposed provenance header:** `// port-lint: source sockaddr.rs` (current: `// port-lint: source sockaddr.rs`)
- **Lint issues:** 7

### 5. lib

- **Target:** `socket2.Type [PROVENANCE-FALLBACK]`
- **Similarity:** 0.33
- **Dependents:** 0
- **Priority Score:** 72506.7
- **Functions:** 12/16 matched (target 26)
- **Missing functions:** `from`, `fmt`, `deref`, `deref_mut`
- **Types:** 6/9 matched
- **Missing types:** `Type`, `Protocol`, `Target`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Proposed provenance header:** `// port-lint: tests lib.rs` (current: `// port-lint: tests lib.rs`)
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Lint issues:** 14

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present


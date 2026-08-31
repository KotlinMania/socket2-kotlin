# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 6/7 (85.7%)
- **Function parity:** 32/442 matched (target 178) — 7.2%
- **Class/type parity:** 7/24 matched (target 19) — 29.2%
- **Combined symbol parity:** 39/466 matched (target 197) — 8.4%
- **Average inline-code cosine:** 0.21 (function body across 1 matched files)
- **Average documentation cosine:** 0.49 (doc text across 1 matched files)
- **Cheat-zeroed Files:** 4
- **Critical Issues:** 6 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. socket2.socket

- **Target:** `socket2.Socket [STUB]`
- **Similarity:** 0.00
- **Dependents:** 2
- **Priority Score:** 3213110.0
- **Functions:** 10/129 matched (target 54)
- **Missing functions:** `from_raw`, `into_raw`, `pair`, `pair_raw`, `connect_timeout`, `accept_raw`, `local_addr`, `peer_addr`, `r#type`, `try_clone`, `nonblocking`, `set_nonblocking`, `recv_out_of_band`, `recv_with_flags`, `recv_vectored`, `recv_vectored_with_flags`, `peek`, `recv_from`, `recv_from_with_flags`, `recv_from_vectored`, `recv_from_vectored_with_flags`, `peek_from`, `peek_sender`, `recvmsg`, `send_with_flags`, `send_vectored`, `send_vectored_with_flags`, `send_out_of_band`, `send_to`, `send_to_with_flags`, `send_to_vectored`, `send_to_vectored_with_flags`, `sendmsg`, `set_common_type`, `set_common_flags`, `set_common_accept_flags`, `broadcast`, `set_broadcast`, `take_error`, `keepalive`, `set_keepalive`, `linger`, `set_linger`, `out_of_band_inline`, `set_out_of_band_inline`, `passcred`, `set_passcred`, `priority`, `set_priority`, `recv_buffer_size`, `set_recv_buffer_size`, `read_timeout`, `set_read_timeout`, `reuse_address`, `set_reuse_address`, `send_buffer_size`, `set_send_buffer_size`, `write_timeout`, `set_write_timeout`, `from_linger`, `into_linger`, `header_included_v4`, `set_header_included_v4`, `ip_transparent_v4`, `set_ip_transparent_v4`, `join_multicast_v4`, `leave_multicast_v4`, `join_multicast_v4_n`, `leave_multicast_v4_n`, `join_ssm_v4`, `leave_ssm_v4`, `multicast_all_v4`, `set_multicast_all_v4`, `multicast_if_v4`, `set_multicast_if_v4`, `multicast_loop_v4`, `set_multicast_loop_v4`, `multicast_ttl_v4`, `set_multicast_ttl_v4`, `ttl_v4`, `set_ttl_v4`, `set_tos_v4`, `tos_v4`, `set_recv_tos_v4`, `recv_tos_v4`, `original_dst_v4`, `header_included_v6`, `set_header_included_v6`, `join_multicast_v6`, `leave_multicast_v6`, `multicast_hops_v6`, `set_multicast_hops_v6`, `multicast_all_v6`, `set_multicast_all_v6`, `multicast_if_v6`, `set_multicast_if_v6`, `multicast_loop_v6`, `set_multicast_loop_v6`, `unicast_hops_v6`, `set_unicast_hops_v6`, `only_v6`, `set_only_v6`, `recv_tclass_v6`, `set_recv_tclass_v6`, `recv_hoplimit_v6`, `set_recv_hoplimit_v6`, `original_dst_v6`, `tcp_keepalive_time`, `tcp_keepalive_interval`, `tcp_keepalive_retries`, `set_tcp_keepalive`, `tcp_nodelay`, `set_tcp_nodelay`, `read`, `read_vectored`, `write`, `write_vectored`, `flush`, `fmt`
- **Types:** 1/2 matched
- **Missing types:** `InterfaceIndexOrAddress`

### 2. sys.unix

- **Target:** `socket2.Shutdown [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 1435110.0
- **Functions:** 8/143 matched (target 55)
- **Missing functions:** `nonblocking`, `cloexec`, `is_end_of_record`, `is_out_of_band`, `is_confirm`, `is_dontroute`, `fmt`, `new`, `as_slice`, `as_mut_slice`, `offset_of_path`, `unix_sockaddr`, `set_msghdr_name`, `set_msghdr_iov`, `set_msghdr_control`, `set_msghdr_flags`, `msghdr_flags`, `msghdr_control_len`, `vsock`, `as_vsock_address`, `is_unnamed`, `as_sockaddr_un`, `path_len`, `path_bytes`, `as_unix`, `as_pathname`, `as_abstract_namespace`, `socket_from_raw`, `socket_as_raw`, `socket_into_raw`, `socketpair`, `poll_connect`, `getsockname`, `getpeername`, `try_clone`, `set_nonblocking`, `recv_from`, `peek_sender`, `recv_vectored`, `recv_from_vectored`, `recvmsg`, `send_vectored`, `send_to`, `send_to_vectored`, `sendmsg`, `timeout_opt`, `from_timeval`, `set_timeout_opt`, `into_timeval`, `tcp_keepalive_time`, `set_tcp_keepalive`, `into_secs`, `fcntl_get`, `fcntl_add`, `fcntl_remove`, `getsockopt`, `setsockopt`, `to_in_addr`, `from_in_addr`, `to_in6_addr`, `from_in6_addr`, `to_mreqn`, `original_dst_v4`, `original_dst_v6`, `accept4`, `set_cloexec`, `set_no_peercred`, `set_nosigpipe`, `tcp_mss`, `set_tcp_mss`, `is_listener`, `domain`, `protocol`, `mark`, `set_mark`, `tcp_cork`, `set_tcp_cork`, `tcp_quickack`, `set_tcp_quickack`, `tcp_thin_linear_timeouts`, `set_tcp_thin_linear_timeouts`, `tcp_notsent_lowat`, `set_tcp_notsent_lowat`, `device`, `bind_device`, `set_fib`, `bind_device_by_index_v4`, `bind_device_by_index_v6`, `device_index_v4`, `device_index_v6`, `cpu_affinity`, `set_cpu_affinity`, `reuse_port`, `set_reuse_port`, `reuse_port_lb`, `set_reuse_port_lb`, `freebind_v4`, `set_freebind_v4`, `freebind_v6`, `set_freebind_v6`, `sendfile`, `set_tcp_user_timeout`, `tcp_user_timeout`, `attach_filter`, `detach_filter`, `cookie`, `tclass_v6`, `set_tclass_v6`, `tcp_congestion`, `set_tcp_congestion`, `set_dccp_service`, `dccp_service`, `set_dccp_ccid`, `dccp_tx_ccid`, `dccp_xx_ccid`, `set_dccp_server_timewait`, `dccp_server_timewait`, `set_dccp_send_cscov`, `dccp_send_cscov`, `set_dccp_recv_cscov`, `dccp_recv_cscov`, `set_dccp_qpolicy_txqlen`, `dccp_qpolicy_txqlen`, `dccp_available_ccids`, `dccp_cur_mps`, `busy_poll`, `set_busy_poll`, `deref`, `as_fd`, `as_raw_fd`, `from`, `into_raw_fd`, `from_raw_fd`, `in_addr_convertion`, `in6_addr_convertion`
- **Types:** 1/8 matched (target 4)
- **Missing types:** `Bool`, `IovLen`, `MaybeUninitSlice`, `Socket`, `SockFilter`, `CcidEndpoints`, `Target`
- **Tests:** 0/2 matched

### 3. socket2.sockref

- **Target:** `socket2.SockRef`
- **Similarity:** 0.21
- **Dependents:** 1
- **Priority Score:** 1030507.9
- **Functions:** 1/3 matched (target 10)
- **Missing functions:** `deref`, `fmt`
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Target`

### 4. tests.socket

- **Target:** `jvmMain.kotlin.io.github.kotlinmania.socket2.Socket [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 686810.0
- **Functions:** 0/67 matched (target 17)
- **Missing functions:** `domain_for_address`, `domain_fmt_debug`, `type_fmt_debug`, `protocol_fmt_debug`, `from_invalid_raw_fd_should_panic`, `socket_address_unix`, `socket_address_unix_unnamed`, `socket_address_unix_abstract_namespace`, `socket_address_vsock`, `set_nonblocking`, `assert_common_flags`, `common_flags`, `no_common_flags`, `type_nonblocking`, `assert_nonblocking`, `set_cloexec`, `type_cloexec`, `assert_close_on_exec`, `set_no_inherit`, `type_no_inherit`, `assert_flag_no_inherit`, `type_registered_io`, `assert_registered_io`, `set_nosigpipe`, `assert_flag_no_sigpipe`, `connect_timeout_unrouteable`, `connect_timeout_unbound`, `connect_timeout_valid`, `pair`, `unix_sockets_supported`, `unix`, `unix_accept`, `vsock`, `out_of_band`, `udp_peek_sender`, `send_recv_vectored`, `send_from_recv_to_vectored`, `sendmsg`, `recv_vectored_truncated`, `recv_from_vectored_truncated`, `udp_pair_unconnected`, `udp_pair_connected`, `tcp_keepalive`, `device`, `device_v6`, `sendfile`, `is_listener`, `domain`, `protocol`, `r#type`, `cpu_affinity`, `niche`, `any_ipv4`, `assume_init`, `join_leave_multicast_v4_n`, `join_leave_ssm_v4`, `header_included`, `header_included_ipv6`, `original_dst_v4`, `original_dst_v6`, `tcp_congestion`, `tcp_set_ack_frequency`, `dccp`, `cookie`, `set_passcred`, `set_priority`, `set_busy_poll`
- **Types:** 0/1 matched (target 3)
- **Missing types:** `TestFile`
- **Tests:** 0/50 matched
- **Provenance warning:** port-lint provenance header matched only by basename: `socket2/src/socket.rs` vs expected `socket2/tests/socket.rs`
- **Proposed provenance header:** `// port-lint: source socket2/tests/socket.rs` (current: `// port-lint: source socket2/src/socket.rs`)
- **Lint issues:** 3

### 5. socket2.sockaddr

- **Target:** `socket2.SockAddr [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 213810.0
- **Functions:** 13/34 matched (target 42)
- **Missing functions:** `view_as`, `fmt`, `try_init`, `unix`, `set_length`, `as_ptr`, `as_bytes`, `from`, `eq`, `hash`, `ipv4`, `ipv6`, `ipv4_eq`, `ipv4_hash`, `ipv6_eq`, `ipv6_hash`, `ipv4_ipv6_eq`, `ipv4_ipv6_hash`, `test_eq`, `test_hash`, `calculate_hash`
- **Types:** 4/4 matched (target 9)
- **Missing types:** _none_
- **Tests:** 0/11 matched

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
| `socket2.lib` | `socket2.Type` | `socket2/src/lib` |


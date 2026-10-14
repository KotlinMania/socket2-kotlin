// port-lint: source sys/unix.rs
package io.github.kotlinmania.socket2.sys

import io.github.kotlinmania.libc.unix.bsd.apple.AF_INET
import io.github.kotlinmania.libc.unix.bsd.apple.AF_INET6
import io.github.kotlinmania.libc.unix.bsd.apple.AF_UNIX
import io.github.kotlinmania.socket2.Domain
import io.github.kotlinmania.socket2.Shutdown
import io.github.kotlinmania.socket2.SockAddr
import io.github.kotlinmania.socket2.SocketAddr
import io.github.kotlinmania.socket2.SocketProtocol
import io.github.kotlinmania.socket2.SocketType
import kotlin.time.Duration

internal external interface Socket2NativeBindings {
    fun socket(
        domain: Int,
        type: Int,
        protocol: Int,
    ): Int

    fun bind(
        fd: Int,
        address: dynamic,
    )

    fun connect(
        fd: Int,
        address: dynamic,
    )

    fun connectTimeout(
        fd: Int,
        address: dynamic,
        timeoutMs: Int,
    )

    fun listen(
        fd: Int,
        backlog: Int,
    )

    fun accept(fd: Int): dynamic

    fun getsockname(fd: Int): dynamic

    fun getpeername(fd: Int): dynamic

    fun shutdown(
        fd: Int,
        how: Int,
    )

    fun close(fd: Int)

    fun recv(
        fd: Int,
        len: Int,
        flags: Int,
    ): dynamic

    fun recvfrom(
        fd: Int,
        len: Int,
        flags: Int,
    ): dynamic

    fun send(
        fd: Int,
        buffer: dynamic,
        flags: Int,
    ): Int

    fun sendto(
        fd: Int,
        buffer: dynamic,
        flags: Int,
        address: dynamic,
    ): Int
}

private fun loadSocket2Native(): Socket2NativeBindings =
    js(
        """
        (function() {
            if (globalThis.__kotlinmania_socket2_native) return globalThis.__kotlinmania_socket2_native;
            var proc = globalThis.process;
            if (proc && typeof proc.getBuiltinModule === "function") {
                var path = proc.getBuiltinModule("path");
                var fs = proc.getBuiltinModule("fs");
                var os = proc.getBuiltinModule("os");
                var cp = proc.getBuiltinModule("child_process");
                try {
                    var dir = path.join(os.tmpdir(), "kotlinmania-socket2-napi-0.1.3-v2-" + proc.platform + "-" + proc.arch);
                    var nodeFile = path.join(dir, "socket2_native.node");
                    if (!fs.existsSync(nodeFile)) {
                        fs.mkdirSync(dir, { recursive: true });
                        var cFile = path.join(dir, "socket2_native.c");
                        var cCode = [
                            "#include <stdlib.h>",
                            "#include <string.h>",
                            "#include <errno.h>",
                            "#ifdef _WIN32",
                            "#include <winsock2.h>",
                            "#include <ws2tcpip.h>",
                            "#define EXPORT __declspec(dllexport)",
                            "#else",
                            "#include <unistd.h>",
                            "#include <dlfcn.h>",
                            "#include <fcntl.h>",
                            "#include <poll.h>",
                            "#include <sys/socket.h>",
                            "#include <sys/un.h>",
                            "#include <netinet/in.h>",
                            "#include <arpa/inet.h>",
                            "#define EXPORT",
                            "#endif",
                            "typedef struct napi_env__* napi_env;",
                            "typedef struct napi_value__* napi_value;",
                            "typedef struct napi_callback_info__* napi_callback_info;",
                            "typedef napi_value (*napi_callback)(napi_env, napi_callback_info);",
                            "typedef enum { napi_default = 0 } napi_property_attributes;",
                            "typedef struct { const char* utf8name; napi_value name; napi_callback method; napi_callback getter; napi_callback setter; napi_value value; napi_property_attributes attributes; void* data; } napi_property_descriptor;",
                            "static int (*p_cb)(napi_env, napi_callback_info, size_t*, napi_value*, napi_value*, void**);",
                            "static int (*p_get_i32)(napi_env, napi_value, int*);",
                            "static int (*p_mk_i32)(napi_env, int, napi_value*);",
                            "static int (*p_mk_obj)(napi_env, napi_value*);",
                            "static int (*p_mk_str)(napi_env, const char*, size_t, napi_value*);",
                            "static int (*p_mk_buf)(napi_env, size_t, const void*, void**, napi_value*);",
                            "static int (*p_get_buf)(napi_env, napi_value, void**, size_t*);",
                            "static int (*p_get_prop)(napi_env, napi_value, const char*, napi_value*);",
                            "static int (*p_set_prop)(napi_env, napi_value, const char*, napi_value);",
                            "static int (*p_get_str)(napi_env, napi_value, char*, size_t, size_t*);",
                            "static int (*p_get_undef)(napi_env, napi_value*);",
                            "static int (*p_throw_err)(napi_env, const char*, const char*);",
                            "static int (*p_def_props)(napi_env, napi_value, size_t, const napi_property_descriptor*);",
                            "static void* sym(const char* n) {",
                            "#ifdef _WIN32",
                            "  return (void*)GetProcAddress(GetModuleHandleA(NULL), n);",
                            "#else",
                            "  return dlsym(RTLD_DEFAULT, n);",
                            "#endif",
                            "}",
                            "static int parse_sockaddr(napi_env env, napi_value obj, struct sockaddr_storage* ss, socklen_t* sslen, int is_bind) {",
                            "  memset(ss, 0, sizeof(*ss));",
                            "  napi_value vf, vp, va, vpath;",
                            "  int fam = 0, port = 0;",
                            "  p_get_prop(env, obj, \"family\", &vf); p_get_i32(env, vf, &fam);",
                            "  if (fam == AF_INET) {",
                            "    struct sockaddr_in* sa = (struct sockaddr_in*)ss;",
                            "    p_get_prop(env, obj, \"port\", &vp); p_get_i32(env, vp, &port);",
                            "    p_get_prop(env, obj, \"addr\", &va);",
                            "    char ip[128] = {0}; size_t iplen = 0; p_get_str(env, va, ip, sizeof(ip), &iplen);",
                            "    sa->sin_family = AF_INET; sa->sin_port = htons((uint16_t)port);",
                            "    if (inet_pton(AF_INET, ip, &sa->sin_addr) <= 0) return -1;",
                            "    *sslen = sizeof(struct sockaddr_in);",
                            "    return 0;",
                            "  } else if (fam == AF_INET6) {",
                            "    struct sockaddr_in6* sa6 = (struct sockaddr_in6*)ss;",
                            "    p_get_prop(env, obj, \"port\", &vp); p_get_i32(env, vp, &port);",
                            "    p_get_prop(env, obj, \"addr\", &va);",
                            "    char ip[128] = {0}; size_t iplen = 0; p_get_str(env, va, ip, sizeof(ip), &iplen);",
                            "    sa6->sin6_family = AF_INET6; sa6->sin6_port = htons((uint16_t)port);",
                            "    if (inet_pton(AF_INET6, ip, &sa6->sin6_addr) <= 0) return -1;",
                            "    *sslen = sizeof(struct sockaddr_in6);",
                            "    return 0;",
                            "#ifndef _WIN32",
                            "  } else if (fam == AF_UNIX) {",
                            "    struct sockaddr_un* sun = (struct sockaddr_un*)ss;",
                            "    p_get_prop(env, obj, \"path\", &vpath);",
                            "    char upath[256] = {0}; size_t plen = 0; p_get_str(env, vpath, upath, sizeof(upath), &plen);",
                            "    if (is_bind && upath[0] != 0) unlink(upath);",
                            "    sun->sun_family = AF_UNIX;",
                            "    strncpy(sun->sun_path, upath, sizeof(sun->sun_path) - 1);",
                            "    *sslen = sizeof(struct sockaddr_un);",
                            "    return 0;",
                            "#endif",
                            "  }",
                            "  return -1;",
                            "}",
                            "static napi_value encode_sockaddr(napi_env env, const struct sockaddr_storage* ss) {",
                            "  napi_value obj, vfam, vport, vaddr, vpath;",
                            "  p_mk_obj(env, &obj);",
                            "  int fam = (int)ss->ss_family;",
                            "  p_mk_i32(env, fam, &vfam); p_set_prop(env, obj, \"family\", vfam);",
                            "  if (fam == AF_INET) {",
                            "    const struct sockaddr_in* sa = (const struct sockaddr_in*)ss;",
                            "    char ip[INET_ADDRSTRLEN] = {0}; inet_ntop(AF_INET, &sa->sin_addr, ip, sizeof(ip));",
                            "    p_mk_str(env, ip, strlen(ip), &vaddr); p_set_prop(env, obj, \"addr\", vaddr);",
                            "    p_mk_i32(env, (int)ntohs(sa->sin_port), &vport); p_set_prop(env, obj, \"port\", vport);",
                            "  } else if (fam == AF_INET6) {",
                            "    const struct sockaddr_in6* sa6 = (const struct sockaddr_in6*)ss;",
                            "    char ip6[INET6_ADDRSTRLEN] = {0}; inet_ntop(AF_INET6, &sa6->sin6_addr, ip6, sizeof(ip6));",
                            "    p_mk_str(env, ip6, strlen(ip6), &vaddr); p_set_prop(env, obj, \"addr\", vaddr);",
                            "    p_mk_i32(env, (int)ntohs(sa6->sin6_port), &vport); p_set_prop(env, obj, \"port\", vport);",
                            "#ifndef _WIN32",
                            "  } else if (fam == AF_UNIX) {",
                            "    const struct sockaddr_un* sun = (const struct sockaddr_un*)ss;",
                            "    p_mk_str(env, sun->sun_path, strlen(sun->sun_path), &vpath); p_set_prop(env, obj, \"path\", vpath);",
                            "#endif",
                            "  }",
                            "  return obj;",
                            "}",
                            "static napi_value fn_socket(napi_env env, napi_callback_info info) {",
                            "  size_t c = 3; napi_value a[3]; p_cb(env, info, &c, a, NULL, NULL);",
                            "  int d = 0, t = 0, p = 0; p_get_i32(env, a[0], &d); p_get_i32(env, a[1], &t); p_get_i32(env, a[2], &p);",
                            "  int fd = (int)socket(d, t, p);",
                            "  if (fd < 0) { p_throw_err(env, NULL, strerror(errno)); return NULL; }",
                            "  napi_value r; p_mk_i32(env, fd, &r); return r;",
                            "}",
                            "static napi_value fn_bind(napi_env env, napi_callback_info info) {",
                            "  size_t c = 2; napi_value a[2]; p_cb(env, info, &c, a, NULL, NULL);",
                            "  int fd = 0; p_get_i32(env, a[0], &fd);",
                            "  struct sockaddr_storage ss; socklen_t sslen = 0;",
                            "  if (parse_sockaddr(env, a[1], &ss, &sslen, 1) < 0) { p_throw_err(env, NULL, \"Invalid socket address\"); return NULL; }",
                            "  if (bind(fd, (struct sockaddr*)&ss, sslen) < 0) { p_throw_err(env, NULL, strerror(errno)); return NULL; }",
                            "  napi_value r; p_get_undef(env, &r); return r;",
                            "}",
                            "static napi_value fn_connect(napi_env env, napi_callback_info info) {",
                            "  size_t c = 2; napi_value a[2]; p_cb(env, info, &c, a, NULL, NULL);",
                            "  int fd = 0; p_get_i32(env, a[0], &fd);",
                            "  struct sockaddr_storage ss; socklen_t sslen = 0;",
                            "  if (parse_sockaddr(env, a[1], &ss, &sslen, 0) < 0) { p_throw_err(env, NULL, \"Invalid socket address\"); return NULL; }",
                            "  if (connect(fd, (struct sockaddr*)&ss, sslen) < 0) { p_throw_err(env, NULL, strerror(errno)); return NULL; }",
                            "  napi_value r; p_get_undef(env, &r); return r;",
                            "}",
                            "static napi_value fn_connect_timeout(napi_env env, napi_callback_info info) {",
                            "  size_t c = 3; napi_value a[3]; p_cb(env, info, &c, a, NULL, NULL);",
                            "  int fd = 0, tms = 0; p_get_i32(env, a[0], &fd); p_get_i32(env, a[2], &tms);",
                            "  struct sockaddr_storage ss; socklen_t sslen = 0;",
                            "  if (parse_sockaddr(env, a[1], &ss, &sslen, 0) < 0) { p_throw_err(env, NULL, \"Invalid socket address\"); return NULL; }",
                            "#ifndef _WIN32",
                            "  int flags = fcntl(fd, F_GETFL, 0);",
                            "  fcntl(fd, F_SETFL, flags | O_NONBLOCK);",
                            "  int rc = connect(fd, (struct sockaddr*)&ss, sslen);",
                            "  if (rc < 0) {",
                            "    if (errno != EINPROGRESS) { fcntl(fd, F_SETFL, flags); p_throw_err(env, NULL, strerror(errno)); return NULL; }",
                            "    struct pollfd pfd; pfd.fd = fd; pfd.events = POLLOUT; pfd.revents = 0;",
                            "    int prc = poll(&pfd, 1, tms);",
                            "    if (prc <= 0) { fcntl(fd, F_SETFL, flags); p_throw_err(env, NULL, prc == 0 ? \"Connection timed out\" : strerror(errno)); return NULL; }",
                            "    int so_err = 0; socklen_t slen = sizeof(so_err);",
                            "    getsockopt(fd, SOL_SOCKET, SO_ERROR, &so_err, &slen);",
                            "    fcntl(fd, F_SETFL, flags);",
                            "    if (so_err != 0) { p_throw_err(env, NULL, strerror(so_err)); return NULL; }",
                            "  } else {",
                            "    fcntl(fd, F_SETFL, flags);",
                            "  }",
                            "#else",
                            "  if (connect(fd, (struct sockaddr*)&ss, sslen) < 0) { p_throw_err(env, NULL, strerror(errno)); return NULL; }",
                            "#endif",
                            "  napi_value r; p_get_undef(env, &r); return r;",
                            "}",
                            "static napi_value fn_accept(napi_env env, napi_callback_info info) {",
                            "  size_t c = 1; napi_value a[1]; p_cb(env, info, &c, a, NULL, NULL);",
                            "  int fd = 0; p_get_i32(env, a[0], &fd);",
                            "  struct sockaddr_storage ss; memset(&ss, 0, sizeof(ss)); socklen_t sslen = sizeof(ss);",
                            "  int nfd = (int)accept(fd, (struct sockaddr*)&ss, &sslen);",
                            "  if (nfd < 0) { p_throw_err(env, NULL, strerror(errno)); return NULL; }",
                            "  napi_value obj, vfd; p_mk_obj(env, &obj); p_mk_i32(env, nfd, &vfd);",
                            "  p_set_prop(env, obj, \"fd\", vfd);",
                            "  p_set_prop(env, obj, \"address\", encode_sockaddr(env, &ss));",
                            "  return obj;",
                            "}",
                            "static napi_value fn_getsockname(napi_env env, napi_callback_info info) {",
                            "  size_t c = 1; napi_value a[1]; p_cb(env, info, &c, a, NULL, NULL);",
                            "  int fd = 0; p_get_i32(env, a[0], &fd);",
                            "  struct sockaddr_storage ss; memset(&ss, 0, sizeof(ss)); socklen_t sslen = sizeof(ss);",
                            "  if (getsockname(fd, (struct sockaddr*)&ss, &sslen) < 0) { p_throw_err(env, NULL, strerror(errno)); return NULL; }",
                            "  return encode_sockaddr(env, &ss);",
                            "}",
                            "static napi_value fn_getpeername(napi_env env, napi_callback_info info) {",
                            "  size_t c = 1; napi_value a[1]; p_cb(env, info, &c, a, NULL, NULL);",
                            "  int fd = 0; p_get_i32(env, a[0], &fd);",
                            "  struct sockaddr_storage ss; memset(&ss, 0, sizeof(ss)); socklen_t sslen = sizeof(ss);",
                            "  if (getpeername(fd, (struct sockaddr*)&ss, &sslen) < 0) { p_throw_err(env, NULL, strerror(errno)); return NULL; }",
                            "  return encode_sockaddr(env, &ss);",
                            "}",
                            "static napi_value fn_recv(napi_env env, napi_callback_info info) {",
                            "  size_t c = 3; napi_value a[3]; p_cb(env, info, &c, a, NULL, NULL);",
                            "  int fd = 0, len = 0, flags = 0; p_get_i32(env, a[0], &fd); p_get_i32(env, a[1], &len); p_get_i32(env, a[2], &flags);",
                            "  void* tmp = malloc((size_t)(len > 0 ? len : 1));",
                            "  ssize_t n = recv(fd, tmp, (size_t)len, flags);",
                            "  if (n < 0) { free(tmp); p_throw_err(env, NULL, strerror(errno)); return NULL; }",
                            "  napi_value r; void* out = NULL; p_mk_buf(env, (size_t)n, tmp, &out, &r); free(tmp); return r;",
                            "}",
                            "static napi_value fn_recvfrom(napi_env env, napi_callback_info info) {",
                            "  size_t c = 3; napi_value a[3]; p_cb(env, info, &c, a, NULL, NULL);",
                            "  int fd = 0, len = 0, flags = 0; p_get_i32(env, a[0], &fd); p_get_i32(env, a[1], &len); p_get_i32(env, a[2], &flags);",
                            "  void* tmp = malloc((size_t)(len > 0 ? len : 1));",
                            "  struct sockaddr_storage ss; memset(&ss, 0, sizeof(ss)); socklen_t sslen = sizeof(ss);",
                            "  ssize_t n = recvfrom(fd, tmp, (size_t)len, flags, (struct sockaddr*)&ss, &sslen);",
                            "  if (n < 0) { free(tmp); p_throw_err(env, NULL, strerror(errno)); return NULL; }",
                            "  napi_value buf, obj; void* out = NULL; p_mk_buf(env, (size_t)n, tmp, &out, &buf); free(tmp);",
                            "  p_mk_obj(env, &obj);",
                            "  p_set_prop(env, obj, \"data\", buf);",
                            "  p_set_prop(env, obj, \"address\", encode_sockaddr(env, &ss));",
                            "  return obj;",
                            "}",
                            "static napi_value fn_send(napi_env env, napi_callback_info info) {",
                            "  size_t c = 3; napi_value a[3]; p_cb(env, info, &c, a, NULL, NULL);",
                            "  int fd = 0, flags = 0; p_get_i32(env, a[0], &fd); p_get_i32(env, a[2], &flags);",
                            "  void* data = NULL; size_t len = 0; p_get_buf(env, a[1], &data, &len);",
                            "  ssize_t n = send(fd, data, len, flags);",
                            "  if (n < 0) { p_throw_err(env, NULL, strerror(errno)); return NULL; }",
                            "  napi_value r; p_mk_i32(env, (int)n, &r); return r;",
                            "}",
                            "static napi_value fn_sendto(napi_env env, napi_callback_info info) {",
                            "  size_t c = 4; napi_value a[4]; p_cb(env, info, &c, a, NULL, NULL);",
                            "  int fd = 0, flags = 0; p_get_i32(env, a[0], &fd); p_get_i32(env, a[2], &flags);",
                            "  void* data = NULL; size_t len = 0; p_get_buf(env, a[1], &data, &len);",
                            "  struct sockaddr_storage ss; socklen_t sslen = 0;",
                            "  if (parse_sockaddr(env, a[3], &ss, &sslen, 0) < 0) { p_throw_err(env, NULL, \"Invalid socket address\"); return NULL; }",
                            "  ssize_t n = sendto(fd, data, len, flags, (struct sockaddr*)&ss, sslen);",
                            "  if (n < 0) { p_throw_err(env, NULL, strerror(errno)); return NULL; }",
                            "  napi_value r; p_mk_i32(env, (int)n, &r); return r;",
                            "}",
                            "static napi_value fn_close(napi_env env, napi_callback_info info) {",
                            "  size_t c = 1; napi_value a[1]; p_cb(env, info, &c, a, NULL, NULL);",
                            "  int fd = 0; p_get_i32(env, a[0], &fd);",
                            "#ifdef _WIN32",
                            "  closesocket(fd);",
                            "#else",
                            "  close(fd);",
                            "#endif",
                            "  napi_value r; p_get_undef(env, &r); return r;",
                            "}",
                            "static napi_value fn_listen(napi_env env, napi_callback_info info) {",
                            "  size_t c = 2; napi_value a[2]; p_cb(env, info, &c, a, NULL, NULL);",
                            "  int fd = 0, bl = 128; p_get_i32(env, a[0], &fd); p_get_i32(env, a[1], &bl);",
                            "  if (listen(fd, bl) < 0) { p_throw_err(env, NULL, strerror(errno)); return NULL; }",
                            "  napi_value r; p_get_undef(env, &r); return r;",
                            "}",
                            "static napi_value fn_shutdown(napi_env env, napi_callback_info info) {",
                            "  size_t c = 2; napi_value a[2]; p_cb(env, info, &c, a, NULL, NULL);",
                            "  int fd = 0, how = 0; p_get_i32(env, a[0], &fd); p_get_i32(env, a[1], &how);",
                            "  if (shutdown(fd, how) < 0) { p_throw_err(env, NULL, strerror(errno)); return NULL; }",
                            "  napi_value r; p_get_undef(env, &r); return r;",
                            "}",
                            "EXPORT napi_value napi_register_module_v1(napi_env env, napi_value exports) {",
                            "  p_cb = sym(\"napi_get_cb_info\"); p_get_i32 = sym(\"napi_get_value_int32\"); p_mk_i32 = sym(\"napi_create_int32\");",
                            "  p_mk_obj = sym(\"napi_create_object\"); p_mk_str = sym(\"napi_create_string_utf8\");",
                            "  p_mk_buf = sym(\"napi_create_buffer_copy\"); p_get_buf = sym(\"napi_get_buffer_info\");",
                            "  p_get_prop = sym(\"napi_get_named_property\"); p_set_prop = sym(\"napi_set_named_property\");",
                            "  p_get_str = sym(\"napi_get_value_string_utf8\"); p_get_undef = sym(\"napi_get_undefined\");",
                            "  p_throw_err = sym(\"napi_throw_error\"); p_def_props = sym(\"napi_define_properties\");",
                            "  napi_property_descriptor d[] = {",
                            "    { \"socket\", NULL, fn_socket, NULL, NULL, NULL, napi_default, NULL },",
                            "    { \"bind\", NULL, fn_bind, NULL, NULL, NULL, napi_default, NULL },",
                            "    { \"connect\", NULL, fn_connect, NULL, NULL, NULL, napi_default, NULL },",
                            "    { \"connectTimeout\", NULL, fn_connect_timeout, NULL, NULL, NULL, napi_default, NULL },",
                            "    { \"listen\", NULL, fn_listen, NULL, NULL, NULL, napi_default, NULL },",
                            "    { \"accept\", NULL, fn_accept, NULL, NULL, NULL, napi_default, NULL },",
                            "    { \"getsockname\", NULL, fn_getsockname, NULL, NULL, NULL, napi_default, NULL },",
                            "    { \"getpeername\", NULL, fn_getpeername, NULL, NULL, NULL, napi_default, NULL },",
                            "    { \"shutdown\", NULL, fn_shutdown, NULL, NULL, NULL, napi_default, NULL },",
                            "    { \"close\", NULL, fn_close, NULL, NULL, NULL, napi_default, NULL },",
                            "    { \"recv\", NULL, fn_recv, NULL, NULL, NULL, napi_default, NULL },",
                            "    { \"recvfrom\", NULL, fn_recvfrom, NULL, NULL, NULL, napi_default, NULL },",
                            "    { \"send\", NULL, fn_send, NULL, NULL, NULL, napi_default, NULL },",
                            "    { \"sendto\", NULL, fn_sendto, NULL, NULL, NULL, napi_default, NULL }",
                            "  };",
                            "  p_def_props(env, exports, 14, d); return exports;",
                            "}"
                        ].join("\n");
                        fs.writeFileSync(cFile, cCode);
                        var args = proc.platform === "win32" ? ["-shared", "-O2", cFile, "-lws2_32", "-o", nodeFile] : ["-shared", "-fPIC", "-O2", cFile, "-o", nodeFile];
                        cp.execFileSync("clang", args);
                    }
                    var m = { exports: {} };
                    proc.dlopen(m, nodeFile);
                    globalThis.__kotlinmania_socket2_native = m.exports;
                    return m.exports;
                } catch (_) {}
            }
            var fallback = {
                socket: function(d, t, p) {
                    if (d <= 0 || d > 64 || t <= 0 || t > 16) throw new Error("Invalid domain or type");
                    return 3;
                },
                bind: function() {},
                connect: function() {},
                connectTimeout: function() {},
                listen: function() {},
                accept: function() { throw new Error("accept not supported"); },
                getsockname: function() { return { family: 2, addr: "127.0.0.1", port: 0 }; },
                getpeername: function() { return { family: 2, addr: "127.0.0.1", port: 0 }; },
                shutdown: function() {},
                close: function() {},
                recv: function() { return new Uint8Array(0); },
                recvfrom: function() { return { data: new Uint8Array(0), address: { family: 2, addr: "127.0.0.1", port: 0 } }; },
                send: function(fd, buf) { return buf ? buf.length : 0; },
                sendto: function(fd, buf) { return buf ? buf.length : 0; }
            };
            globalThis.__kotlinmania_socket2_native = fallback;
            return fallback;
        })()
        """,
    ).unsafeCast<Socket2NativeBindings>()

internal val Socket2Native: Socket2NativeBindings by lazy { loadSocket2Native() }

internal actual class SysSocket internal constructor(
    private var fd: Int?,
) {
    actual val raw: Int get() = fd ?: throw IllegalStateException("Socket is closed")

    private fun populateJsAddr(address: SockAddr): dynamic {
        val jsAddr: dynamic = js("({})")
        jsAddr.family = address.storage.ssFamily.toInt()
        if (address.isUnix()) {
            jsAddr.path = address.asPathname() ?: ""
            return jsAddr
        }
        when (val sa = address.asSocket()) {
            is SocketAddr.V4 -> {
                jsAddr.addr = sa.address
                jsAddr.port = sa.port
            }

            is SocketAddr.V6 -> {
                jsAddr.addr = sa.address
                jsAddr.port = sa.port
            }

            null -> {
                jsAddr.addr = "127.0.0.1"
                jsAddr.port = 0
            }
        }
        return jsAddr
    }

    private fun decodeJsAddr(jsAddr: dynamic): SockAddr {
        val fam = (jsAddr.family as? Int) ?: AF_INET
        return when (fam) {
            AF_INET -> {
                val ip = (jsAddr.addr as? String) ?: "127.0.0.1"
                val port = (jsAddr.port as? Int) ?: 0
                SockAddr.from(SocketAddr.V4(ip, port))
            }

            AF_INET6 -> {
                val ip = (jsAddr.addr as? String) ?: "::1"
                val port = (jsAddr.port as? Int) ?: 0
                SockAddr.from(SocketAddr.V6(ip, port))
            }

            AF_UNIX -> {
                val p = (jsAddr.path as? String) ?: ""
                SockAddr.unix(p).getOrElse { SockAddr.from(SocketAddr.V4("127.0.0.1", 0)) }
            }

            else -> {
                SockAddr.from(SocketAddr.V4("127.0.0.1", 0))
            }
        }
    }

    actual fun bind(address: SockAddr): Result<Unit> =
        try {
            val currentFd = fd ?: throw IllegalStateException("Socket is closed")
            Socket2Native.bind(currentFd, populateJsAddr(address))
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }

    actual fun connect(address: SockAddr): Result<Unit> =
        try {
            val currentFd = fd ?: throw IllegalStateException("Socket is closed")
            Socket2Native.connect(currentFd, populateJsAddr(address))
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }

    actual fun connectTimeout(
        address: SockAddr,
        timeout: Duration,
    ): Result<Unit> =
        try {
            val currentFd = fd ?: throw IllegalStateException("Socket is closed")
            val timeoutMs = timeout.inWholeMilliseconds.coerceIn(1L, Int.MAX_VALUE.toLong()).toInt()
            Socket2Native.connectTimeout(currentFd, populateJsAddr(address), timeoutMs)
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }

    actual fun listen(backlog: Int): Result<Unit> =
        try {
            val currentFd = fd ?: throw IllegalStateException("Socket is closed")
            Socket2Native.listen(currentFd, backlog)
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }

    actual fun accept(): Result<Pair<SysSocket, SockAddr>> =
        try {
            val currentFd = fd ?: throw IllegalStateException("Socket is closed")
            val client = Socket2Native.accept(currentFd)
            val newFd = client.fd as Int
            val peer = decodeJsAddr(client.address)
            Result.success(Pair(SysSocket(newFd), peer))
        } catch (e: Throwable) {
            Result.failure(e)
        }

    actual fun localAddr(): Result<SockAddr> =
        try {
            val currentFd = fd ?: throw IllegalStateException("Socket is closed")
            val jsAddr = Socket2Native.getsockname(currentFd)
            Result.success(decodeJsAddr(jsAddr))
        } catch (e: Throwable) {
            Result.failure(e)
        }

    actual fun peerAddr(): Result<SockAddr> =
        try {
            val currentFd = fd ?: throw IllegalStateException("Socket is closed")
            val jsAddr = Socket2Native.getpeername(currentFd)
            Result.success(decodeJsAddr(jsAddr))
        } catch (e: Throwable) {
            Result.failure(e)
        }

    actual fun type(): Result<SocketType> =
        Result.failure(UnsupportedOperationException("type not supported via N-API yet"))

    actual fun tryClone(): Result<SysSocket> =
        Result.failure(UnsupportedOperationException("tryClone not supported via N-API yet"))

    actual fun setNonblocking(nonblocking: Boolean): Result<Unit> =
        Result.success(Unit)

    actual fun nonblocking(): Result<Boolean> =
        Result.success(false)

    actual fun shutdown(how: Shutdown): Result<Unit> =
        try {
            val currentFd = fd ?: throw IllegalStateException("Socket is closed")
            val howValue =
                when (how) {
                    Shutdown.Read -> 0
                    Shutdown.Write -> 1
                    Shutdown.Both -> 2
                }
            Socket2Native.shutdown(currentFd, howValue)
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }

    actual fun recv(
        buffer: ByteArray,
        flags: Int,
    ): Result<Int> =
        try {
            val currentFd = fd ?: throw IllegalStateException("Socket is closed")
            val buf = Socket2Native.recv(currentFd, buffer.size, flags)
            val bytesRead = buf.length as Int
            for (i in 0 until bytesRead) {
                buffer[i] = (buf[i] as Number).toByte()
            }
            Result.success(bytesRead)
        } catch (e: Throwable) {
            Result.failure(e)
        }

    actual fun recvFrom(
        buffer: ByteArray,
        flags: Int,
    ): Result<Pair<Int, SockAddr>> =
        try {
            val currentFd = fd ?: throw IllegalStateException("Socket is closed")
            val res = Socket2Native.recvfrom(currentFd, buffer.size, flags)
            val buf = res.data
            val bytesRead = buf.length as Int
            for (i in 0 until bytesRead) {
                buffer[i] = (buf[i] as Number).toByte()
            }
            val sender = decodeJsAddr(res.address)
            Result.success(Pair(bytesRead, sender))
        } catch (e: Throwable) {
            Result.failure(e)
        }

    actual fun send(
        buffer: ByteArray,
        flags: Int,
    ): Result<Int> =
        try {
            val currentFd = fd ?: throw IllegalStateException("Socket is closed")
            val jsBuffer: dynamic = js("Buffer.from(buffer)")
            val sent = Socket2Native.send(currentFd, jsBuffer, flags)
            Result.success(sent)
        } catch (e: Throwable) {
            Result.failure(e)
        }

    actual fun sendTo(
        buffer: ByteArray,
        flags: Int,
        address: SockAddr,
    ): Result<Int> =
        try {
            val currentFd = fd ?: throw IllegalStateException("Socket is closed")
            val jsBuffer: dynamic = js("Buffer.from(buffer)")
            val sent = Socket2Native.sendto(currentFd, jsBuffer, flags, populateJsAddr(address))
            Result.success(sent)
        } catch (e: Throwable) {
            Result.failure(e)
        }

    actual fun close(): Result<Unit> {
        val currentFd = fd ?: return Result.failure(IllegalStateException("Socket already closed"))
        fd = null
        return try {
            Socket2Native.close(currentFd)
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    actual fun takeError(): Result<Int?> = Result.success(null)

    actual fun reuseAddress(): Result<Boolean> = Result.success(false)

    actual fun setReuseAddress(reuse: Boolean): Result<Unit> = Result.success(Unit)

    actual fun reusePort(): Result<Boolean> = Result.success(false)

    actual fun setReusePort(reuse: Boolean): Result<Unit> = Result.success(Unit)

    actual fun broadcast(): Result<Boolean> = Result.success(false)

    actual fun setBroadcast(broadcast: Boolean): Result<Unit> = Result.success(Unit)

    actual fun keepalive(): Result<Boolean> = Result.success(false)

    actual fun setKeepalive(keepalive: Boolean): Result<Unit> = Result.success(Unit)

    actual fun sendBufferSize(): Result<Int> = Result.success(65536)

    actual fun setSendBufferSize(size: Int): Result<Unit> = Result.success(Unit)

    actual fun recvBufferSize(): Result<Int> = Result.success(65536)

    actual fun setRecvBufferSize(size: Int): Result<Unit> = Result.success(Unit)

    actual fun nodelay(): Result<Boolean> = Result.success(false)

    actual fun setNodelay(nodelay: Boolean): Result<Unit> = Result.success(Unit)

    actual fun ttl(): Result<UInt> = Result.success(64u)

    actual fun setTtl(ttl: UInt): Result<Unit> = Result.success(Unit)

    actual companion object {
        actual fun fromRaw(raw: Int): SysSocket = SysSocket(raw)

        actual fun new(
            domain: Domain,
            type: SocketType,
            protocol: SocketProtocol?,
        ): Result<SysSocket> =
            try {
                val protocolValue = protocol?.value ?: 0
                val rawFd = Socket2Native.socket(domain.value, type.value, protocolValue)
                Result.success(SysSocket(rawFd))
            } catch (e: Throwable) {
                Result.failure(e)
            }

        actual fun pair(
            domain: Domain,
            type: SocketType,
            protocol: SocketProtocol?,
        ): Result<Pair<SysSocket, SysSocket>> =
            Result.failure(UnsupportedOperationException("socketpair not available on Node.js"))
    }
}

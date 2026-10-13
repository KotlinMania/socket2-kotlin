#ifndef SOCKET2_WRAPPER_H
#define SOCKET2_WRAPPER_H

#include <stdint.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>

#ifdef _WIN32
#include <winsock2.h>
#include <ws2tcpip.h>
#else
#include <sys/socket.h>
#include <sys/un.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <unistd.h>
#include <fcntl.h>
#endif

#ifdef __cplusplus
extern "C" {
#endif

typedef struct Socket2AddrStorage {
    struct sockaddr_storage storage;
} Socket2AddrStorage;

static inline Socket2AddrStorage* socket2_addr_storage_new(void) {
    Socket2AddrStorage* addr = (Socket2AddrStorage*)malloc(sizeof(Socket2AddrStorage));
    if (addr) {
        memset(&addr->storage, 0, sizeof(addr->storage));
    }
    return addr;
}

static inline void socket2_addr_storage_free(Socket2AddrStorage* storage) {
    free(storage);
}

static inline uint16_t socket2_addr_storage_get_family(const Socket2AddrStorage* storage) {
    return storage->storage.ss_family;
}

static inline void socket2_addr_storage_set_family(Socket2AddrStorage* storage, uint16_t family) {
    storage->storage.ss_family = family;
}

static inline struct sockaddr* socket2_addr_storage_as_sockaddr(Socket2AddrStorage* storage) {
    return (struct sockaddr*)(&storage->storage);
}

static inline struct sockaddr_storage* socket2_addr_storage_as_storage(Socket2AddrStorage* storage) {
    return (struct sockaddr_storage*)(&storage->storage);
}

static inline void socket2_addr_storage_from_raw(Socket2AddrStorage* dest, const struct sockaddr_storage* src, uint32_t len) {
    memcpy(&dest->storage, src, len);
}

static inline void socket2_addr_storage_set_padding(Socket2AddrStorage* storage, const uint8_t* padding, uint32_t len) {
    if (len > sizeof(storage->storage) - 2) {
        len = sizeof(storage->storage) - 2;
    }
    memcpy(((char*)&storage->storage) + 2, padding, len);
}

static inline void socket2_addr_storage_get_padding(const Socket2AddrStorage* storage, uint8_t* padding, uint32_t len) {
    if (len > sizeof(storage->storage) - 2) {
        len = sizeof(storage->storage) - 2;
    }
    memcpy(padding, ((const char*)&storage->storage) + 2, len);
}

static inline int socket2_socket(int domain, int type, int protocol) {
    return socket(domain, type, protocol);
}

static inline int socket2_bind(int sockfd, Socket2AddrStorage* addr, uint32_t addrlen) {
#ifdef _WIN32
    return bind(sockfd, socket2_addr_storage_as_sockaddr(addr), (int)addrlen);
#else
    return bind(sockfd, socket2_addr_storage_as_sockaddr(addr), addrlen);
#endif
}

static inline int socket2_connect(int sockfd, Socket2AddrStorage* addr, uint32_t addrlen) {
#ifdef _WIN32
    return connect(sockfd, socket2_addr_storage_as_sockaddr(addr), (int)addrlen);
#else
    return connect(sockfd, socket2_addr_storage_as_sockaddr(addr), addrlen);
#endif
}

static inline int socket2_listen(int sockfd, int backlog) {
    return listen(sockfd, backlog);
}

static inline int socket2_accept(int sockfd, Socket2AddrStorage* addr, uint32_t* addrlen) {
#ifdef _WIN32
    int len = (int)(*addrlen);
    int result = accept(sockfd, socket2_addr_storage_as_sockaddr(addr), &len);
    *addrlen = (uint32_t)len;
    return result;
#else
    socklen_t len = *addrlen;
    int result = accept(sockfd, socket2_addr_storage_as_sockaddr(addr), &len);
    *addrlen = len;
    return result;
#endif
}

static inline int socket2_shutdown(int sockfd, int how) {
    return shutdown(sockfd, how);
}

static inline int64_t socket2_recv(int sockfd, void* buf, uint64_t len, int flags) {
#ifdef _WIN32
    return recv(sockfd, (char*)buf, (int)len, flags);
#else
    return recv(sockfd, buf, len, flags);
#endif
}

static inline int64_t socket2_send(int sockfd, const void* buf, uint64_t len, int flags) {
#ifdef _WIN32
    return send(sockfd, (const char*)buf, (int)len, flags);
#else
    return send(sockfd, buf, len, flags);
#endif
}

static inline int socket2_close(int fd) {
#ifdef _WIN32
    return closesocket(fd);
#else
    return close(fd);
#endif
}

static inline int socket2_get_errno(void) {
#ifdef _WIN32
    return WSAGetLastError();
#else
    return errno;
#endif
}

static inline const char* socket2_get_error_string(int errnum) {
    return strerror(errnum);
}

static inline int socket2_getsockname(int sockfd, Socket2AddrStorage* addr, uint32_t* addrlen) {
#ifdef _WIN32
    int len = (int)(*addrlen);
    int result = getsockname(sockfd, socket2_addr_storage_as_sockaddr(addr), &len);
    *addrlen = (uint32_t)len;
    return result;
#else
    socklen_t len = *addrlen;
    int result = getsockname(sockfd, socket2_addr_storage_as_sockaddr(addr), &len);
    *addrlen = len;
    return result;
#endif
}

static inline int socket2_getpeername(int sockfd, Socket2AddrStorage* addr, uint32_t* addrlen) {
#ifdef _WIN32
    int len = (int)(*addrlen);
    int result = getpeername(sockfd, socket2_addr_storage_as_sockaddr(addr), &len);
    *addrlen = (uint32_t)len;
    return result;
#else
    socklen_t len = *addrlen;
    int result = getpeername(sockfd, socket2_addr_storage_as_sockaddr(addr), &len);
    *addrlen = len;
    return result;
#endif
}

static inline int socket2_socketpair(int domain, int type, int protocol, int sv[2]) {
#ifdef _WIN32
    errno = ENOSYS;
    return -1;
#else
    return socketpair(domain, type, protocol, sv);
#endif
}

static inline int socket2_getsockopt(int sockfd, int level, int optname, void* optval, uint32_t* optlen) {
#ifdef _WIN32
    int len = (int)(*optlen);
    int result = getsockopt(sockfd, level, optname, (char*)optval, &len);
    *optlen = (uint32_t)len;
    return result;
#else
    socklen_t len = *optlen;
    int result = getsockopt(sockfd, level, optname, optval, &len);
    *optlen = len;
    return result;
#endif
}

static inline int socket2_setsockopt(int sockfd, int level, int optname, const void* optval, uint32_t optlen) {
#ifdef _WIN32
    return setsockopt(sockfd, level, optname, (const char*)optval, (int)optlen);
#else
    return setsockopt(sockfd, level, optname, optval, optlen);
#endif
}

static inline int64_t socket2_recvfrom(int sockfd, void* buf, uint64_t len, int flags, Socket2AddrStorage* addr, uint32_t* addrlen) {
#ifdef _WIN32
    int slen = addrlen ? (int)(*addrlen) : 0;
    int result = recvfrom(sockfd, (char*)buf, (int)len, flags, addr ? socket2_addr_storage_as_sockaddr(addr) : NULL, addrlen ? &slen : NULL);
    if (addrlen) *addrlen = (uint32_t)slen;
    return result;
#else
    socklen_t slen = addrlen ? *addrlen : 0;
    ssize_t result = recvfrom(sockfd, buf, len, flags, addr ? socket2_addr_storage_as_sockaddr(addr) : NULL, addrlen ? &slen : NULL);
    if (addrlen) *addrlen = slen;
    return result;
#endif
}

static inline int64_t socket2_sendto(int sockfd, const void* buf, uint64_t len, int flags, const Socket2AddrStorage* addr, uint32_t addrlen) {
#ifdef _WIN32
    return sendto(sockfd, (const char*)buf, (int)len, flags, addr ? (const struct sockaddr*)(&addr->storage) : NULL, (int)addrlen);
#else
    return sendto(sockfd, buf, len, flags, addr ? (const struct sockaddr*)(&addr->storage) : NULL, addrlen);
#endif
}

static inline int socket2_try_clone(int sockfd) {
#ifdef _WIN32
    WSAPROTOCOL_INFOW info;
    if (WSADuplicateSocketW((SOCKET)sockfd, GetCurrentProcessId(), &info) != 0) {
        return -1;
    }
    return (int)WSASocketW(FROM_PROTOCOL_INFO, FROM_PROTOCOL_INFO, FROM_PROTOCOL_INFO, &info, 0, WSA_FLAG_NO_HANDLE_INHERIT);
#else
#ifdef F_DUPFD_CLOEXEC
    return fcntl(sockfd, F_DUPFD_CLOEXEC, 0);
#else
    return dup(sockfd);
#endif
#endif
}

static inline int socket2_set_nonblocking(int sockfd, int nonblocking) {
#ifdef _WIN32
    u_long mode = nonblocking ? 1 : 0;
    return ioctlsocket(sockfd, FIONBIO, &mode);
#else
    int flags = fcntl(sockfd, F_GETFL, 0);
    if (flags == -1) return -1;
    if (nonblocking) {
        flags |= O_NONBLOCK;
    } else {
        flags &= ~O_NONBLOCK;
    }
    return fcntl(sockfd, F_SETFL, flags);
#endif
}

static inline int socket2_get_nonblocking(int sockfd) {
#ifdef _WIN32
    return -1;
#else
    int flags = fcntl(sockfd, F_GETFL, 0);
    if (flags == -1) return -1;
    return (flags & O_NONBLOCK) ? 1 : 0;
#endif
}

static inline int socket2_opt_sol_socket(void) { return SOL_SOCKET; }
static inline int socket2_opt_so_reuseaddr(void) { return SO_REUSEADDR; }
#ifdef SO_REUSEPORT
static inline int socket2_opt_so_reuseport(void) { return SO_REUSEPORT; }
#else
static inline int socket2_opt_so_reuseport(void) { return -1; }
#endif
static inline int socket2_opt_so_broadcast(void) { return SO_BROADCAST; }
static inline int socket2_opt_so_keepalive(void) { return SO_KEEPALIVE; }
static inline int socket2_opt_so_sndbuf(void) { return SO_SNDBUF; }
static inline int socket2_opt_so_rcvbuf(void) { return SO_RCVBUF; }
static inline int socket2_opt_so_error(void) { return SO_ERROR; }
static inline int socket2_opt_so_type(void) { return SO_TYPE; }
static inline int socket2_opt_ipproto_tcp(void) { return IPPROTO_TCP; }
static inline int socket2_opt_tcp_nodelay(void) { return TCP_NODELAY; }
static inline int socket2_opt_ipproto_ip(void) { return IPPROTO_IP; }
static inline int socket2_opt_ip_ttl(void) { return IP_TTL; }

#ifdef __cplusplus
}
#endif

#endif // SOCKET2_WRAPPER_H

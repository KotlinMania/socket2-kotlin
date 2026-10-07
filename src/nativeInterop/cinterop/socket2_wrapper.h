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
#include <netinet/in.h>
#include <unistd.h>
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

#ifdef __cplusplus
}
#endif

#endif // SOCKET2_WRAPPER_H

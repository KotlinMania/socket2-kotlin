#include "socket2_wrapper.h"
#include <cstring>
#include <cstdint>
#ifdef _WIN32
#include <io.h>
#else
#include <unistd.h>
#endif
#include <errno.h>

// Internal structure - wraps sockaddr_storage
struct Socket2AddrStorage {
    struct sockaddr_storage storage;

    Socket2AddrStorage() {
        std::memset(&storage, 0, sizeof(storage));
    }
};

extern "C" {

Socket2AddrStorage* socket2_addr_storage_new() {
    return new Socket2AddrStorage();
}

void socket2_addr_storage_free(Socket2AddrStorage* storage) {
    delete storage;
}

uint16_t socket2_addr_storage_get_family(const Socket2AddrStorage* storage) {
    return storage->storage.ss_family;
}

void socket2_addr_storage_set_family(Socket2AddrStorage* storage, uint16_t family) {
    storage->storage.ss_family = family;
}

struct sockaddr* socket2_addr_storage_as_sockaddr(Socket2AddrStorage* storage) {
    return reinterpret_cast<struct sockaddr*>(&storage->storage);
}

struct sockaddr_storage* socket2_addr_storage_as_storage(Socket2AddrStorage* storage) {
    return &storage->storage;
}

void socket2_addr_storage_from_raw(Socket2AddrStorage* dest, const struct sockaddr_storage* src, uint32_t len) {
    std::memcpy(&dest->storage, src, len);
}

void socket2_addr_storage_set_padding(Socket2AddrStorage* storage, const uint8_t* padding, uint32_t len) {
    if (len > sizeof(storage->storage) - 2) {
        len = sizeof(storage->storage) - 2;
    }
    std::memcpy(reinterpret_cast<char*>(&storage->storage) + 2, padding, len);
}

void socket2_addr_storage_get_padding(const Socket2AddrStorage* storage, uint8_t* padding, uint32_t len) {
    if (len > sizeof(storage->storage) - 2) {
        len = sizeof(storage->storage) - 2;
    }
    std::memcpy(padding, reinterpret_cast<const char*>(&storage->storage) + 2, len);
}

// Socket syscalls - direct pass-through
int socket2_socket(int domain, int type, int protocol) {
    return ::socket(domain, type, protocol);
}

int socket2_bind(int sockfd, Socket2AddrStorage* addr, uint32_t addrlen) {
    return ::bind(sockfd, socket2_addr_storage_as_sockaddr(addr), static_cast<int>(addrlen));
}

int socket2_connect(int sockfd, Socket2AddrStorage* addr, uint32_t addrlen) {
    return ::connect(sockfd, socket2_addr_storage_as_sockaddr(addr), static_cast<int>(addrlen));
}

int socket2_listen(int sockfd, int backlog) {
    return ::listen(sockfd, backlog);
}

int socket2_accept(int sockfd, Socket2AddrStorage* addr, uint32_t* addrlen) {
#ifdef _WIN32
    int len = static_cast<int>(*addrlen);
    int result = ::accept(sockfd, socket2_addr_storage_as_sockaddr(addr), &len);
    *addrlen = static_cast<uint32_t>(len);
    return result;
#else
    socklen_t len = *addrlen;
    int result = ::accept(sockfd, socket2_addr_storage_as_sockaddr(addr), &len);
    *addrlen = len;
    return result;
#endif
}

int socket2_shutdown(int sockfd, int how) {
    return ::shutdown(sockfd, how);
}

int64_t socket2_recv(int sockfd, void* buf, uint64_t len, int flags) {
#ifdef _WIN32
    return ::recv(sockfd, static_cast<char*>(buf), static_cast<int>(len), flags);
#else
    return ::recv(sockfd, buf, len, flags);
#endif
}

int64_t socket2_send(int sockfd, const void* buf, uint64_t len, int flags) {
#ifdef _WIN32
    return ::send(sockfd, static_cast<const char*>(buf), static_cast<int>(len), flags);
#else
    return ::send(sockfd, buf, len, flags);
#endif
}

int socket2_close(int fd) {
#ifdef _WIN32
    return ::closesocket(fd);
#else
    return ::close(fd);
#endif
}

int socket2_get_errno() {
#ifdef _WIN32
    return ::WSAGetLastError();
#else
    return errno;
#endif
}

const char* socket2_get_error_string(int errnum) {
    return ::strerror(errnum);
}

} // extern "C"

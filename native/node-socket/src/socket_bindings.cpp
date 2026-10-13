// N-API bindings for socket2-kotlin
// Direct POSIX socket syscalls exposed to Node.js
//
// This follows the same pattern as our Kotlin/Native implementation:
// Direct C library calls with no abstractions.

#include <napi.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <string.h>

namespace socket2_native {

/**
 * Create a new socket.
 *
 * Direct binding to socket(2) syscall.
 *
 * JS signature: socket(domain: number, type: number, protocol: number): number
 * Returns: socket file descriptor or throws on error
 */
Napi::Value Socket(const Napi::CallbackInfo& info) {
    Napi::Env env = info.Env();

    if (info.Length() < 3) {
        Napi::TypeError::New(env, "Expected 3 arguments: domain, type, protocol")
            .ThrowAsJavaScriptException();
        return env.Null();
    }

    if (!info[0].IsNumber() || !info[1].IsNumber() || !info[2].IsNumber()) {
        Napi::TypeError::New(env, "Arguments must be numbers")
            .ThrowAsJavaScriptException();
        return env.Null();
    }

    int domain = info[0].As<Napi::Number>().Int32Value();
    int type = info[1].As<Napi::Number>().Int32Value();
    int protocol = info[2].As<Napi::Number>().Int32Value();

    int fd = ::socket(domain, type, protocol);

    if (fd == -1) {
        std::string error = "socket() failed: ";
        error += strerror(errno);
        Napi::Error::New(env, error).ThrowAsJavaScriptException();
        return env.Null();
    }

    return Napi::Number::New(env, fd);
}

/**
 * Bind a socket to an address.
 *
 * Direct binding to bind(2) syscall.
 *
 * JS signature: bind(fd: number, address: object): void
 * address object: { family: number, port: number, addr: string }
 */
Napi::Value Bind(const Napi::CallbackInfo& info) {
    Napi::Env env = info.Env();

    if (info.Length() < 2) {
        Napi::TypeError::New(env, "Expected 2 arguments: fd, address")
            .ThrowAsJavaScriptException();
        return env.Null();
    }

    if (!info[0].IsNumber() || !info[1].IsObject()) {
        Napi::TypeError::New(env, "Invalid argument types")
            .ThrowAsJavaScriptException();
        return env.Null();
    }

    int fd = info[0].As<Napi::Number>().Int32Value();
    Napi::Object addrObj = info[1].As<Napi::Object>();

    int family = addrObj.Get("family").As<Napi::Number>().Int32Value();

    if (family == AF_INET) {
        struct sockaddr_in addr;
        memset(&addr, 0, sizeof(addr));
        addr.sin_family = AF_INET;
        addr.sin_port = htons(addrObj.Get("port").As<Napi::Number>().Uint32Value());

        std::string addrStr = addrObj.Get("addr").As<Napi::String>().Utf8Value();
        if (inet_pton(AF_INET, addrStr.c_str(), &addr.sin_addr) <= 0) {
            Napi::Error::New(env, "Invalid IPv4 address").ThrowAsJavaScriptException();
            return env.Null();
        }

        if (::bind(fd, (struct sockaddr*)&addr, sizeof(addr)) == -1) {
            std::string error = "bind() failed: ";
            error += strerror(errno);
            Napi::Error::New(env, error).ThrowAsJavaScriptException();
            return env.Null();
        }
    } else if (family == AF_INET6) {
        struct sockaddr_in6 addr;
        memset(&addr, 0, sizeof(addr));
        addr.sin6_family = AF_INET6;
        addr.sin6_port = htons(addrObj.Get("port").As<Napi::Number>().Uint32Value());

        std::string addrStr = addrObj.Get("addr").As<Napi::String>().Utf8Value();
        if (inet_pton(AF_INET6, addrStr.c_str(), &addr.sin6_addr) <= 0) {
            Napi::Error::New(env, "Invalid IPv6 address").ThrowAsJavaScriptException();
            return env.Null();
        }

        if (::bind(fd, (struct sockaddr*)&addr, sizeof(addr)) == -1) {
            std::string error = "bind() failed: ";
            error += strerror(errno);
            Napi::Error::New(env, error).ThrowAsJavaScriptException();
            return env.Null();
        }
    } else {
        Napi::Error::New(env, "Unsupported address family").ThrowAsJavaScriptException();
        return env.Null();
    }

    return env.Undefined();
}

/**
 * Connect a socket to an address.
 *
 * Direct binding to connect(2) syscall.
 */
Napi::Value Connect(const Napi::CallbackInfo& info) {
    Napi::Env env = info.Env();

    if (info.Length() < 2) {
        Napi::TypeError::New(env, "Expected 2 arguments: fd, address")
            .ThrowAsJavaScriptException();
        return env.Null();
    }

    int fd = info[0].As<Napi::Number>().Int32Value();
    Napi::Object addrObj = info[1].As<Napi::Object>();

    int family = addrObj.Get("family").As<Napi::Number>().Int32Value();

    if (family == AF_INET) {
        struct sockaddr_in addr;
        memset(&addr, 0, sizeof(addr));
        addr.sin_family = AF_INET;
        addr.sin_port = htons(addrObj.Get("port").As<Napi::Number>().Uint32Value());

        std::string addrStr = addrObj.Get("addr").As<Napi::String>().Utf8Value();
        inet_pton(AF_INET, addrStr.c_str(), &addr.sin_addr);

        if (::connect(fd, (struct sockaddr*)&addr, sizeof(addr)) == -1) {
            std::string error = "connect() failed: ";
            error += strerror(errno);
            Napi::Error::New(env, error).ThrowAsJavaScriptException();
            return env.Null();
        }
    } else if (family == AF_INET6) {
        struct sockaddr_in6 addr;
        memset(&addr, 0, sizeof(addr));
        addr.sin6_family = AF_INET6;
        addr.sin6_port = htons(addrObj.Get("port").As<Napi::Number>().Uint32Value());

        std::string addrStr = addrObj.Get("addr").As<Napi::String>().Utf8Value();
        inet_pton(AF_INET6, addrStr.c_str(), &addr.sin6_addr);

        if (::connect(fd, (struct sockaddr*)&addr, sizeof(addr)) == -1) {
            std::string error = "connect() failed: ";
            error += strerror(errno);
            Napi::Error::New(env, error).ThrowAsJavaScriptException();
            return env.Null();
        }
    }

    return env.Undefined();
}

/**
 * Listen for connections on a socket.
 *
 * Direct binding to listen(2) syscall.
 */
Napi::Value Listen(const Napi::CallbackInfo& info) {
    Napi::Env env = info.Env();

    int fd = info[0].As<Napi::Number>().Int32Value();
    int backlog = info[1].As<Napi::Number>().Int32Value();

    if (::listen(fd, backlog) == -1) {
        std::string error = "listen() failed: ";
        error += strerror(errno);
        Napi::Error::New(env, error).ThrowAsJavaScriptException();
        return env.Null();
    }

    return env.Undefined();
}

/**
 * Accept a connection on a socket.
 *
 * Direct binding to accept(2) syscall.
 * Returns: { fd: number, address: object }
 */
Napi::Value Accept(const Napi::CallbackInfo& info) {
    Napi::Env env = info.Env();

    int fd = info[0].As<Napi::Number>().Int32Value();

    struct sockaddr_storage addr;
    socklen_t addrlen = sizeof(addr);

    int newFd = ::accept(fd, (struct sockaddr*)&addr, &addrlen);

    if (newFd == -1) {
        std::string error = "accept() failed: ";
        error += strerror(errno);
        Napi::Error::New(env, error).ThrowAsJavaScriptException();
        return env.Null();
    }

    Napi::Object result = Napi::Object::New(env);
    result.Set("fd", Napi::Number::New(env, newFd));

    // TODO: Parse address back into object
    Napi::Object addrObj = Napi::Object::New(env);
    addrObj.Set("family", Napi::Number::New(env, addr.ss_family));
    result.Set("address", addrObj);

    return result;
}

/**
 * Shutdown part of a socket connection.
 *
 * Direct binding to shutdown(2) syscall.
 */
Napi::Value Shutdown(const Napi::CallbackInfo& info) {
    Napi::Env env = info.Env();

    int fd = info[0].As<Napi::Number>().Int32Value();
    int how = info[1].As<Napi::Number>().Int32Value();

    if (::shutdown(fd, how) == -1) {
        std::string error = "shutdown() failed: ";
        error += strerror(errno);
        Napi::Error::New(env, error).ThrowAsJavaScriptException();
        return env.Null();
    }

    return env.Undefined();
}

/**
 * Close a socket.
 *
 * Direct binding to close(2) syscall.
 */
Napi::Value Close(const Napi::CallbackInfo& info) {
    Napi::Env env = info.Env();

    int fd = info[0].As<Napi::Number>().Int32Value();

    if (::close(fd) == -1) {
        std::string error = "close() failed: ";
        error += strerror(errno);
        Napi::Error::New(env, error).ThrowAsJavaScriptException();
        return env.Null();
    }

    return env.Undefined();
}

/**
 * Receive data from a socket.
 *
 * Direct binding to recv(2) syscall.
 * Returns: Buffer
 */
Napi::Value Recv(const Napi::CallbackInfo& info) {
    Napi::Env env = info.Env();

    int fd = info[0].As<Napi::Number>().Int32Value();
    int len = info[1].As<Napi::Number>().Int32Value();
    int flags = info[2].As<Napi::Number>().Int32Value();

    Napi::Buffer<uint8_t> buffer = Napi::Buffer<uint8_t>::New(env, len);

    ssize_t bytesRead = ::recv(fd, buffer.Data(), len, flags);

    if (bytesRead == -1) {
        std::string error = "recv() failed: ";
        error += strerror(errno);
        Napi::Error::New(env, error).ThrowAsJavaScriptException();
        return env.Null();
    }

    // Return a buffer with the actual bytes read
    return Napi::Buffer<uint8_t>::Copy(env, buffer.Data(), bytesRead);
}

/**
 * Send data on a socket.
 *
 * Direct binding to send(2) syscall.
 * Returns: number of bytes sent
 */
Napi::Value Send(const Napi::CallbackInfo& info) {
    Napi::Env env = info.Env();

    int fd = info[0].As<Napi::Number>().Int32Value();
    Napi::Buffer<uint8_t> buffer = info[1].As<Napi::Buffer<uint8_t>>();
    int flags = info[2].As<Napi::Number>().Int32Value();

    ssize_t bytesSent = ::send(fd, buffer.Data(), buffer.Length(), flags);

    if (bytesSent == -1) {
        std::string error = "send() failed: ";
        error += strerror(errno);
        Napi::Error::New(env, error).ThrowAsJavaScriptException();
        return env.Null();
    }

    return Napi::Number::New(env, bytesSent);
}

/**
 * Tests for the existence of a socket file at socket_path.
 *
 * Direct binding to access(2) with F_OK.
 * JS signature: socket_exists(socket_path: string): boolean
 * Returns: boolean indicating whether socket file exists
 */
Napi::Value SocketExists(const Napi::CallbackInfo& info) {
    Napi::Env env = info.Env();

    if (info.Length() < 1 || !info[0].IsString()) {
        Napi::TypeError::New(env, "Expected string argument: socket_path")
            .ThrowAsJavaScriptException();
        return env.Null();
    }

    std::string path = info[0].As<Napi::String>().Utf8Value();
    int exists = (::access(path.c_str(), F_OK) == 0) ? 1 : 0;
    return Napi::Boolean::New(env, exists != 0);
}

/**
 * Initialize the N-API module.
 * Exports all socket functions to JavaScript.
 */
Napi::Object Init(Napi::Env env, Napi::Object exports) {
    exports.Set("socket", Napi::Function::New(env, Socket));
    exports.Set("bind", Napi::Function::New(env, Bind));
    exports.Set("connect", Napi::Function::New(env, Connect));
    exports.Set("listen", Napi::Function::New(env, Listen));
    exports.Set("accept", Napi::Function::New(env, Accept));
    exports.Set("shutdown", Napi::Function::New(env, Shutdown));
    exports.Set("close", Napi::Function::New(env, Close));
    exports.Set("recv", Napi::Function::New(env, Recv));
    exports.Set("send", Napi::Function::New(env, Send));
    exports.Set("socket_exists", Napi::Function::New(env, SocketExists));
    exports.Set("socketExists", Napi::Function::New(env, SocketExists));

    // Export constants
    exports.Set("AF_INET", Napi::Number::New(env, AF_INET));
    exports.Set("AF_INET6", Napi::Number::New(env, AF_INET6));
    exports.Set("SOCK_STREAM", Napi::Number::New(env, SOCK_STREAM));
    exports.Set("SOCK_DGRAM", Napi::Number::New(env, SOCK_DGRAM));
    exports.Set("SHUT_RD", Napi::Number::New(env, SHUT_RD));
    exports.Set("SHUT_WR", Napi::Number::New(env, SHUT_WR));
    exports.Set("SHUT_RDWR", Napi::Number::New(env, SHUT_RDWR));

    return exports;
}

NODE_API_MODULE(socket2_native, Init)

} // namespace socket2_native

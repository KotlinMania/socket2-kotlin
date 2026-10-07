// Entry point for the native N-API module
// This is loaded by Node.js and provides the socket2 native bindings

// Hide binary .node require from webpack static analysis for browser targets
const req = (typeof __webpack_require__ === 'function')
    ? null
    : (new Function('return typeof require === "function" ? require : null'))();

const defaultConstants = {
    AF_INET: 2,
    AF_INET6: (typeof process !== 'undefined' && process.platform === 'darwin') ? 30 : 10,
    SOCK_STREAM: 1,
    SOCK_DGRAM: 2,
    SOCK_RAW: 3,
    SOCK_SEQPACKET: 5,
    SOCK_DCCP: 6,
    SHUT_RD: 0,
    SHUT_WR: 1,
    SHUT_RDWR: 2,
};

let nativeModule = null;
if (req && typeof __dirname !== 'undefined') {
    const path = req('path');
    try {
        nativeModule = req(path.join(__dirname, 'build/Release/socket2_native.node'));
    } catch (err) {
        try {
            nativeModule = req(path.join(__dirname, 'build/Debug/socket2_native.node'));
        } catch (err2) {
            nativeModule = null;
        }
    }
}

module.exports = Object.assign({}, defaultConstants, nativeModule || {});


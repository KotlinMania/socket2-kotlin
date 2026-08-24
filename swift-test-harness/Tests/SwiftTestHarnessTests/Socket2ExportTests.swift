import Testing
import Socket2

@Suite("Socket2 Swift Export Tests")
struct Socket2ExportTests {
    @Test("Swift module loads and exports SocketType cleanly")
    func swiftModuleLoads() {
        let stream = SocketType.Companion.shared.STREAM
        #expect(stream.value != 0)
        let dgram = SocketType.Companion.shared.DGRAM
        #expect(dgram.value != 0)
        let tcp = SocketProtocol.Companion.shared.TCP
        #expect(tcp.value != 0)
    }
}

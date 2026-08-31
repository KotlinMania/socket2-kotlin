// port-lint: tests socket2/src/sys/unix.rs
package io.github.kotlinmania.socket2

import kotlin.test.Test
import kotlin.test.assertEquals

class ShutdownTest {
    @Test
    fun testShutdownVariants() {
        val values = Shutdown.entries
        assertEquals(3, values.size)
        assertEquals(Shutdown.Read, Shutdown.valueOf("Read"))
        assertEquals(Shutdown.Write, Shutdown.valueOf("Write"))
        assertEquals(Shutdown.Both, Shutdown.valueOf("Both"))
    }
}

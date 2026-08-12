package io.github.kotlinmania.socket2

import kotlin.test.Test

class TypeConversionTest {
    @Test
    fun testUShortArrayBitShift() {
        println("\n=== UShortArray Bit Shift Test ===")

        val ushorts: UShortArray = UShortArray(4)
        ushorts[0] = 0x1234u.toUShort()

        // Access element - what type is it?
        val element = ushorts[0]
        println("Type of ushorts[0]: ${element::class.simpleName}")

        // Try bit shift - this should require explicit toInt() conversion
        // val shifted = element shl 8  // This won't compile!

        // Must explicitly convert to Int for bit operations
        val shiftedInt: Int = element.toInt() shl 8
        println("Explicitly converted to Int for shift: $shiftedInt")
        println("Type after shift: ${shiftedInt::class.simpleName}")

        // Assigning back requires explicit conversion
        // ushorts[1] = shiftedInt  // Won't compile!
        ushorts[1] = shiftedInt.toUShort()
        println("Must explicitly convert back to UShort")
    }

    @Test
    fun testByteArrayBitShift() {
        println("\n=== ByteArray Bit Shift Test ===")

        val bytes: ByteArray = ByteArray(4)
        bytes[0] = 0x12.toByte()

        // Access element - what type is it?
        val element = bytes[0]
        println("Type of bytes[0]: ${element::class.simpleName}")

        // Bit shift on Byte - what happens?
        // bytes[0] = bytes[0] shl 1  // Won't compile - shl not defined on Byte!

        // The issue: you MUST convert to Int
        val asInt: Int = bytes[0].toInt()
        println("bytes[0].toInt() type: ${asInt::class.simpleName}")

        // Bit operations force Int conversion
        val shifted: Int = bytes[0].toInt() and 0xFF // THIS is the implicit conversion!
        println("Type after 'and 0xFF': ${shifted::class.simpleName}")

        // Even just accessing requires masking because of sign extension
        val b: Byte = 0xFF.toByte() // -1 as signed byte
        val asIntSigned: Int = b.toInt() // -1 as signed int
        val asIntUnsigned: Int = b.toInt() and 0xFF // 255 as unsigned

        println("Byte 0xFF as signed Int: $asIntSigned")
        println("Byte 0xFF with 'and 0xFF': $asIntUnsigned")
        println("This is what you warned about - ByteArray forces Int conversions!")
    }

    @Test
    fun testDirectAssignment() {
        println("\n=== Direct Assignment Test ===")

        // UShortArray: type-safe, no conversions
        val ushorts: UShortArray = UShortArray(2)
        val value1: UShort = 0x1234u.toUShort()
        ushorts[0] = value1 // Direct assignment, no conversion
        val retrieved1: UShort = ushorts[0] // Direct retrieval, no conversion
        println("UShort roundtrip: stored=$value1, retrieved=$retrieved1, same=${value1 == retrieved1}")

        // ByteArray: requires conversions for any bit operations
        val bytes: ByteArray = ByteArray(2)
        val value2: Byte = 0x12.toByte()
        bytes[0] = value2 // Direct assignment OK
        val retrieved2: Byte = bytes[0] // Direct retrieval OK

        // But combining two bytes into a short requires Int!
        val combined: Int = (bytes[0].toInt() and 0xFF) or ((bytes[1].toInt() and 0xFF) shl 8)
        println("ByteArray combining bytes requires Int conversion: type=${combined::class.simpleName}")

        // UShortArray doesn't need masking or conversions
        val ushortValue: UShort = ushorts[0]
        println("UShort direct use: type=${ushortValue::class.simpleName}, no Int conversion needed")
    }
}

package com.corbit.binary

import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryStringBuilderTest {

    @Test fun `building empty binary string produces empty BinaryString`() {
        assertEquals(BinaryString.EMPTY, buildBinaryString { })
    }

    @Test fun `bulding binary string from vararg bytes produces correct data`() {
        // arrange
        val data = "Foo Bar".encodeToByteArray()

        // act
        val result = buildBinaryString { append(*data) }

        // assert
        assertEquals("Foo Bar".asBinaryString(), result)
    }

    @Test fun `building binary string from other binary strings produces correct data`() {
        // arrange
        val data = listOf("Foo", " ", "Bar").map(String::asBinaryString)

        // act
        val result = buildBinaryString { data.forEach(this::append) }

        // assert
        assertEquals("Foo Bar".asBinaryString(), result)
    }

    @Test fun `building binary string from Strings produces correct data`() {
        // arrange
        val data = listOf("Foo", " ", "Bar")

        // act
        val result = buildBinaryString { data.forEach(this::append) }

        // assert
        assertEquals("Foo Bar".asBinaryString(), result)
    }

    @Test fun `building binary string from mixed sources produces correct data`() {
        val result = buildBinaryString {
            append(*"Foo".encodeToByteArray())
            append(" ".asBinaryString())
            append("Bar")
        }

        assertEquals("Foo Bar".asBinaryString(), result)
    }
}

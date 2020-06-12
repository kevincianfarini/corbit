package com.corbit.bencoding

import com.corbit.binary.BinaryString
import com.corbit.binary.asBinaryString
import kotlin.test.Test
import kotlin.test.assertEquals

class EncoderTest {

    @Test fun `bencoded int can properly be encoded to binary data`() {
        // arrange
        val data = BencodedInt(123)

        // act
        val result = data.encode()

        // assert
        assertEquals(
            "i123e".asBinaryString(),
            result
        )
    }

    @Test fun `empty bencoded string is encoded to proper binary data`() {
        // arrange
        val data = BencodedString(BinaryString.EMPTY)

        // act
        val result = data.encode()

        // assert
        assertEquals(
            "0:".asBinaryString(),
            result
        )
    }

    @Test fun `bencoded string is encoded to proper binary data`() {
        // arrange
        val data = BencodedString("Middle Earth".asBinaryString())

        // act
        val result = data.encode()

        // assert
        assertEquals(
            "12:Middle Earth".asBinaryString(),
            result
        )
    }

    @Test fun `empty bencoded list is encoded to proper binary data`() {
        // arrange
        val data = BencodedList(emptyList())

        // act
        val result = data.encode()

        // assert
        assertEquals(
            "le".asBinaryString(),
            result
        )
    }

    @Test fun `trivial bencoded list is encoded to proper binary data`() {
        // arrange
        val data = BencodedList(listOf(
            BencodedInt(123),
            BencodedString("eggs".asBinaryString())
        ))

        // act
        val result = data.encode()

        // assert
        assertEquals(
            "li123e4:eggse".asBinaryString(),
            result
        )
    }

    @Test fun `nested bencoded list is encoded to proper binary data`() {
        // arrange
        val data = BencodedList(listOf(
            BencodedList(listOf(
                BencodedInt(123),
                BencodedString("eggs".asBinaryString())
            ))
        ))

        // act
        val result = data.encode()

        // assert
        assertEquals(
            "lli123e4:eggsee".asBinaryString(),
            result
        )
    }

    @Test fun `empty bencoded dictionary is encoded to proper binary data`() {
        // arrange
        val data = BencodedDict(emptyMap())

        // act
        val result = data.encode()

        // assert
        assertEquals(
            "de".asBinaryString(),
            result
        )
    }

    @Test fun `trivial bencoded dictionary is encoded to proper binary data`() {
        // arrange
        val data = BencodedDict(mapOf(
            BencodedString("foo".asBinaryString()) to BencodedInt(123),
            BencodedString("bar".asBinaryString()) to BencodedString("baz".asBinaryString())
        ))

        // act
        val result = data.encode()

        // assert
        assertEquals(
            "d3:fooi123e3:bar3:baze".asBinaryString(),
            result
        )
    }

    @Test fun `nested bencoded dictionary is encoded to proper binary data`() {
        // arrange
        val data = BencodedDict(mapOf(
            BencodedString("map".asBinaryString()) to BencodedDict(mapOf(
                BencodedString("foo".asBinaryString()) to BencodedInt(123),
                BencodedString("bar".asBinaryString()) to BencodedString("baz".asBinaryString())
            ))
        ))

        // act
        val result = data.encode()

        // assert
        assertEquals(
            "d3:mapd3:fooi123e3:bar3:bazee".asBinaryString(),
            result
        )
    }
}

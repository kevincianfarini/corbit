package com.corbit.bencoding

import com.corbit.binary.asBinaryString
import com.ionspin.kotlin.bignum.integer.toBigInteger
import kotlin.test.Test
import kotlin.test.assertEquals

class DecoderTest {

    @Test fun `simple bencoded integer can be decoded`() {
        // arrange
        val data = "i123e".asBinaryString()

        // act
        val result = data.decode()

        // assert
        assertEquals(123.toBigInteger(), (result as BencodedInt).value)
    }

    @Test fun `simple bencoded string can be decoded`() {
        // arrange
        val data = "12:Middle Earth".asBinaryString()

        // act
        val result = data.decode()

        // assert
        assertEquals(
            "Middle Earth".asBinaryString(),
            (result as BencodedString).value
        )
    }

    @Test fun `empty bencoded string can be decoded`() {
        // arrange
        val data = "0:".asBinaryString()

        // act
        val result = data.decode()

        // assert
        assertEquals("".asBinaryString(), (result as BencodedString).value)
    }

    @Test fun `empty bencoded list decodes to empty list`() {
        // arrange
        val data = "le".asBinaryString()

        // act
        val result = data.decode()

        assertEquals(
            BencodedList(emptyList()),
            result
        )
    }

    @Test fun `bencoded string list can properly decode`() {
        // arrange
        val data = "l4:spam4:eggse".asBinaryString()

        // act
        val result = data.decode()

        // assert
        val expected = listOf("spam", "eggs").map { BencodedString(it.asBinaryString()) }
        assertEquals(
            BencodedList(expected),
            result
        )
    }

    @Test fun `mixed type bencoded list can properly be decoded`() {
        // arrange
        val data = "li123e4:spami12e4:eggse".asBinaryString()

        // act
        val result = data.decode()

        // assert
        val expected = listOf(
            BencodedInt(123.toBigInteger()),
            BencodedString("spam".asBinaryString()),
            BencodedInt(12.toBigInteger()),
            BencodedString("eggs".asBinaryString())
        )
        assertEquals(
            BencodedList(expected),
            result
        )
    }

    @Test fun `nested bencoded list can be decoded properly`() {
        // arrange
        val data = "lli123eeli123eee".asBinaryString()

        // act
        val result = data.decode()

        // assert
        assertEquals(
            BencodedList(listOf(
                BencodedList(listOf(BencodedInt(123.toBigInteger()))),
                BencodedList(listOf(BencodedInt(123.toBigInteger())))
            )),
            result
        )
    }

    @Test fun `empty bencoded dictionary is decoded properly`() {
        // arrange
        val data = "de".asBinaryString()

        // act
        val result = data.decode()

        // assert
        assertEquals(BencodedDict(emptyMap()), result)
    }

    @Test fun `bencoded string dictionary is decoded properly`() {
        // arrange
        val data = "d3:cow3:moo4:spam4:eggse".asBinaryString()

        // act
        val result = data.decode()

        // assert
        val expected = mapOf(
            BencodedString("cow".asBinaryString()) to BencodedString("moo".asBinaryString()),
            BencodedString("spam".asBinaryString()) to BencodedString("eggs".asBinaryString())
        )
        assertEquals(BencodedDict(expected), result)
    }

    @Test fun `nested bencoded dictionary is decoded properly`() {
        // arrange
        val data = "d1:adee".asBinaryString()

        // act
        val result = data.decode()

        // assert
        val expected = mapOf(
            BencodedString("a".asBinaryString()) to BencodedDict(emptyMap())
        )
        assertEquals(BencodedDict(expected), result)
    }

    @Test fun `mixed type bencoded dictionaty is decoded properly`() {
        // arrange
        val data = "d4:spaml1:a1:bee".asBinaryString()

        // act
        val result = data.decode()

        // assert
        val expected = mapOf(
            BencodedString("spam".asBinaryString()) to BencodedList(listOf(
                BencodedString("a".asBinaryString()),
                BencodedString("b".asBinaryString())
            ))
        )
        assertEquals(BencodedDict(expected), result)
    }
}

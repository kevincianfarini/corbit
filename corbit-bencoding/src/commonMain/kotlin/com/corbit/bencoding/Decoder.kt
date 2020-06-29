package com.corbit.bencoding

import com.corbit.binary.BinaryString
import com.corbit.binary.slice
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger

internal class Decoder(private val source: BinaryString) {

    private var index: Int = 0
    private val nextByte: Byte get() = source[index]

    fun decode(): BencodedData = when (BencoderToken.fromToken(nextByte)) {
        BencoderToken.INTEGER -> decodeInteger()
        BencoderToken.STRING -> decodeString()
        BencoderToken.LIST -> decodeList()
        BencoderToken.DICT -> decodeDictionary()
        else -> error("Tried to parse from $nextByte")
    }

    private fun read(numBytes: Int): BinaryString = if (numBytes > 0) {
        source.slice(index, index + numBytes - 1).also { index += numBytes }
    } else {
        BinaryString.EMPTY
    }

    private fun readToNextToken(token: BencoderToken): BinaryString {
        val slice = source.slice(startIndex = index)

        return source.slice(
            startIndex = index,
            endIndex = index + slice.indexOf(token.token.first())
        ).also {
            index += it.size
        }
    }

    private fun decodeInteger(): BencodedInt {
        val value: BigInteger = readToNextToken(BencoderToken.END).run {
            slice(1, size - 2).utf8.toBigInteger()
        }

        return BencodedInt(value)
    }

    private fun decodeString(): BencodedString {
        val stringLength = readToNextToken(BencoderToken.STRING_SEPARATOR).run {
            slice(endIndex = size - 2).utf8.toInt()
        }

        return BencodedString(read(stringLength))
    }

    private fun decodeList(): BencodedList {
        readToNextToken(BencoderToken.LIST) // read and discard

        val list: MutableList<BencodedData> = mutableListOf()
        while (BencoderToken.fromToken(nextByte) != BencoderToken.END) {
            list.add(decode())
        }

        readToNextToken(BencoderToken.END) // read and discard
        return BencodedList(list)
    }

    private fun decodeDictionary(): BencodedDict {
        readToNextToken(BencoderToken.DICT) // read and discard

        val dict: MutableMap<BencodedString, BencodedData> = mutableMapOf()
        while (BencoderToken.fromToken(nextByte) != BencoderToken.END) {
            dict[decodeString()] = decode()
        }

        readToNextToken(BencoderToken.END) // read and discard
        return BencodedDict(dict)
    }
}

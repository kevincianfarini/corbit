package com.corbit.bencoding

import com.corbit.binary.BinaryString
import com.corbit.binary.asBinaryString
import com.corbit.binary.buildBinaryString

internal class Encoder(private val source: BencodedData) {

    fun encode(): BinaryString = encodeValue(source)

    private fun encodeValue(value: BencodedData): BinaryString = when (value) {
        is BencodedInt -> encodeInt(value)
        is BencodedString -> encodeString(value)
        is BencodedList -> encodeList(value)
        is BencodedDict -> encodeDict(value)
    }

    private fun encodeInt(data: BencodedInt) = buildBinaryString {
        append(BencoderToken.INTEGER.token)
        append(data.value.toString().asBinaryString())
        append(BencoderToken.END.token)
    }

    private fun encodeString(data: BencodedString) = buildBinaryString {
        append(data.value.size.toString().asBinaryString())
        append(BencoderToken.STRING_SEPARATOR.token)
        append(data.value)
    }

    private fun encodeList(data: BencodedList) = buildBinaryString {
        append(BencoderToken.LIST.token)
        data.forEach { append(encodeValue(it)) }
        append(BencoderToken.END.token)
    }

    private fun encodeDict(data: BencodedDict) = buildBinaryString {
        append(BencoderToken.DICT.token)

        data.forEach { (key, value) ->
            append(encodeValue(key))
            append(encodeValue(value))
        }

        append(BencoderToken.END.token)
    }
}

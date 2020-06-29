package com.corbit.bencoding

import com.corbit.binary.BinaryString
import com.corbit.binary.BinaryStringBuilder
import com.corbit.binary.asBinaryString
import com.corbit.binary.buildBinaryString

internal class Encoder(private val source: BencodedData) {

    fun encode(): BinaryString = buildBinaryString {
        encodeValue(source, this)
    }

    private fun encodeValue(value: BencodedData, builder: BinaryStringBuilder) {
        when (value) {
            is BencodedInt -> encodeInt(value, builder)
            is BencodedString -> encodeString(value, builder)
            is BencodedList -> encodeList(value, builder)
            is BencodedDict -> encodeDict(value, builder)
        }
    }

    private fun encodeInt(data: BencodedInt, builder: BinaryStringBuilder) = with(builder) {
        append(BencoderToken.INTEGER.token)
        append(data.value.toString().asBinaryString())
        append(BencoderToken.END.token)
    }

    private fun encodeString(data: BencodedString, builder: BinaryStringBuilder) = with(builder) {
        append(data.value.size.toString().asBinaryString())
        append(BencoderToken.STRING_SEPARATOR.token)
        append(data.value)
    }

    private fun encodeList(data: BencodedList, builder: BinaryStringBuilder) = with(builder) {
        append(BencoderToken.LIST.token)
        data.forEach { encodeValue(it, builder) }
        append(BencoderToken.END.token)
    }

    private fun encodeDict(data: BencodedDict, builder: BinaryStringBuilder) = with(builder) {
        append(BencoderToken.DICT.token)

        data.forEach { (key, value) ->
            encodeValue(key, builder)
            encodeValue(value, builder)
        }

        append(BencoderToken.END.token)
    }
}

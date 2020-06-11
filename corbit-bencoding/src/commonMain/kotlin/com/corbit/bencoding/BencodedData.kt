package com.corbit.bencoding

import com.corbit.binary.BinaryString

sealed class BencodedData {
    fun encode(): BinaryString = Encoder(this).encode()
}

data class BencodedInt(val value: Long) : BencodedData()

data class BencodedString(val value: BinaryString) : BencodedData()

data class BencodedList(
    private val value: List<BencodedData>
) : BencodedData(), List<BencodedData> by value

data class BencodedDict(
    private val value: Map<BencodedString, BencodedData>
) : BencodedData(), Map<BencodedString, BencodedData> by value

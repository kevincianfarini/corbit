package com.kevincianfarini

sealed class BencodedData

data class BencodedInt(val value: Long) : BencodedData()

data class BencodedString(val value: ByteArray) : BencodedData()

data class BencodedList(
    val value: List<BencodedData>
) : BencodedData(), List<BencodedData> by value

data class BencodedDict(
    val value: Map<BencodedString, BencodedDict>
) : BencodedData(), Map<BencodedString, BencodedData> by value

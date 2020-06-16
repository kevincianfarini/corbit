package com.corbit.binary.internal

import com.corbit.binary.BinaryString
import com.corbit.binary.asBinaryString

internal operator fun CharSequence.component1(): Char = this[0]
internal operator fun CharSequence.component2(): Char = this[1]
internal operator fun CharSequence.component3(): Char? = getOrNull(2)
internal operator fun CharSequence.component4(): Char? = getOrNull(3)

/**
 * Decode the value represented by [this] into a [BinaryString].
 */
internal fun String.decodeHex(): BinaryString {
    require(length % 2 == 0) { "Expected to be byte aligned but has length $length" }
    return toLowerCase().chunked(2) { (first, second) ->
        require(first in HEX_DIGIT_CHARS && second in HEX_DIGIT_CHARS) {
            "Not a hex character 0x$first$second"
        }

        ((HEX_DIGIT_CHARS.indexOf(first) shl 4) or HEX_DIGIT_CHARS.indexOf(second)).toByte()
    }.toByteArray().asBinaryString()
}

/**
 * Decode the Base64 encoded string [this] into a [BinaryString] object.
 */
internal fun String.decodeBase64(): BinaryString {
    require(length % 4 == 0) { "Expected input to be 4 byte aligned but has length $length" }
    val stripped = filter { it != '=' }
    require(stripped.all { it in BASE_64_DIGIT_CHARS }) { "Illegal character in input $this" }

    val size = ((stripped.length / 4) * 3) + when (length - stripped.length) {
        0 -> 0
        1 -> 2
        2 -> 1
        else -> error("Stripped more than two padding characters")
    }

    var index = 0
    val array = ByteArray(size)
    for ((first, second, third, fourth) in stripped.chunked(size = 4)) {
        when {
            third == null && fourth == null -> {
                val firstIndex = BASE_64_DIGIT_CHARS.indexOf(first) and 0x3f shl 6
                val secondIndex = BASE_64_DIGIT_CHARS.indexOf(second) and 0x3f

                val accumulate = firstIndex + secondIndex

                array[index++] = (accumulate shr 4).toByte()
            }
            third != null && fourth == null -> {
                val firstIndex = BASE_64_DIGIT_CHARS.indexOf(first) and 0x3f shl 12
                val secondIndex = BASE_64_DIGIT_CHARS.indexOf(second) and 0x3f shl 6
                val thirdIndex = BASE_64_DIGIT_CHARS.indexOf(third) and 0x3f

                val accumulate = firstIndex + secondIndex + thirdIndex

                array[index++] = ((accumulate and 0x3fc00) shr 10).toByte()
                array[index++] = ((accumulate and 0x3fc) shr 2).toByte()
            }
            third != null && fourth != null -> {
                val firstIndex = BASE_64_DIGIT_CHARS.indexOf(first) and 0x3f shl 18
                val secondIndex = BASE_64_DIGIT_CHARS.indexOf(second) and 0x3f shl 12
                val thirdIndex = BASE_64_DIGIT_CHARS.indexOf(third) and 0x3f shl 6
                val fourthIndex = BASE_64_DIGIT_CHARS.indexOf(fourth) and 0x3f

                val accumulate = firstIndex + secondIndex + thirdIndex + fourthIndex

                array[index++] = (accumulate shr 16 and 0xff).toByte()
                array[index++] = (accumulate shr 8 and 0xff).toByte()
                array[index++] = (accumulate and 0xff).toByte()
            }
        }
    }

    return array.asBinaryString(defensiveCopy = false)
}

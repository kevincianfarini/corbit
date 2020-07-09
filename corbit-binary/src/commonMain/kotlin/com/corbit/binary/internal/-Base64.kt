package com.corbit.binary.internal

import com.corbit.binary.BinaryString
import com.corbit.binary.asBinaryString
import com.corbit.binary.chunked

/**
 * Encode this [BinaryString] as a [Base64](http://www.ietf.org/rfc/rfc2045.txt) text.
 * This implementation omits newline delimiters.
 */
internal fun BinaryString.encodeToBase64(): String {
    val chars = CharArray(size = (size + 2) / 3 * 4)
    var index = 0

    // grab in increments of 3 bytes for 24 bits total.
    for (binary in chunked(size = 3)) {
        when (binary.size) {
            3 -> {
                val first = binary[0].toInt() shl 16
                val second = binary[1].toInt() shl 8
                val third = binary[2].toInt()

                val accumulator = first + second + third

                chars[index++] = BASE_64_DIGIT_CHARS[(accumulator shr 18) and 0x3f]
                chars[index++] = BASE_64_DIGIT_CHARS[(accumulator shr 12) and 0x3f]
                chars[index++] = BASE_64_DIGIT_CHARS[(accumulator shr 6) and 0x3f]
                chars[index++] = BASE_64_DIGIT_CHARS[accumulator and 0x3f]
            }
            2 -> {
                val first = binary[0].toInt() shl 8
                val second = binary[1].toInt()

                val accumulator = (first + second) shl 2 // align to 18 bits

                chars[index++] = BASE_64_DIGIT_CHARS[(accumulator shr 12) and 0x3f]
                chars[index++] = BASE_64_DIGIT_CHARS[(accumulator shr 6) and 0x3f]
                chars[index++] = BASE_64_DIGIT_CHARS[accumulator and 0x3f]
                chars[index++] = '='
            }
            1 -> {
                val first = binary[0].toInt()

                val accumulator = first shl 4 // align to 12 bits

                chars[index++] = BASE_64_DIGIT_CHARS[(accumulator shr 6) and 0x3f]
                chars[index++] = BASE_64_DIGIT_CHARS[accumulator and 0x3f]
                chars[index++] = '='
                chars[index++] = '='
            }
        }
    }

    return String(chars)
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

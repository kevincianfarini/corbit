package com.corbit.binary.internal

import com.corbit.binary.BinaryString
import com.corbit.binary.asBinaryString

/**
 * This [BinaryString] encoded as a hexidecimal string.
 */
internal fun BinaryString.encodeHex(): String {
    val array = CharArray(size * 2)

    var index = 0
    for (byte in this) {
        array[index++] = HEX_DIGIT_CHARS[(byte.toInt() shr 4) and 0xf]
        array[index++] = HEX_DIGIT_CHARS[byte.toInt() and 0xf]
    }

    return String(array)
}

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

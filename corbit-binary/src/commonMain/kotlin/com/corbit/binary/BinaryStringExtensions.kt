package com.corbit.binary

import com.corbit.binary.internal.BASE_64_DIGIT_CHARS
import com.corbit.binary.internal.HEX_DIGIT_CHARS
import kotlin.math.min

/**
 * Get a [BinaryString] wrapper around [this]. In order to ensure immutability of the underlying resource,
 * you should set [defensiveCopy] to true.
 *
 * Note: Utility functions that are cached like [BinaryString.hex] are not guaranteed to be correct if a
 * defensive copy is not used and the underlying resource is mutated.
 */
fun ByteArray.asBinaryString(defensiveCopy: Boolean = true): BinaryString = ByteArrayBinaryString(this, defensiveCopy)

/**
 * Get a copy of the underlying data in this [String] as a [BinaryString]
 */
fun String.asBinaryString(): BinaryString = encodeToByteArray().asBinaryString(false)

/**
 * Exposes access to a [BinaryStringBuilder] which is then converted to a [BinaryString]
 * after [builder] has completed.
 */
fun buildBinaryString(builder: BinaryStringBuilder.() -> Unit): BinaryString {
    return BinaryStringBuilder().apply(builder).build()
}

/**
 * A subset view into [this] where index 0 correspends to [startIndex] and the last index corresponds to
 * [endIndex].
 */
fun BinaryString.slice(startIndex: Int = 0, endIndex: Int = size - 1): BinaryString = BinaryStringSlice(
    this,
    startIndex,
    endIndex
)

/**
 * Splits the data contained in [this] into [BinaryString] chunks of [size] bytes.
 * The final chunk of this list may be smaller than [size].
 *
 * This oprator does not allocate any new memory under the hood, and instead uses [slice]
 * to produce the chunks of data.
 */
fun BinaryString.chunked(size: Int): List<BinaryString> {
    val result = mutableListOf<BinaryString>()
    val lastIndex = if (this.size % size == 0) this.size else (this.size / size) + this.size

    for (startIndex in 0 until lastIndex step size) {
        if (startIndex > this.size) break
        val endIndex = min(startIndex + size - 1, this.size - 1)
        result.add(slice(startIndex, endIndex))
    }

    return result
}

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

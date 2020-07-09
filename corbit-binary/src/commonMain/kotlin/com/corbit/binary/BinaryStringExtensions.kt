package com.corbit.binary

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

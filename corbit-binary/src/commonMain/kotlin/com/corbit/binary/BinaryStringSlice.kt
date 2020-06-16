package com.corbit.binary

/**
 * A zero-copy view into [source] ranging from [startIndex] to [endIndex]
 */
internal class BinaryStringSlice(
    private val source: BinaryString,
    private val startIndex: Int,
    private val endIndex: Int
) : BinaryString() {

    init {
        require(startIndex >= 0) { "startIndex shouldn't be negative but was $startIndex" }
        require(startIndex <= endIndex) { "endIndex should not be less than startIndex" }
        require(endIndex < source.size) { "endIndex $endIndex is out of bounds" }
    }

    override val size: Int = endIndex - startIndex + 1

    override fun get(index: Int): Byte {
        require(index >= 0) { "index must not be negative but was $index" }
        require(index < size) { "index $index is out of bounds [0, $size)" }

        return source[index + startIndex]
    }

    override fun contains(byte: Byte): Boolean {
        for (currentByte in this) {
            if (currentByte == byte) return true
        }

        return false
    }
}

package com.corbit.binary

/**
 * A [BinaryString] wrapper for a [ByteArray]. In order to ensure immutability, [ByteArrayBinaryString] has the ability
 * to perform a defensive copy.
 */
internal class ByteArrayBinaryString(data: ByteArray, defensiveCopy: Boolean) : BinaryString() {

    private val data = if (defensiveCopy) data.copyOf() else data

    override val size = data.size

    override val utf8: String by lazy { this.data.decodeToString() }

    override fun get(index: Int): Byte = data[index]

    override fun equals(other: Any?): Boolean = when (other) {
        is ByteArrayBinaryString -> other.data.contentEquals(data)
        else -> super.equals(other)
    }

    override fun hashCode(): Int {
        if (cachedHashCode == 0) {
            cachedHashCode = data.contentHashCode()
        }

        return cachedHashCode
    }
}

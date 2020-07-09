package com.corbit.binary

import com.corbit.binary.internal.decodeBase64
import com.corbit.binary.internal.decodeHex
import com.corbit.binary.internal.encodeHex
import com.corbit.binary.internal.encodeToBase64
import com.corbit.binary.internal.sha1HashDigest

/**
 * An immutable sequence of bytes that can be read multiple times.
 */
abstract class BinaryString : Iterable<Byte> {

    protected var cachedHashCode: Int = 0

    /**
     * The size of the sequence of bytes this [BinaryString] represents
     */
    abstract val size: Int

    /**
     * This [BinaryString] encoded as a hexidecimal string.
     *
     * This calculation is lazy and will be cached after the first access to this property.
     */
    val hex: String by lazy { encodeHex() }

    /**
     * Encode this [BinaryString] as a [Base64](http://www.ietf.org/rfc/rfc2045.txt) text.
     * This implementation omits newline delimiters.
     *
     * This calculation is lazy and will be cached after the first access to this property.
     */
    val base64: String by lazy { encodeToBase64() }

    /**
     * Decode this [BinaryString] as UTF-8 text.
     *
     * This calculation is lazy and will be cached after the first access to this property.
     */
    open val utf8: String by lazy { toByteArray().decodeToString() }

    /**
     * Calculate the MD5 hash of this [BinaryString].
     *
     * This calculation is lazy and will be cached after the first access to this property.
     */
    val md5: String by lazy { TODO() }

    /**
     * Calculate the SHA-1 hash of this [BinaryString].
     *
     * This calculation is lazy and will be cached after the first access to this property.
     */
    val sha1: BinaryString by lazy { sha1HashDigest() }

    val sha256: String by lazy { TODO() }

    val sha512: String by lazy { TODO() }

    /**
     * Get a single byte at [index]. When requesting a byte at index outside of range
     * [0, size) throw [IndexOutOfBoundsException]
     */
    abstract operator fun get(index: Int): Byte

    /**
     * Returns a [ByteArray] containing a copy of the bytes contained within this [BinaryString]
     */
    fun toByteArray(): ByteArray = ByteArray(size) { get(it) }

    override fun iterator(): Iterator<Byte> = object : Iterator<Byte> {

        private var currentIndex: Int = 0

        override fun hasNext(): Boolean = currentIndex < size

        override fun next(): Byte = this@BinaryString[currentIndex++]
    }

    override fun equals(other: Any?): Boolean {
        if (other !is BinaryString) return false
        if (size != other.size) return false

        forEachIndexed { index, byte ->
            if (byte != other[index]) return false
        }

        return true
    }

    /**
     * By default, generates a copy of the underlying bytes of this [BinaryString]
     * and returns the result of that [ByteArray.contentHashCode].
     *
     * This operation is cached.
     */
    override fun hashCode(): Int {
        if (cachedHashCode == 0) {
            cachedHashCode = toByteArray().contentHashCode()
        }

        return cachedHashCode
    }

    companion object {

        /**
         * An empty [BinaryString]
         */
        val EMPTY = byteArrayOf().asBinaryString(false)

        /**
         * Decode the value represented by [hex] into a [BinaryString].
         */
        fun fromHexString(hex: String): BinaryString = hex.decodeHex()

        /**
         * Decode the Base64 encoded string [base64] into a [BinaryString] object.
         */
        fun fromBase64(base64: String): BinaryString = base64.decodeBase64()
    }
}

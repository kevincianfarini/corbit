package com.corbit.binary

/**
 * A mutable sequence of [Byte] which can be built into a [BinaryString]
 */
class BinaryStringBuilder internal constructor() {

    private val bytes = mutableListOf<Byte>()

    fun append(vararg bytes: Byte) {
        this.bytes.addAll(bytes.toTypedArray())
    }

    fun append(binary: BinaryString) {
        binary.forEach { bytes.add(it) }
    }

    fun append(string: String) {
        bytes.addAll(string.encodeToByteArray().toTypedArray())
    }

    fun build(): BinaryString = bytes.toByteArray().asBinaryString(false)
}

/**
 * Exposes access to a [BinaryStringBuilder] which is then converted to a [BinaryString]
 * after [builder] has completed.
 */
fun buildBinaryString(builder: BinaryStringBuilder.() -> Unit): BinaryString {
    return BinaryStringBuilder().apply(builder).build()
}

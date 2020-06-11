package com.corbit.bencoding

import com.corbit.binary.BinaryString
import com.corbit.binary.asBinaryString

internal enum class BencoderToken(val token: BinaryString) {
    INTEGER(byteArrayOf('i'.toByte()).asBinaryString(false)),
    STRING("0123456789".map { it.toByte() }.toByteArray().asBinaryString(false)),
    STRING_SEPARATOR(byteArrayOf(':'.toByte()).asBinaryString(false)),
    LIST(byteArrayOf('l'.toByte()).asBinaryString(false)),
    DICT(byteArrayOf('d'.toByte()).asBinaryString(false)),
    END(byteArrayOf('e'.toByte()).asBinaryString(false));

    companion object {
        fun fromToken(token: Byte): BencoderToken = when (token) {
            in INTEGER.token -> INTEGER
            in STRING.token, in STRING_SEPARATOR.token -> STRING
            in LIST.token -> LIST
            in DICT.token -> DICT
            in END.token -> END
            else -> throw IllegalArgumentException("$token is not a bencoding delimiter")
        }
    }
}

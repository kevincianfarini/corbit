package com.corbit.bencoding

import com.corbit.binary.BinaryString

fun BinaryString.decode(): BencodedData = Decoder(this).decode()

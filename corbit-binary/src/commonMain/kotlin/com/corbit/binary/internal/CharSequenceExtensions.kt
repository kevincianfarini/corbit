package com.corbit.binary.internal

internal operator fun CharSequence.component1(): Char = this[0]
internal operator fun CharSequence.component2(): Char = this[1]
internal operator fun CharSequence.component3(): Char? = getOrNull(2)
internal operator fun CharSequence.component4(): Char? = getOrNull(3)

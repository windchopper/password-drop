package com.github.windchopper.tools.password.drop.crypto

import java.security.SecureRandom
import java.util.*
import javax.crypto.spec.PBEParameterSpec

class Salt(val saltRaw: ByteArray? = ByteArray(SALT_SIZE).also(SecureRandom()::nextBytes)) {

    companion object {

        private const val SALT_SIZE = 8
        private const val ITERATION_COUNT = 1000

    }

    fun passwordBasedEncryptionParameters(): PBEParameterSpec {
        return PBEParameterSpec(saltRaw, ITERATION_COUNT)
    }

}
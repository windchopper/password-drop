package com.github.windchopper.tools.password.drop.crypto

import java.security.SecureRandom
import java.util.*
import javax.crypto.spec.PBEParameterSpec

class Salt(val saltBytes: ByteArray? = ByteArray(SALT_SIZE).also { SecureRandom().nextBytes(it) }) {

    companion object {

        private const val SALT_SIZE = 8
        private const val ITERATION_COUNT = 1000

    }

    fun passwordBasedEncryptionParameters(): PBEParameterSpec {
        return PBEParameterSpec(saltBytes, ITERATION_COUNT)
    }

    fun base64EncodedString(): String {
        return Base64.getEncoder().encodeToString(saltBytes)
    }

}
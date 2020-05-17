package com.github.windchopper.tools.password.drop.crypto

import java.security.SecureRandom
import java.util.*
import javax.crypto.spec.PBEParameterSpec
import javax.xml.bind.annotation.adapters.XmlAdapter

class EncryptSalt(val saltBytes: ByteArray? = ByteArray(SALT_SIZE).also { SecureRandom().nextBytes(it) }) {

    companion object {

        private const val SALT_SIZE = 8
        private const val ITERATION_COUNT = 1000

    }

    class XmlJavaTypeAdapter: XmlAdapter<String, EncryptSalt>() {

        @Throws(Exception::class) override fun marshal(salt: EncryptSalt): String {
            return salt.base64EncodedString()
        }

        @Throws(Exception::class) override fun unmarshal(base64EncodedSalt: String): EncryptSalt {
            return EncryptSalt(Base64.getDecoder().decode(base64EncodedSalt))
        }

    }

    fun passwordBasedEncryptionParameters(): PBEParameterSpec {
        return PBEParameterSpec(saltBytes, ITERATION_COUNT)
    }

    fun base64EncodedString(): String {
        return Base64.getEncoder().encodeToString(saltBytes)
    }

}
package com.github.windchopper.tools.password.drop.crypto

import java.security.Key
import java.security.spec.AlgorithmParameterSpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class CryptoEngine(private val password: String, private val salt: Salt) {

    private val cipher = Cipher.getInstance("PBEWithMD5AndTripleDES")

    private val key: Key by lazy {
        SecretKeyFactory.getInstance(cipher.algorithm).generateSecret(PBEKeySpec(password.toCharArray()))
    }

    private val parameters: AlgorithmParameterSpec by lazy {
        salt.passwordBasedEncryptionParameters()
    }

    fun encrypt(string: String): String {
        cipher.init(Cipher.ENCRYPT_MODE, key, parameters)
        return Base64.getEncoder().encodeToString(cipher.doFinal(string.toByteArray()))
    }

    fun decrypt(string: String): String {
        cipher.init(Cipher.DECRYPT_MODE, key, parameters)
        return String(cipher.doFinal(Base64.getDecoder().decode(string)))
    }

}
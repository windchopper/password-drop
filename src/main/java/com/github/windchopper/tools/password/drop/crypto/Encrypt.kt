package com.github.windchopper.tools.password.drop.crypto

import java.security.GeneralSecurityException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.PBEParameterSpec
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped class Encrypt(password: CharArray, salt: EncryptSalt) {

    private val cipher = Cipher.getInstance("PBEWithMD5AndTripleDES")
    private val parameters: PBEParameterSpec = salt.passwordBasedEncryptionParameters()
    private val key: SecretKey = SecretKeyFactory.getInstance(cipher.algorithm)
        .generateSecret(PBEKeySpec(password))

    @Throws(GeneralSecurityException::class) fun encrypt(string: String): String {
        cipher.init(Cipher.ENCRYPT_MODE, key, parameters)
        return Base64.getEncoder().encodeToString(cipher.doFinal(string.toByteArray()))
    }

    @Throws(GeneralSecurityException::class) fun decrypt(string: String?): String {
        cipher.init(Cipher.DECRYPT_MODE, key, parameters)
        return String(cipher.doFinal(Base64.getDecoder().decode(string)))
    }

}
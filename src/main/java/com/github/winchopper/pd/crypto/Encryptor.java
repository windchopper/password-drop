package com.github.winchopper.pd.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.GeneralSecurityException;
import java.util.Base64;

public class Encryptor {

    private final Cipher cipher = Cipher.getInstance("PBEWithMD5AndTripleDES");

    private final PBEParameterSpec parameters;
    private final SecretKey key;

    public Encryptor(char[] password, EncryptorSalt salt) throws GeneralSecurityException {
        parameters = salt.passwordBasedEncryptionParameters();
        key = SecretKeyFactory.getInstance(cipher.getAlgorithm()).generateSecret(
            new PBEKeySpec(password));
    }

    public String encrypt(String string) throws GeneralSecurityException {
        cipher.init(Cipher.ENCRYPT_MODE, key, parameters);
        return Base64.getEncoder().encodeToString(cipher.doFinal(string.getBytes()));
    }

    public String decrypt(String string) throws GeneralSecurityException {
        cipher.init(Cipher.DECRYPT_MODE, key, parameters);
        return new String(cipher.doFinal(Base64.getDecoder().decode(string)));
    }

}

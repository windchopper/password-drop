package com.github.winchopper.pd.crypto;

import javax.crypto.spec.PBEParameterSpec;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptorSalt {

    private static final int SALT_SIZE = 8;
    private static final int ITERATION_COUNT = 1000;

    private final byte[] salt;

    public EncryptorSalt(byte[] salt) {
        this.salt = salt;
    }

    public EncryptorSalt() {
        new SecureRandom().nextBytes(salt = new byte[SALT_SIZE]);
    }

    public PBEParameterSpec passwordBasedEncryptionParameters() {
        return new PBEParameterSpec(salt, ITERATION_COUNT);
    }

    public String base64EncodedString() {
        return Base64.getEncoder().encodeToString(salt);
    }

    public static class XmlJavaTypeAdapter extends XmlAdapter<String, EncryptorSalt> {

        @Override public String marshal(EncryptorSalt salt) throws Exception {
            return salt.base64EncodedString();
        }

        @Override public EncryptorSalt unmarshal(String base64EncodedSalt) throws Exception {
            return new EncryptorSalt(Base64.getDecoder().decode(base64EncodedSalt));
        }

    }

}

package com.github.windchopper.tools.password.drop.book

import com.github.windchopper.tools.password.drop.crypto.EncryptSalt
import java.util.*
import javax.xml.bind.annotation.adapters.XmlAdapter

class EncryptSaltAdapter: XmlAdapter<String?, EncryptSalt?>() {

    override fun marshal(salt: EncryptSalt?): String? {
        return salt?.base64EncodedString()
    }

    override fun unmarshal(base64EncodedSalt: String?): EncryptSalt? {
        return base64EncodedSalt?.let { EncryptSalt(Base64.getDecoder().decode(it)) }
    }

}

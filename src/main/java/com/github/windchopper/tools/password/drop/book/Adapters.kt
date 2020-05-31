package com.github.windchopper.tools.password.drop.book

import com.github.windchopper.tools.password.drop.crypto.Salt
import java.util.*
import javax.xml.bind.annotation.adapters.XmlAdapter

class EncryptSaltAdapter: XmlAdapter<String?, Salt?>() {

    override fun marshal(salt: Salt?): String? {
        return salt?.base64EncodedString()
    }

    override fun unmarshal(base64EncodedSalt: String?): Salt? {
        return base64EncodedSalt?.let { Salt(Base64.getDecoder().decode(it)) }
    }

}

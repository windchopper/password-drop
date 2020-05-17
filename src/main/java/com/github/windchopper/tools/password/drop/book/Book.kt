package com.github.windchopper.tools.password.drop.book

import com.github.windchopper.tools.password.drop.crypto.EncryptSalt
import java.util.*
import javax.xml.bind.annotation.*
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter

open class BookPart {

    @XmlAttribute var name: String? = null

    override fun toString(): String {
        return name?:"?"
    }

}

@XmlType @XmlAccessorType(XmlAccessType.FIELD) class Page: BookPart() {

    @XmlElement(name = "paragraph") var paragraphs: MutableList<Paragraph> = ArrayList()

}

@XmlType @XmlAccessorType(XmlAccessType.FIELD) class Paragraph: BookPart() {

    @XmlElement(name = "word") var words: MutableList<Word> = ArrayList()

}

@XmlType @XmlAccessorType(XmlAccessType.FIELD) class Word: BookPart() {

    @XmlValue var text: String? = null

}

@XmlType @XmlAccessorType(XmlAccessType.FIELD) class Book: BookPart() {

    @XmlAttribute(name = "salt") @XmlJavaTypeAdapter(EncryptSaltAdapter::class) var salt: EncryptSalt? = null
    @XmlElement(name = "page") var pages: MutableList<Page> = ArrayList()

}

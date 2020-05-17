package com.github.windchopper.tools.password.drop.book

import com.github.windchopper.tools.password.drop.crypto.EncryptSalt
import java.util.*
import javax.xml.bind.annotation.*
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter

open class BookPart {

    @XmlAttribute var name: String? = null

}

@XmlType @XmlAccessorType(XmlAccessType.FIELD) class Page: BookPart() {

    @XmlElement(name = "word") var paragraphs: MutableList<Paragraph> = ArrayList()

}

@XmlType @XmlAccessorType(XmlAccessType.FIELD) class Paragraph: BookPart() {

    @XmlElement(name = "word") var words: MutableList<Word> = ArrayList()

}

@XmlType @XmlAccessorType(XmlAccessType.FIELD) class Word: BookPart() {

    @XmlValue var text: String? = null

}

@XmlType @XmlAccessorType(XmlAccessType.FIELD) class Book: BookPart() {

    @XmlAttribute(name = "salt") @XmlJavaTypeAdapter(EncryptSalt.XmlJavaTypeAdapter::class) var salt: EncryptSalt? = null
    @XmlElement(name = "page") var pages: MutableList<Page> = ArrayList()

}

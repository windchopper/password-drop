package com.github.windchopper.tools.password.drop.book

import com.github.windchopper.tools.password.drop.crypto.EncryptSalt
import java.nio.file.Path
import java.util.*
import javax.xml.bind.Unmarshaller
import javax.xml.bind.annotation.*
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter

open class BookPart {

    @XmlAttribute var name: String? = null

    override fun toString(): String {
        return name?:"?"
    }

}

open class InternalBookPart<ParentType>: BookPart() where ParentType: BookPart {

    @XmlTransient var parent: ParentType? = null

}

@XmlType @XmlAccessorType(XmlAccessType.FIELD) class Page: InternalBookPart<Book>() {

    @XmlElement(name = "paragraph") var paragraphs: MutableList<Paragraph> = ArrayList()

    @Suppress("unused", "UNUSED_PARAMETER") fun afterUnmarshal(unmarshaller: Unmarshaller, parent: Any) {
        this.parent = parent as Book
    }

}

@XmlType @XmlAccessorType(XmlAccessType.FIELD) class Paragraph: InternalBookPart<Page>() {

    @XmlElement(name = "word") var phrases: MutableList<Phrase> = ArrayList()

    @Suppress("unused", "UNUSED_PARAMETER") fun afterUnmarshal(unmarshaller: Unmarshaller, parent: Any) {
        this.parent = parent as Page
    }

}

@XmlType @XmlAccessorType(XmlAccessType.FIELD) class Phrase: InternalBookPart<Paragraph>() {

    @XmlValue var text: String? = null

    @Suppress("unused", "UNUSED_PARAMETER") fun afterUnmarshal(unmarshaller: Unmarshaller, parent: Any) {
        this.parent = parent as Paragraph
    }

}

@XmlType @XmlAccessorType(XmlAccessType.FIELD) class Book: BookPart() {

    @XmlTransient var path: Path? = null

    @XmlAttribute(name = "salt") @XmlJavaTypeAdapter(EncryptSaltAdapter::class) var salt: EncryptSalt? = null
    @XmlElement(name = "page") var pages: MutableList<Page> = ArrayList()

    fun copy(textHandler: (String?) -> String? = { it }): Book {
        return Book().also { newBook ->
            newBook.name = name
            newBook.path = path
            newBook.salt = salt

            pages.forEach { page ->
                newBook.pages.add(Page().also { newPage ->
                    newPage.parent = newBook
                    newPage.name = page.name

                    page.paragraphs.forEach { paragraph ->
                        newPage.paragraphs.add(Paragraph().also { newParagraph ->
                            newParagraph.parent = newPage
                            newParagraph.name = paragraph.name

                            paragraph.phrases.forEach { phrase ->
                                newParagraph.phrases.add(Phrase().also { newPhrase ->
                                    newPhrase.parent = newParagraph
                                    newPhrase.name = phrase.name
                                    newPhrase.text = textHandler.invoke(phrase.text)
                                })
                            }
                        })
                    }
                })
            }
        }
    }

}

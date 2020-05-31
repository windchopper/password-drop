package com.github.windchopper.tools.password.drop.book

import com.github.windchopper.tools.password.drop.Application
import com.github.windchopper.tools.password.drop.crypto.Salt
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

    fun newParagraph(): Paragraph {
        return Paragraph().also(paragraphs::add).also {
            it.name = Application.messages["paragraph.unnamed"]
            it.parent = this
        }
    }

    @Suppress("unused", "UNUSED_PARAMETER") fun afterUnmarshal(unmarshaller: Unmarshaller, parent: Any) {
        this.parent = parent as Book
    }

}

@XmlType @XmlAccessorType(XmlAccessType.FIELD) class Paragraph: InternalBookPart<Page>() {

    @XmlElement(name = "word") var phrases: MutableList<Phrase> = ArrayList()

    @Suppress("unused", "UNUSED_PARAMETER") fun afterUnmarshal(unmarshaller: Unmarshaller, parent: Any) {
        this.parent = parent as Page
    }

    fun newPhrase(): Phrase {
        return Phrase().also(phrases::add).also {
            it.name = Application.messages["phrase.unnamed"]
            it.parent = this
        }
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

    @XmlAttribute(name = "salt") @XmlJavaTypeAdapter(SaltAdapter::class) var salt: Salt? = null
    @XmlElement(name = "page") var pages: MutableList<Page> = ArrayList()

    fun newPage(): Page {
        return Page().also(pages::add).also {
            it.name = Application.messages["page.unnamed"]
            it.parent = this
        }
    }

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

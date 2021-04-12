@file:Suppress("unused", "UNUSED_PARAMETER")

package com.github.windchopper.tools.password.drop.book

import com.github.windchopper.tools.password.drop.Application
import com.github.windchopper.tools.password.drop.crypto.CryptoEngine
import jakarta.xml.bind.Unmarshaller
import jakarta.xml.bind.annotation.*
import java.nio.file.Path
import java.util.*

@XmlAccessorType(XmlAccessType.FIELD) abstract class BookPart {

    @XmlAttribute var name: String? = null
    abstract val type: String?

    override fun toString(): String {
        return name?:"?"
    }

}

abstract class InternalBookPart<ParentType>: BookPart() where ParentType: BookPart {

    @XmlTransient var parent: ParentType? = null

    abstract fun removeFromParent()

}

@XmlType @XmlAccessorType(XmlAccessType.FIELD) class Page: InternalBookPart<Book>() {

    @XmlTransient override val type: String? = Application.messages["page.type"]

    @XmlElement(name = "paragraph") var paragraphs: MutableList<Paragraph> = ArrayList()

    fun newParagraph(): Paragraph {
        return Paragraph()
            .also {
                paragraphs.add(it)
                it.name = Application.messages["paragraph.unnamed"]
                it.parent = this
            }
    }

    fun afterUnmarshal(unmarshaller: Unmarshaller, parent: Any) {
        this.parent = parent as Book
    }

    override fun removeFromParent() {
        parent?.pages?.remove(this)
    }

}

@XmlType @XmlAccessorType(XmlAccessType.FIELD) class Paragraph: InternalBookPart<Page>() {

    @XmlTransient override val type: String? = Application.messages["paragraph.type"]

    @XmlElement(name = "word") var phrases: MutableList<Phrase> = ArrayList()

    fun afterUnmarshal(unmarshaller: Unmarshaller, parent: Any) {
        this.parent = parent as Page
    }

    fun newPhrase(): Phrase {
        return Phrase()
            .also {
                phrases.add(it)
                it.name = Application.messages["phrase.unnamed"]
                it.parent = this
            }
    }

    override fun removeFromParent() {
        parent?.paragraphs?.remove(this)
    }

}

@XmlType @XmlAccessorType(XmlAccessType.FIELD) class Phrase: InternalBookPart<Paragraph>() {

    companion object {

        const val ENCRYPTED_PREFIX = "{ENCRYPTED}"

    }

    @XmlTransient override val type: String? = Application.messages["phrase.type"]

    @XmlElement var text: String? = null
        get() = field?.let {
            return field
                ?.let { text ->
                    val cleanText = if (text.startsWith(ENCRYPTED_PREFIX)) {
                        text.substring(ENCRYPTED_PREFIX.length)
                    } else {
                        text
                    }

                    parent?.parent?.parent?.cryptoEngine
                        ?.decrypt(cleanText)
                        ?:throw RuntimeException(Application.messages["error.couldNotDecrypt"])
                }
        }
        set(value) {
            field = value
                ?.let { text ->
                    parent?.parent?.parent?.cryptoEngine
                        ?.let { encryptEngine ->
                            ENCRYPTED_PREFIX + encryptEngine.encrypt(text)
                        }
                        ?:text
                }
        }

    fun afterUnmarshal(unmarshaller: Unmarshaller, parent: Any) {
        this.parent = parent as Paragraph
    }

    override fun removeFromParent() {
        parent?.phrases?.remove(this)
    }

}

@XmlType @XmlRootElement @XmlAccessorType(XmlAccessType.FIELD) open class Book: BookPart() {

    @XmlTransient override val type: String? = Application.messages["book.type"]

    @XmlAttribute(name = "salt") @XmlSchemaType(name = "base64Binary") var saltRaw: ByteArray? = null
    @XmlElement(name = "page") var pages: MutableList<Page> = ArrayList()

    @XmlTransient var path: Path? = null
    @XmlTransient var cryptoEngine: CryptoEngine? = null
    @XmlTransient var dirty: Boolean = false

    fun newPage(): Page {
        return Page()
            .also {
                pages.add(it)
                it.name = Application.messages["page.unnamed"]
                it.parent = this
            }
    }

    open fun copy(bookSupplier: () -> Book): Book {
        return bookSupplier.invoke().also { newBook ->
            newBook.name = name
            newBook.saltRaw = saltRaw
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
                                    newPhrase.text = phrase.text
                                })
                            }
                        })
                    }
                })
            }
        }
    }

}

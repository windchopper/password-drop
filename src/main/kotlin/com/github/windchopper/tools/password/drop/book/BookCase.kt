@file:Suppress("NestedLambdaShadowedImplicitParameter")

package com.github.windchopper.tools.password.drop.book

import jakarta.enterprise.context.ApplicationScoped
import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.Marshaller
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

@ApplicationScoped class BookCase {

    private val bindingContext: JAXBContext = JAXBContext.newInstance(Book::class.java)

    fun readBook(bookPath: Path): Book {
        Files.newBufferedReader(bookPath).use {
            return (bindingContext.createUnmarshaller().unmarshal(it) as Book)
                .also { it.path = bookPath }
        }
    }

    fun saveBook(book: Book, bookPath: Path) {
        val temporaryBookPath = Files.createTempFile("password-drop-book-", "")

        Files.newBufferedWriter(temporaryBookPath).use {
            bindingContext.createMarshaller()
                .also { it.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true) }
                .marshal(book, it)
        }

        book.path = Files.move(temporaryBookPath, bookPath,
            StandardCopyOption.REPLACE_EXISTING)
    }

}
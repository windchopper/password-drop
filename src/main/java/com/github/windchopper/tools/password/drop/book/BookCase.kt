package com.github.windchopper.tools.password.drop.book

import java.nio.file.Files
import java.nio.file.Path
import javax.enterprise.context.ApplicationScoped
import javax.xml.bind.JAXBContext

@ApplicationScoped class BookCase {

    private val bindingContext: JAXBContext = JAXBContext.newInstance(Book::class.java)

    fun readBook(bookPath: Path): Book {
        Files.newBufferedReader(bookPath).use {
            return bindingContext.createUnmarshaller().unmarshal(it) as Book
        }
    }

}